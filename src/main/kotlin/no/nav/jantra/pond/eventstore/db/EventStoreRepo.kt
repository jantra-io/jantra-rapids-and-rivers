package no.nav.jantra.pond.eventstore.db

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageProblems
import no.nav.jantra.river.Key
import no.nav.jantra.river.MessageType
import no.nav.jantra.river.interestedIn
import no.nav.jantra.river.model.Behov
import no.nav.jantra.river.model.Event
import no.nav.jantra.river.model.Fail
import no.nav.jantra.river.model.Message
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class EventStoreRepo(val database: Database) {

    val riverEventRepo = RiverEventRepo(database)

    fun put(appKey: String, event: Event) {
        transaction(database.db) {
            EVENTSTORE_TABLE.insert {
                it[id] = event.toJsonMessage().id
                it[EVENTSTORE_TABLE.appKey] = appKey
                it[eventName] = event.event.value
                it[originRiver] = event[Key.RIVER_ORIGIN].asText()
                it[eventTime] = LocalDateTime.parse(event[Key.EVENT_TIME].asText())
                it[message] = event.toJsonMessage().toJson()
            }
        }
    }


    fun getByAppKey(appKey: String): Event {
        val row = transaction(database.db) {
            EVENTSTORE_TABLE.select {
                EVENTSTORE_TABLE.appKey.eq(appKey)
            }.first()
        }
        val jsonMessage = JsonMessage(row[EVENTSTORE_TABLE.message], MessageProblems(row[EVENTSTORE_TABLE.message]))
        return jsonMessage
            .also {
                Event.packetValidator.validate(it)
            }
            .let {
                Event.create(it)
            }
    }

    fun get(key: String): Event {
        val row = transaction(database.db) {
            EVENTSTORE_TABLE.select {
                EVENTSTORE_TABLE.id.eq(key)
            }.first()
        }
        val jsonMessage = JsonMessage(row[EVENTSTORE_TABLE.message], MessageProblems(row[EVENTSTORE_TABLE.message]))
        return jsonMessage
            .also {
                Event.packetValidator.validate(it)
            }
            .let {
                Event.create(it)
            }
    }

    fun findEventByAppkeyAndType(appKey: String,eventName:MessageType): List<Event> {
             val rows = transaction(database.db) {
            EVENTSTORE_TABLE.select {
                EVENTSTORE_TABLE.appKey.eq(appKey).and(EVENTSTORE_TABLE.eventName.eq(eventName.value))
            }.toList()
        }
        val jsonMessage =
        return rows.map { JsonMessage(it[EVENTSTORE_TABLE.message], MessageProblems(it[EVENTSTORE_TABLE.message])) }
                .map {
                it
                .also {
                    Event.packetValidator.validate(it)
                }
                .let {
                    Event.create(it)
                }
            }.toList()

    }

    fun findOriginEvent(event: Event): Event {

        val outerquery = EVENTSTORE_TABLE.join(RIVER_EVENT, JoinType.INNER, EVENTSTORE_TABLE.id, RIVER_EVENT.eventId)
            .select(EVENTSTORE_TABLE.id, RIVER_EVENT.riverId).alias("outerselect")
        val eventKey = transaction(database.db) {
            outerquery.select(outerquery.get(EVENTSTORE_TABLE.id)).where {
                exists(
                    EVENTSTORE_TABLE.join(
                        RIVER_TABLE,
                        JoinType.INNER,
                        EVENTSTORE_TABLE.originRiver,
                        RIVER_TABLE.riverId
                    ).selectAll().where(
                        EVENTSTORE_TABLE.eventName.eq(event.event.value)
                            .and(outerquery.get(RIVER_EVENT.riverId).eq(RIVER_TABLE.riverId))
                    )
                )
            }.distinct().toList().first()
        }

        return get(eventKey[outerquery[EVENTSTORE_TABLE.id]])
    }

    fun finishedRivers(event: Event): Map<String, List<Message>> {
        val riverIds = riverEventRepo.getRivers(event)

        val downstreamEventMap: Map<String, List<Message>> = transaction(
            database.db
        ) {
            EVENTSTORE_TABLE.select(EVENTSTORE_TABLE.originRiver, EVENTSTORE_TABLE.message).where {
                EVENTSTORE_TABLE.originRiver.inList(
                    riverIds
                )
            }.groupByTo(
                mutableMapOf()
            ) {
                it[EVENTSTORE_TABLE.originRiver]
            }.toMap().mapValues {
                val messages = it.value.map {
                    val message= it[EVENTSTORE_TABLE.message]
                    val jsonMessage = JsonMessage(message, MessageProblems(message))
                    Event.packetValidator.validate(jsonMessage)
                    jsonMessage
                }.map {
                    Event.create(it)
                }.toList()
                messages
            }.toMap()
        }

        val finishedRiverIds = downstreamEventMap.map { it.key }.distinct()
        val finishedRivers = transaction(database.db) {
            RIVER_TABLE
                .select(RIVER_TABLE.message, RIVER_TABLE.riverId)
                .where {
                    RIVER_TABLE.riverId.inList(finishedRiverIds)
                }.groupBy({
                    it[RIVER_TABLE.riverId]
                }, {
                    val message = it[RIVER_TABLE.message]
                    val jsonMessage = JsonMessage(message, MessageProblems(message))
                    jsonMessage.interestedIn(Key.EVENT_NAME)
                    jsonMessage.interestedIn(Key.BEHOV)
                    jsonMessage.interestedIn(Key.FAIL)
                    if (jsonMessage[Key.FAIL.str()].isMissingNode) {
                        Behov.create(jsonMessage) as Message
                    } else {
                        Fail.create(jsonMessage) as Message
                    }
                }).toMutableMap()
        }

        return finishedRivers.toMutableMap().apply {

            downstreamEventMap.forEach() {
                if (!this[it.key].isNullOrEmpty()) {
                    this.put(it.key, this[it.key]!!.toMutableList().apply { addAll(it.value) })
                }
            }
        }
    }


}
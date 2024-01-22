package no.nav.reka.pond.eventstore.db

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageProblems
import no.nav.reka.river.Key
import no.nav.reka.river.MessageType
import no.nav.reka.river.model.Event
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class EventStoreRepo(val database: Database) {

    fun put(appKey:String,event: Event) {
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



    fun get(appKey: String): Event {
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

    fun findUpstreamEvent(appKey: String, event: MessageType.Event ) : List<Event> {

        val row = transaction (database.db) {
            EVENTSTORE_TABLE.join(RIVER_TABLE, JoinType.INNER, EVENTSTORE_TABLE.originRiver,RIVER_TABLE.riverId)
                .selectAll().where(EVENTSTORE_TABLE.eventName.eq(event.value)).toList()
        }

        val outerquery = EVENTSTORE_TABLE.join(RIVER_EVENT,JoinType.INNER,EVENTSTORE_TABLE.id,RIVER_EVENT.eventId).select(EVENTSTORE_TABLE.id,RIVER_EVENT.riverId).alias("outerselect")
        val outerRows = transaction(database.db) { outerquery.selectAll().toList() }
        println(outerRows)
        /*
        val riverId = outerRows.first().get(RIVER_EVENT.riverId)
        row.forEach {
            if(it[RIVER_TABLE.riverId] == riverId) {
                println("Hello")
            }
        }*/
        val rows = transaction (database.db) { outerquery.select(outerquery.get(EVENTSTORE_TABLE.id)).where {  exists(EVENTSTORE_TABLE.join(RIVER_TABLE, JoinType.INNER, EVENTSTORE_TABLE.originRiver,RIVER_TABLE.riverId).selectAll().where(EVENTSTORE_TABLE.eventName.eq(event.value).and(outerquery.get(RIVER_EVENT.riverId).eq(RIVER_TABLE.riverId)))) }.toList() }
        println(rows)
        val message = row.first()[RIVER_TABLE.message]
        print(message)
        return emptyList()
    }


}
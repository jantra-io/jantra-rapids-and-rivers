package no.nav.reka.pond.eventstore.db

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageProblems
import no.nav.reka.river.Key
import no.nav.reka.river.model.Behov
import no.nav.reka.river.model.Event
import no.nav.reka.river.model.Fail
import no.nav.reka.river.model.Message
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.IllegalArgumentException
import java.time.LocalDateTime

class RiverRepo(val database: Database) {

    fun put(behov: Behov) {
        transaction(database.db) {
            RIVER_TABLE.insert {
                it[riverId] = behov.riverId()
                it[eventName] = behov.event.value
                it[behovName] = behov.behov.value
                it[isFail] = false
                it[eventTime] = LocalDateTime.parse(behov[Key.EVENT_TIME].asText())
                it[message] = behov.toJsonMessage().toJson()
            }
        }
    }

     fun put(fail: Fail) {
         transaction(database.db) {
             RIVER_TABLE.insert {
                 it[riverId] = fail.riverId()
                 it[eventName] = fail.event.value
                 it[behovName] = fail.behov?.value
                 it[isFail] = false
                 it[eventTime] = LocalDateTime.parse(fail[Key.EVENT_TIME].asText())
                 it[message] = fail.toJsonMessage().toJson()
             }
         }
    }

     fun createRiver(event:Behov) {
        transaction (database.db) {
            RIVER_EVENT.insert {
                it[eventId] = event[Key.EVENT_ORIGIN].asText()
                it[riverId] = event[Key.RIVER_ID].asText()
            }
        }

    }

    fun get(riverId:String): Message? {
        val row = transaction (database.db) {
            RIVER_TABLE.select {
                RIVER_TABLE.riverId.eq(riverId)
            }
        }.first()
        val jsonMessage = JsonMessage(row[RIVER_TABLE.message], MessageProblems(row[RIVER_TABLE.message]))
        if(row[RIVER_TABLE.isFail]) {
            return  jsonMessage.also { Fail.packetValidator.validate(it) }.let {   Fail.create(jsonMessage)}
        }
        else if (row[RIVER_TABLE.behovName] !=null ) {
            return jsonMessage.also { Behov.packetValidator.validate(it) }.let { Behov.create(jsonMessage) }
        }
        else if (row[RIVER_TABLE.eventName].isNullOrBlank()) {
            return jsonMessage.also { Event.packetValidator.validate(it) }.let { Event.create(jsonMessage) }
        }
        else {
            throw IllegalArgumentException("at least one of event or behov should be defined")
        }
    }


}
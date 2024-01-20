package no.nav.reka.pond.eventstore.db

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageProblems
import no.nav.reka.river.Key
import no.nav.reka.river.model.Behov
import no.nav.reka.river.model.Event
import no.nav.reka.river.model.Fail
import no.nav.reka.river.model.Message
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.IllegalArgumentException
import java.time.LocalDateTime

class EventStoreRepo(val database: Database) {

    fun put(appKey:String,behov: Event) {
        transaction(database.db) {
            EVENTSTORE_TABLE.insert {
                it[EVENTSTORE_TABLE.appKey] = appKey
                it[eventName] = behov.event.value
                it[eventTime] = LocalDateTime.parse(behov[Key.EVENT_TIME].asText())
                it[message] = behov.toJsonMessage().toJson()
            }
        }
    }

    fun get(appKey: String): Event {
        val row = transaction {
            EVENTSTORE_TABLE.select {
                EVENTSTORE_TABLE.appKey.eq(appKey)
            }
        }.first()
        val jsonMessage = JsonMessage(row[EVENTSTORE_TABLE.message], MessageProblems(row[EVENTSTORE_TABLE.message]))
        return Event.create(jsonMessage)

    }



}
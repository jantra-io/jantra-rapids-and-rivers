package no.nav.jantra.pond.eventstore.db

import no.nav.jantra.river.model.Event
import org.jetbrains.exposed.sql.transactions.transaction

class RiverEventRepo(val database: Database) {


    fun getRivers(event: Event): List<String> {
        return transaction(database.db) {
            RIVER_EVENT.select(RIVER_EVENT.riverId).where { RIVER_EVENT.eventId.eq(event.toJsonMessage().id) }.toList().map { it[RIVER_EVENT.riverId] }.toList()
        }
    }

}
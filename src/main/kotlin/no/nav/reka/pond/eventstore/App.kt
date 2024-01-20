package no.nav.reka.pond.eventstore

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.pond.eventstore.db.Database
import no.nav.reka.pond.eventstore.db.EventStoreRepo
import no.nav.reka.pond.eventstore.db.RiverRepo
import no.nav.reka.river.Key
import no.nav.reka.river.bridge.BehovRiver
import no.nav.reka.river.bridge.EventRiver
import no.nav.reka.river.bridge.FailRiver
import no.nav.reka.river.model.Event


fun RapidsConnection.configureEventStore(database: Database) {
    val eventStoreRepo = EventStoreRepo(database)
    val riverStoreRepo = RiverRepo(database)

    EventRiver(this, EventScrapper(eventStoreRepo) {
        event: Event -> event[Key.APP_KEY].asText()
    },{}).start()
    BehovRiver(this, BehovScrapper(riverStoreRepo),{}).start()
    FailRiver(this, FailScrapper(riverStoreRepo),{}).start()

}
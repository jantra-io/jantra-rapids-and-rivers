package no.nav.jantra.pond.eventstore

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.jantra.pond.eventstore.db.Database
import no.nav.jantra.pond.eventstore.db.EventStoreRepo
import no.nav.jantra.pond.eventstore.db.RiverRepo
import no.nav.jantra.river.Key
import no.nav.jantra.river.bridge.BehovRiver
import no.nav.jantra.river.bridge.FailRiver
import no.nav.jantra.river.model.Event


fun RapidsConnection.configureEventStore(database: Database): RapidsConnection {
    val eventStoreRepo = EventStoreRepo(database)
    val riverStoreRepo = RiverRepo(database)

    EventScrapper(this, eventStoreRepo) {
        event: Event -> event[Key.APP_KEY].asText()
    }.start()
    BehovRiver(this, BehovScrapper(riverStoreRepo)) {}.start()
    FailRiver(this, FailScrapper(riverStoreRepo),{}).start()
    return this

}
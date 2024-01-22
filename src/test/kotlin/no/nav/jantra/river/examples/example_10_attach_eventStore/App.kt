package no.nav.jantra.river.examples.example_10_attach_eventStore

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.jantra.pond.eventstore.configureEventStore
import no.nav.jantra.pond.eventstore.db.Database
import no.nav.jantra.river.examples.example_3_event_triger_2_behov_emitting_event.`setup EventTriggering 2 Behov And Emitting Event`


fun RapidsConnection.eventStoreExample(database: Database):RapidsConnection {
        this.configureEventStore(database)
        this.`setup EventTriggering 2 Behov And Emitting Event`()
        return this
}
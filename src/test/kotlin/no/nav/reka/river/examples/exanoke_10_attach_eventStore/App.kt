package no.nav.reka.river.examples.exanoke_10_attach_eventStore

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.pond.eventstore.configureEventStore
import no.nav.reka.pond.eventstore.db.Database
import no.nav.reka.river.examples.example_3_event_triger_2_behov_emitting_event.`setup EventTriggering 2 Behov And Emitting Event`
import no.nav.reka.river.examples.example_8_retrieving_data_from_client.`client retrieving data`


fun RapidsConnection.eventStoreExample(database: Database):RapidsConnection {
        this.configureEventStore(database)
        this.`setup EventTriggering 2 Behov And Emitting Event`()
        return this
}
package no.nav.jantra.river.examples.example_11_spawn_2_rivers

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.jantra.pond.eventstore.configureEventStore
import no.nav.jantra.pond.eventstore.db.Database
import no.nav.jantra.river.examples.example_3_event_triger_2_behov_emitting_event.`setup EventTriggering 2 Behov And Emitting Event`
import no.nav.jantra.river.examples.example_4_reacting_to_data_on_any_event.buildReactingToData

fun RapidsConnection.multipleRiversExample(database: Database): RapidsConnection {
        this.configureEventStore(database)
        this.`setup EventTriggering 2 Behov And Emitting Event`()
        this.buildReactingToData()
        return this
}
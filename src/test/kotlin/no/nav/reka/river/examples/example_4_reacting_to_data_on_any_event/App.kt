package no.nav.reka.river.examples.example_4_reacting_to_data_on_any_event

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.examples.example_4_reacting_to_data_on_any_event.services.DocumentRecievedListener
import no.nav.reka.river.examples.example_4_reacting_to_data_on_any_event.services.JSONFormater
import no.nav.reka.river.examples.example_4_reacting_to_data_on_any_event.services.PersistDocument
import no.nav.reka.river.examples.example_4_reacting_to_data_on_any_event.services.XMLFormater
import no.nav.reka.river.redis.RedisStore


    fun RapidsConnection.buildReactingToData(): RapidsConnection {
        PersistDocument(this).start()
        DocumentRecievedListener(this).start()
        JSONFormater(this).start()
        XMLFormater(this).start()
        return this
    }

package no.nav.reka.river.examples.dsltest

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.examples.basic_consumer.EventName
import no.nav.reka.river.configuration.dsl.listener
import no.nav.reka.river.configuration.dsl.topology
import no.nav.reka.river.examples.basic_consumer.DataFelt
import no.nav.reka.river.examples.composite_med_fail_listener.services.DocumentRecievedListener


fun RapidsConnection.`testDsl`(): RapidsConnection {
    val listenerImpl = DocumentRecievedListener(this)
    listener(this) {
        event = EventName.DOCUMENT_RECIEVED
        eventListener {
            implementation = listenerImpl
            accepts {
                it.demandKey(DataFelt.RAW_DOCUMENT.str)
            }
        }
        failListener {
            implementation = listenerImpl
            accepts {
                it.demandKey(DataFelt.RAW_DOCUMENT.str)
            }
        }


    }.start()

    topology(this) {
        composition {
            eventListener {
                event = EventName.DOCUMENT_RECIEVED
                implementation = listenerImpl
            }

        }
    }

    return this
}
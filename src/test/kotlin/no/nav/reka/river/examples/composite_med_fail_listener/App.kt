package no.nav.reka.river.examples.composite_med_fail_listener

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.examples.basic_consumer.EventName
import no.nav.reka.river.examples.composite_med_fail_listener.services.DocumentRecievedListener
import no.nav.reka.river.newtest.ListenerBuilder


fun RapidsConnection.`setup EventListener reacting to Failure`(): RapidsConnection {
    val documentListener = DocumentRecievedListener(this)
    ListenerBuilder(this)
            .eventListener(EventName.DOCUMENT_RECIEVED)
            .implementation(documentListener)
            .build()
        .failListener()
            .implementation(documentListener)
            .build()
        .start()
    return this
}
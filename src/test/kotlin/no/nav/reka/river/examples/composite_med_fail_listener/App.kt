package no.nav.reka.river.examples.composite_med_fail_listener

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.examples.basic_consumer.EventName
import no.nav.reka.river.examples.composite_med_fail_listener.services.DocumentRecievedListener
import no.nav.reka.river.bridge.ListenerBuilder
import no.nav.reka.river.examples.basic_consumer.DataFelt
import no.nav.reka.river.examples.capture_fail_from_listener.services.FormatDokumentService
import no.nav.reka.river.examples.capture_fail_from_listener.services.LegacyIBMFormatter
import no.nav.reka.river.examples.capture_fail_from_listener.services.PersistDocument
import no.nav.reka.river.examples.capture_fail_from_listener.`setup EventListener reacting to Failure`
import no.nav.reka.river.interestedIn


fun RapidsConnection.`setup EventListener reacting to Failure`(): RapidsConnection {
    val documentListener = DocumentRecievedListener(this)
    ListenerBuilder(this)
            .eventListener(EventName.DOCUMENT_RECIEVED)
            .implementation(documentListener)
            .build()
        .failListener()
            .implementation(documentListener)
            .accept {
                it.interestedIn(DataFelt.RAW_DOCUMENT)
            }
            .build()
        .start()
    FormatDokumentService(this).start()
    LegacyIBMFormatter(this).start()
    PersistDocument(this).start()
    return this
}
package no.nav.reka.river.examples.example_6_composite_med_fail_listener

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.examples.example_1_basic_løser.EventName
import no.nav.reka.river.examples.example_6_composite_med_fail_listener.services.DocumentRecievedListener
import no.nav.reka.river.configuration.ListenerBuilder
import no.nav.reka.river.examples.example_1_basic_løser.DataFelt
import no.nav.reka.river.examples.example_5_capture_fail_from_listener.services.FormatDokumentService
import no.nav.reka.river.examples.example_5_capture_fail_from_listener.services.LegacyIBMFormatter
import no.nav.reka.river.examples.example_5_capture_fail_from_listener.services.PersistDocument
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
    /*
    FormatDokumentService(this).start()
    LegacyIBMFormatter(this).start()
    PersistDocument(this).start()
    */
    return this

}
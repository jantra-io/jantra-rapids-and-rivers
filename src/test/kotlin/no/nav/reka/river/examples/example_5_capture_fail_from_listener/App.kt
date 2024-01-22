package no.nav.reka.river.examples.example_5_capture_fail_from_listener

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.Key
import no.nav.reka.river.examples.example_1_basic_løser.DataFelt
import no.nav.reka.river.examples.example_1_basic_løser.EventName
import no.nav.reka.river.examples.example_5_capture_fail_from_listener.services.DocumentRecievedListener
import no.nav.reka.river.examples.example_5_capture_fail_from_listener.services.FormatDokumentService
import no.nav.reka.river.examples.example_5_capture_fail_from_listener.services.LegacyIBMFormatter
import no.nav.reka.river.examples.example_5_capture_fail_from_listener.services.PersistDocument
import no.nav.reka.river.interestedIn
import no.nav.reka.river.configuration.ListenerBuilder
import no.nav.reka.river.configuration.dsl.composition
import no.nav.reka.river.demandValue
import no.nav.reka.river.examples.example_1_basic_løser.BehovName

fun RapidsConnection.`setup EventListener reacting to Failure`(): RapidsConnection {
    val documentRecieved = DocumentRecievedListener(this)

    composition("Provides document formatting capability",this) {
        eventListener(EventName.DOCUMENT_RECIEVED){
            implementation = documentRecieved
            accepts {
                 it.interestedIn(DataFelt.RAW_DOCUMENT)
            }
        }
        løser(BehovName.FORMAT_DOCUMENT) {
            implementation = FormatDokumentService(this@`setup EventListener reacting to Failure`)
            accepts {
                it.interestedIn(DataFelt.RAW_DOCUMENT)
                it.interestedIn(DataFelt.RAW_DOCUMENT_FORMAT)
            }
        }
        løser(BehovName.FORMAT_DOCUMENT_IBM) {
            implementation = LegacyIBMFormatter(this@`setup EventListener reacting to Failure`)
            accepts {
                 it.interestedIn(DataFelt.RAW_DOCUMENT)
            it.interestedIn(DataFelt.RAW_DOCUMENT_FORMAT)
            }
        }
        løser(BehovName.PERSIST_DOCUMENT) {
            implementation = PersistDocument(this@`setup EventListener reacting to Failure`)
            accepts {
                it.interestedIn(DataFelt.FORMATED_DOCUMENT)
            }
        }
        failListener {
            implementation = documentRecieved
            accepts {
                it.interestedIn(DataFelt.RAW_DOCUMENT)
            }
        }
    }.start()

    return this
}
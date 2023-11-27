package no.nav.reka.river.examples.dsltest

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.IFailListener
import no.nav.reka.river.Key
import no.nav.reka.river.examples.example_1_basic_løser.EventName
import no.nav.reka.river.configuration.dsl.topology
import no.nav.reka.river.demandValue
import no.nav.reka.river.examples.example_1_basic_løser.BehovName
import no.nav.reka.river.examples.example_1_basic_løser.DataFelt
import no.nav.reka.river.examples.dsltest.services.DocumentRecievedListener
import no.nav.reka.river.examples.dsltest.services.FormatDokumentService
import no.nav.reka.river.examples.dsltest.services.LegacyIBMFormatter
import no.nav.reka.river.examples.dsltest.services.PersistDocument
import no.nav.reka.river.interestedIn


fun RapidsConnection.`testDsl`(): RapidsConnection {
    val listenerImpl = DocumentRecievedListener(this)


    topology(this) {
        composition {
            eventListener(EventName.DOCUMENT_RECIEVED) {
                accepts {
                    it.interestedIn(DataFelt.RAW_DOCUMENT)
                }
                implementation = listenerImpl
                løser(BehovName.FORMAT_DOCUMENT) {
                    accepts {
                        it.interestedIn(DataFelt.RAW_DOCUMENT)
                        it.interestedIn(DataFelt.RAW_DOCUMENT_FORMAT)
                    }
                    implementation = FormatDokumentService(this@testDsl)
                }
                løser(BehovName.FORMAT_DOCUMENT_IBM) {
                    accepts {
                        it.interestedIn(DataFelt.RAW_DOCUMENT)
                        it.interestedIn(DataFelt.RAW_DOCUMENT_FORMAT)
                    }
                    implementation = LegacyIBMFormatter(this@testDsl)
                }
                løser(BehovName.PERSIST_DOCUMENT) {
                     accepts {
                        it.interestedIn(DataFelt.FORMATED_DOCUMENT)
                         it.interestedIn(DataFelt.FORMATED_DOCUMENT_IBM)
                    }
                    implementation = PersistDocument(this@testDsl)
                }


            }

        }
    }.start()

    fun RapidsConnection.`empty`(): RapidsConnection {
        return this
    }
/*
    saga(this,RedisStore("test")) {
        event(EventName.DOCUMENT_RECIEVED) {
            capture { DataFelt.RAW_DOCUMENT
                                 DataFelt.FORMATED_DOCUMENT
                                }
        }
    }
*/
    /*
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
    */



    return this
}
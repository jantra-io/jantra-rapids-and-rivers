package no.nav.jantra.river.examples.example_8_retrieving_data_from_client

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.jantra.river.Key
import no.nav.jantra.river.configuration.ListenerBuilder
import no.nav.jantra.river.configuration.dsl.topology
import no.nav.jantra.river.demandValue
import no.nav.jantra.river.examples.example_1_basic_løser.BehovName
import no.nav.jantra.river.examples.example_1_basic_løser.DataFelt
import no.nav.jantra.river.examples.example_1_basic_løser.EventName
import no.nav.jantra.river.examples.example_7_simple_saga.services.FormatDokumentService
import no.nav.jantra.river.examples.example_7_simple_saga.services.LegacyIBMFormatter
import no.nav.jantra.river.examples.example_8_retrieving_data_from_client.services.ApplicationRecievedListener
import no.nav.jantra.river.examples.example_8_retrieving_data_from_client.services.DocumentFormatingSaga
import no.nav.jantra.river.examples.example_8_retrieving_data_from_client.services.PersistDocument
import no.nav.jantra.river.interestedIn

import no.nav.jantra.river.redis.RedisStore



fun RapidsConnection.`client retrieving data`(redisStore: RedisStore): RapidsConnection {
    val applicationRecievedListener = ApplicationRecievedListener(this, redisStore)
    ListenerBuilder(this)
        .eventListener(EventName.APPLICATION_RECIEVED)
        .implementation(applicationRecievedListener)
        .accept {
            it.interestedIn(DataFelt.FORMATED_DOCUMENT)
        }
        .build()
        .dataListener()
        .implementation(applicationRecievedListener)
        .accept {
            it.interestedIn(DataFelt.DOCUMENT_REFERECE)
        }
        .build()
        .failListener()
        .implementation(applicationRecievedListener)
        .build()
        .start()
    PersistDocument(this).start()


    return this
}

fun RapidsConnection.`client retrieving data SAGA`(redisStore: RedisStore): RapidsConnection {

     topology(this) {
        saga("My saga",redisStore) {
            implementation(DocumentFormatingSaga(EventName.DOCUMENT_RECIEVED))
            eventListener(EventName.DOCUMENT_RECIEVED) {
                this.capture(DataFelt.RAW_DOCUMENT)
            }
            dataListener(
                        DataFelt.FORMATED_DOCUMENT,
                        DataFelt.FORMATED_DOCUMENT_IBM,
                        DataFelt.DOCUMENT_REFERECE
            )

            løser(BehovName.FORMAT_DOCUMENT) {
                implementation = FormatDokumentService(this@`client retrieving data SAGA`)
                accepts {
                     it.demandValue(Key.EVENT_NAME, EventName.DOCUMENT_RECIEVED)
                     it.demandValue(Key.BEHOV, BehovName.FORMAT_DOCUMENT)
                     it.interestedIn(DataFelt.RAW_DOCUMENT)
                }
            }
            løser(BehovName.FORMAT_DOCUMENT_IBM) {
                implementation = LegacyIBMFormatter(this@`client retrieving data SAGA`)
                accepts {
                      it.demandValue(Key.EVENT_NAME, EventName.DOCUMENT_RECIEVED)
                      it.demandValue(Key.BEHOV, BehovName.FORMAT_DOCUMENT_IBM)
                      it.interestedIn(DataFelt.RAW_DOCUMENT)
                      it.interestedIn(DataFelt.RAW_DOCUMENT_FORMAT)
                }
            }
            løser(BehovName.PERSIST_DOCUMENT) {
                implementation = no.nav.jantra.river.examples.example_7_simple_saga.services.PersistDocument(this@`client retrieving data SAGA`)
                accepts {
                    it.demandValue(Key.BEHOV, BehovName.PERSIST_DOCUMENT)
                    it.interestedIn(DataFelt.FORMATED_DOCUMENT)
                    it.interestedIn(DataFelt.FORMATED_DOCUMENT_IBM)
                }
            }
            failListener{}
        }
    }.start()
    return this

}

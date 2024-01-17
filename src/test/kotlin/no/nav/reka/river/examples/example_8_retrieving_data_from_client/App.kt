package no.nav.reka.river.examples.example_8_retrieving_data_from_client

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.configuration.ListenerBuilder
import no.nav.reka.river.examples.example_1_basic_løser.DataFelt
import no.nav.reka.river.examples.example_1_basic_løser.EventName
import no.nav.reka.river.examples.example_8_retrieving_data_from_client.services.ApplicationRecievedListener
import no.nav.reka.river.examples.example_8_retrieving_data_from_client.services.PersistDocument
import no.nav.reka.river.interestedIn

import no.nav.reka.river.redis.RedisStore



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

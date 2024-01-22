package no.nav.jantra.river.examples.example_8_retrieving_data_from_client.services

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.jantra.river.IDataListener
import no.nav.jantra.river.IEventListener
import no.nav.jantra.river.IFailListener
import no.nav.jantra.river.examples.example_1_basic_løser.BehovName
import no.nav.jantra.river.examples.example_1_basic_løser.DataFelt
import no.nav.jantra.river.examples.example_1_basic_løser.EventName
import no.nav.jantra.river.model.Data
import no.nav.jantra.river.model.Event
import no.nav.jantra.river.model.Fail
import no.nav.jantra.river.publish
import no.nav.jantra.river.redis.RedisStore

class ApplicationRecievedListener(val rapidsConnection: RapidsConnection, val redisStore: RedisStore) : IEventListener,IDataListener, IFailListener {



    override fun onEvent(event: Event) {
        redisStore.set(event.riverId(),event.clientId)
        rapidsConnection.publish(event.createBehov(BehovName.PERSIST_DOCUMENT, mapOf(DataFelt.FORMATED_DOCUMENT to event[DataFelt.FORMATED_DOCUMENT])))
    }

    override fun onData(data: Data) {
        rapidsConnection.publish(Event.create(EventName.DOCUMENT_PERSISTED))
        val dokRef = data[DataFelt.DOCUMENT_REFERECE].asText()
        val uuid = data.riverId()
        val clientId = redisStore.get(uuid)
        redisStore.set(clientId!!, dokRef)
    }

     override fun onFail(fail: Fail) {

        val clientId = redisStore.get(fail.riverId())
         redisStore.set(clientId!!, "{fail:$fail.feilmelding}")
     }


}
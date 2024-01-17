package no.nav.reka.river.examples.example_8_retrieving_data_from_client.services

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.IDataListener
import no.nav.reka.river.IEventListener
import no.nav.reka.river.IFailListener
import no.nav.reka.river.Rapid
import no.nav.reka.river.examples.example_1_basic_løser.BehovName
import no.nav.reka.river.examples.example_1_basic_løser.DataFelt
import no.nav.reka.river.examples.example_1_basic_løser.EventName
import no.nav.reka.river.model.Data
import no.nav.reka.river.model.Event
import no.nav.reka.river.model.Fail
import no.nav.reka.river.publish
import no.nav.reka.river.redis.RedisStore

class ø(val rapidsConnection: RapidsConnection, val redisStore: RedisStore) : IEventListener,IDataListener, IFailListener {

    override fun accept(): River.PacketValidation {
        TODO("Not yet implemented")
    }

    override fun onEvent(event: Event) {
        redisStore.set(event.uuid(),event.clientId)
        rapidsConnection.publish(event.createBehov(BehovName.PERSIST_DOCUMENT, mapOf(DataFelt.FORMATED_DOCUMENT to event[DataFelt.FORMATED_DOCUMENT])))
    }

    override fun onData(data: Data) {
        rapidsConnection.publish(Event.create(EventName.DOCUMENT_PERSISTED))

        val dokRef = data[DataFelt.DOCUMENT_REFERECE].asText()
        val uuid = data.uuid()
        val clientId = redisStore.get(uuid)
        redisStore.set(clientId!!, dokRef)
    }

     override fun onFail(fail: Fail) {

        val clientId = redisStore.get(fail.uuid())
         redisStore.set(clientId!!, "{fail:$fail.feilmelding}")
     }


}
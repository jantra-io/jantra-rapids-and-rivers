package no.nav.reka.river.examples.example_8_retrieving_data_from_client.frontend

import kotlinx.coroutines.runBlocking
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.examples.example_1_basic_løser.DataFelt
import no.nav.reka.river.examples.example_1_basic_løser.EventName
import no.nav.reka.river.model.Event
import no.nav.reka.river.publish
import no.nav.reka.river.redis.RedisPoller
import java.util.*

class DocumentRecievedProducer(val rapid: RapidsConnection, val redisPoller: RedisPoller) {

    fun publish(rawDocument: String) : String {
        val clientId = UUID.randomUUID().toString()
        rapid.publish(Event.create(EventName.DOCUMENT_RECIEVED, clientId, mapOf(DataFelt.RAW_DOCUMENT to rawDocument)))
        return runBlocking {   redisPoller.hent(clientId)!! }
    }
}
package no.nav.reka.river.examples.exanoke_10_attach_eventStore

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.pond.eventstore.db.EventStoreRepo
import no.nav.reka.river.EndToEndTest
import no.nav.reka.river.Key
import no.nav.reka.river.examples.example_1_basic_løser.BehovName
import no.nav.reka.river.examples.example_1_basic_løser.DataFelt
import no.nav.reka.river.examples.example_1_basic_løser.EventName
import no.nav.reka.river.examples.example_3_event_triger_2_behov_emitting_event.`setup EventTriggering 2 Behov And Emitting Event`
import no.nav.reka.river.model.Event
import no.nav.reka.river.redis.RedisStore
import org.junit.Assert
import org.junit.jupiter.api.Test

class EventStoreTest: EndToEndTest() {


    val RAW_DOCUMNET = "This is my raw document"
    @Test
    fun `Event is triggering 2 behov, the last is emitting event`() {
        publish(Event.create(EventName.DOCUMENT_RECIEVED, mapOf(Key.APP_KEY to "doc121234", DataFelt.RAW_DOCUMENT to RAW_DOCUMNET)))
        Thread.sleep(10000)
        var riverId = ""
        with(filter(EventName.DOCUMENT_RECIEVED, BehovName.FORMAT_DOCUMENT).first()) {
            Assert.assertEquals(this[DataFelt.RAW_DOCUMENT.str].asText(), RAW_DOCUMNET)
            riverId = this[Key.UUID.str()].asText()
        }
        with(filter(EventName.DOCUMENT_RECIEVED, BehovName.PERSIST_DOCUMENT).first()) {
            Assert.assertEquals(this[DataFelt.FORMATED_DOCUMENT.str].asText(), "This is my formated document")
            Assert.assertEquals(riverId,this[Key.UUID.str()].asText())
        }
        with(filter(EventName.DOCUMENT_PERSISTED).first()) {
            Assert.assertEquals(this[DataFelt.DOCUMENT_REFERECE.str].asText(), "AB123")
            Assert.assertEquals(riverId,this[Key.TRANSACTION_ORIGIN.str()].asText())
        }

        val eventRepo = EventStoreRepo(this.db)
        val event = eventRepo.get("doc121234").toJsonMessage().toJson()
        eventRepo.findDownstreamEvents("doc121234",EventName.DOCUMENT_PERSISTED)
        println(event)

    }

    override val appBuilder: (rapidConnection: RapidsConnection, redisStore: RedisStore) -> RapidsConnection
        get() = { rapid: RapidsConnection, redisStore: RedisStore -> rapid.eventStoreExample(this.db)}
}
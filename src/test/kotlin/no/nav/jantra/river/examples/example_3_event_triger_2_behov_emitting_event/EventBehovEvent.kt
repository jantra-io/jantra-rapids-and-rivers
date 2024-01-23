package no.nav.jantra.river.examples.example_3_event_triger_2_behov_emitting_event

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.jantra.river.EndToEndTest
import no.nav.jantra.river.examples.example_1_basic_løser.BehovName
import no.nav.jantra.river.examples.example_1_basic_løser.DataFelt
import no.nav.jantra.river.examples.example_1_basic_løser.EventName
import no.nav.jantra.river.model.Event
import no.nav.jantra.river.redis.RedisStore
import no.nav.jantra.river.wait
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventBehovEvent : EndToEndTest() {

    val RAW_DOCUMNET = "This is my raw document"
    @Test
    fun `Event is triggering 2 behov, the last is emitting event`() {
        publish(Event.create(EventName.DOCUMENT_RECIEVED, mapOf(DataFelt.RAW_DOCUMENT to RAW_DOCUMNET)))
        wait()
        with(filter(EventName.DOCUMENT_RECIEVED, BehovName.FORMAT_DOCUMENT).first()) {
            Assert.assertEquals(this[DataFelt.RAW_DOCUMENT.str].asText(), RAW_DOCUMNET)
        }
        with(filter(EventName.DOCUMENT_RECIEVED, BehovName.PERSIST_DOCUMENT).first()) {
            Assert.assertEquals(this[DataFelt.FORMATED_DOCUMENT.str].asText(), "This is my formated document")
        }
        with(filter(EventName.DOCUMENT_PERSISTED).first()) {
            Assert.assertEquals(this[DataFelt.DOCUMENT_REFERECE.str].asText(), "AB123")
        }
    }

    override val appBuilder: (rapidConnection: RapidsConnection, redisStore: RedisStore) -> RapidsConnection
        get() = {rapid: RapidsConnection,redisStore:RedisStore -> rapid.`setup EventTriggering 2 Behov And Emitting Event`()}
}
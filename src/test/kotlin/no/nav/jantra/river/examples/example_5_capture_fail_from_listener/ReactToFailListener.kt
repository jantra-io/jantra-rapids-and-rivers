package no.nav.jantra.river.examples.example_5_capture_fail_from_listener

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.jantra.river.EndToEndTest
import no.nav.jantra.river.examples.example_1_basic_løser.BehovName
import no.nav.jantra.river.examples.example_1_basic_løser.DataFelt
import no.nav.jantra.river.examples.example_1_basic_løser.EventName
import no.nav.jantra.river.model.Event
import no.nav.jantra.river.redis.RedisStore
import no.nav.jantra.river.pause
import org.junit.Assert
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Event listener that can react to FAIL")
class ReactToFailListener : EndToEndTest() {

    val RAW_DOCUMNET = "This is my raw document"

    @Test
    fun `Event listener can also react to fail`() {
        publish(Event.create(EventName.DOCUMENT_RECIEVED, mapOf(DataFelt.RAW_DOCUMENT to RAW_DOCUMNET,
                                                                DataFelt.RAW_DOCUMENT_FORMAT to "")))
        pause()
        with(filter(EventName.DOCUMENT_RECIEVED, BehovName.FORMAT_DOCUMENT).first()) {
            Assert.assertEquals(this[DataFelt.RAW_DOCUMENT.str].asText(), RAW_DOCUMNET)
        }
        with(filter(EventName.DOCUMENT_RECIEVED, BehovName.PERSIST_DOCUMENT).first()) {
            Assert.assertEquals(this[DataFelt.FORMATED_DOCUMENT.str].asText(), "This is my IBM formated document")
        }
        with(filter(EventName.DOCUMENT_PERSISTED).first()) {
            Assert.assertEquals(this[DataFelt.DOCUMENT_REFERECE.str].asText(), "AB123")
        }
    }

    override val appBuilder: (rapidConnection: RapidsConnection, redisStore: RedisStore) -> RapidsConnection
        get() = { rapid: RapidsConnection, redisStore: RedisStore -> rapid.`setup EventListener reacting to Failure`()}

}
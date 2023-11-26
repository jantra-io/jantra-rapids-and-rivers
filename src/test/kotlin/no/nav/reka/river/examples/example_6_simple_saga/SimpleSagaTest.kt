package no.nav.reka.river.examples.example_6_simple_saga

import com.fasterxml.jackson.module.kotlin.contains
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.EndToEndTest
import no.nav.reka.river.Key
import no.nav.reka.river.examples.example_1_basic_løser.BehovName
import no.nav.reka.river.examples.example_1_basic_løser.DataFelt
import no.nav.reka.river.examples.example_1_basic_løser.EventName
import no.nav.reka.river.model.Event
import no.nav.reka.river.redis.RedisStore
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SimpleSagaTest : EndToEndTest(){
    val RAW_DOCUMNET = "This is my raw document"
    val ILLEGAL_CHARACTER = "%"

    @BeforeEach
    fun clean() {
        this.meldinger.clear()
        this.messages.clear()
    }
    @Test
    fun `2 step saga example`() {

        publish(Event.create(EventName.DOCUMENT_RECIEVED, mapOf(DataFelt.RAW_DOCUMENT to RAW_DOCUMNET)))
        Thread.sleep(5000)

        this.messages
            .withEventName(EventName.DOCUMENT_RECIEVED)
            .withBehovName(BehovName.FORMAT_DOCUMENT)
            .withData(listOf(DataFelt.RAW_DOCUMENT))
            .single().also {
                Assert.assertEquals(it[DataFelt.RAW_DOCUMENT.str].asText(), RAW_DOCUMNET)
            }
        with(filter(EventName.DOCUMENT_RECIEVED, BehovName.FORMAT_DOCUMENT).first()) {
            Assert.assertEquals(this[DataFelt.RAW_DOCUMENT.str].asText(), RAW_DOCUMNET)
        }
        with(filter(EventName.DOCUMENT_RECIEVED, BehovName.PERSIST_DOCUMENT).first()) {
            Assert.assertEquals("this is my formated document", this[DataFelt.FORMATED_DOCUMENT.str].asText())
        }
        with(filter(EventName.DOCUMENT_PERSISTED).first()) {
            Assert.assertEquals(this[DataFelt.DOCUMENT_REFERECE.str].asText(), "AB123")
        }

    }

    @Test
    fun `2 step saga demonstrating fail handling`() {

        publish(Event.create(EventName.DOCUMENT_RECIEVED, mapOf(DataFelt.RAW_DOCUMENT to RAW_DOCUMNET + ILLEGAL_CHARACTER)))
        Thread.sleep(5000)
        with(filter(EventName.DOCUMENT_RECIEVED, BehovName.FORMAT_DOCUMENT).first()) {
            Assert.assertEquals(this[DataFelt.RAW_DOCUMENT.str].asText(), RAW_DOCUMNET +ILLEGAL_CHARACTER)
        }
        with(filter(EventName.DOCUMENT_RECIEVED)) {
            Assert.assertNotNull(
                this.filter {
                    it.contains(Key.FAIL.str)
                }.first()
            )
        }
        with(filter(EventName.DOCUMENT_RECIEVED, BehovName.PERSIST_DOCUMENT).first()) {
            Assert.assertNull( this[DataFelt.FORMATED_DOCUMENT.str])
            Assert.assertEquals("This is my IBM formatted document", this[DataFelt.FORMATED_DOCUMENT_IBM.str].asText())
        }
        with(filter(EventName.DOCUMENT_PERSISTED).first()) {
            Assert.assertEquals(this[DataFelt.DOCUMENT_REFERECE.str].asText(), "AB123")
        }

    }


    override val appBuilder: (rapidConnection: RapidsConnection, redisStore: RedisStore) -> RapidsConnection
        get() = {rapid: RapidsConnection,redisStore:RedisStore -> rapid.buildSagaViaDSL(redisStore)}


}
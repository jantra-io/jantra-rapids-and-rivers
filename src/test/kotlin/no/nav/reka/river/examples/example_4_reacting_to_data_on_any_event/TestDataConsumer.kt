package no.nav.reka.river.examples.example_4_reacting_to_data_on_any_event

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.EndToEndTest
import no.nav.reka.river.examples.example_1_basic_løser.BehovName
import no.nav.reka.river.examples.example_1_basic_løser.DataFelt
import no.nav.reka.river.examples.example_1_basic_løser.EventName
import no.nav.reka.river.model.Event
import no.nav.reka.river.redis.RedisStore
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestDataConsumer : EndToEndTest() {
    override val appBuilder: (rapidConnection: RapidsConnection, redisStore: RedisStore) -> RapidsConnection
        get() = { rapid:RapidsConnection, redisStore -> rapid.buildReactingToData(redisStore) }

    @Test
    fun `Consumer reacting to data, agnostic of event and behov`() {
        publish(Event.create(EventName.DOCUMENT_RECIEVED, map = mapOf(DataFelt.RAW_DOCUMENT to "raw document")))
        Thread.sleep(5000)
        with(filter(EventName.DOCUMENT_RECIEVED).first()) {
            Assertions.assertEquals(this[DataFelt.RAW_DOCUMENT.str].asText(),"raw document")
        }
        with(filter(EventName.DOCUMENT_RECIEVED, BehovName.FORMAT_JSON).first()) {
            Assertions.assertEquals(this[DataFelt.RAW_DOCUMENT.str].asText(),"raw document")
        }
        with(filter(EventName.DOCUMENT_RECIEVED, BehovName.FORMAT_XML).first()) {
            Assertions.assertEquals(this[DataFelt.RAW_DOCUMENT.str].asText(),"raw document")
        }
        with(filter(EventName.DOCUMENT_RECIEVED, datafelt = DataFelt.FORMATED_DOCUMENT)) {
            Assertions.assertTrue( this.filter{
                it[DataFelt.FORMATED_DOCUMENT.str].asText() == "My XML formatted document"
            }.isNotEmpty())
            Assertions.assertTrue( this.filter{
                it[DataFelt.FORMATED_DOCUMENT.str].asText() == "My json formatted document"
            }.isNotEmpty())
        }

        with(filter(EventName.DOCUMENT_PERSISTED).first()) {
            Assertions.assertEquals(this[DataFelt.DOCUMENT_REFERECE.str].asText(),"AB123")
        }
    }

}
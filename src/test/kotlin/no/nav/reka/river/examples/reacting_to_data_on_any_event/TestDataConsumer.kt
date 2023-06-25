package no.nav.reka.river.examples.reacting_to_data_on_any_event

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.EndToEndTest
import no.nav.reka.river.examples.basic_consumer.DataFelt
import no.nav.reka.river.examples.basic_consumer.EventName
import no.nav.reka.river.model.Data
import no.nav.reka.river.redis.RedisStore
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("My First test")
class TestDataConsumer : EndToEndTest() {
    override val appBuilder: (rapidConnection: RapidsConnection, redisStore: RedisStore) -> RapidsConnection
        get() = { rapid:RapidsConnection, redisStore -> rapid.buildReactingToData(redisStore) }

    @Test
    fun `Consumer reacting to data, agnostic of event and behov`() {
        publish(Data.create(EventName.DOCUMENT_RECIEVED, map = mapOf(DataFelt.FORMATED_DOCUMENT to "formated document")))
        Thread.sleep(50000)
        with(filter(EventName.DOCUMENT_PERSISTED).first()) {
            Assertions.assertEquals(this[DataFelt.DOCUMENT_REFERECE.str].asText(),"AB123")
        }
    }

}
package no.nav.reka.river.examples.example_11_spawn_2_rivers

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.EndToEndTest
import no.nav.reka.river.Key
import no.nav.reka.river.examples.example_10_attach_eventStore.eventStoreExample
import no.nav.reka.river.examples.example_1_basic_løser.DataFelt
import no.nav.reka.river.examples.example_1_basic_løser.EventName
import no.nav.reka.river.model.Event
import no.nav.reka.river.redis.RedisStore
import org.junit.jupiter.api.Test

class SpawnsMultipleRiversTest : EndToEndTest() {

    val RAW_DOCUMNET = "This is my raw document"
    @Test
    fun `Spawns multiple rivers from the same Event`() {
         publish(Event.create(EventName.DOCUMENT_RECIEVED, mapOf(Key.APP_KEY to "doc121234", DataFelt.RAW_DOCUMENT to RAW_DOCUMNET)))
         Thread.sleep(5000)
    }
    override val appBuilder: (rapidConnection: RapidsConnection, redisStore: RedisStore) -> RapidsConnection
        get() = { rapid: RapidsConnection, redisStore: RedisStore -> rapid.multipleRiversExample(this.db)}
}
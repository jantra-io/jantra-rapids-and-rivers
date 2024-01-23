package no.nav.jantra.river.examples.example_11_spawn_2_rivers

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.jantra.pond.eventstore.db.RiverEventRepo
import no.nav.jantra.river.EndToEndTest
import no.nav.jantra.river.Key
import no.nav.jantra.river.examples.example_1_basic_løser.DataFelt
import no.nav.jantra.river.examples.example_1_basic_løser.EventName
import no.nav.jantra.river.model.Event
import no.nav.jantra.river.redis.RedisStore
import no.nav.jantra.river.pause
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SpawnsMultipleRiversTest : EndToEndTest() {

    val RAW_DOCUMNET = "This is my raw document"
    @Test
    fun `Spawns multiple rivers from the same Event`() {
        val event = Event.create(EventName.DOCUMENT_RECIEVED, mapOf(Key.APP_KEY to "doc121234", DataFelt.RAW_DOCUMENT to RAW_DOCUMNET))
         publish(event)
         pause()
         val riverEvent = RiverEventRepo(this.db)
         Assertions.assertEquals(2 , riverEvent.getRivers(event).size)

    }
    override val appBuilder: (rapidConnection: RapidsConnection, redisStore: RedisStore) -> RapidsConnection
        get() = { rapid: RapidsConnection, redisStore: RedisStore -> rapid.multipleRiversExample(this.db)}
}
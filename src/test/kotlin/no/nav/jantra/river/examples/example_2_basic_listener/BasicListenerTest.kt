package no.nav.jantra.river.examples.example_2_basic_listener

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
import org.junit.jupiter.api.TestInstance


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Example 2: Basic listener test")
class BasicListenerTest : EndToEndTest() {

    override val appBuilder: (rapidConnection: RapidsConnection, redisStore: RedisStore) -> RapidsConnection
        get() =   {rapid: RapidsConnection,redisStore:RedisStore -> rapid.buildBasicListenerApp(redisStore)}
    @org.junit.jupiter.api.Test
    fun `trigger Listener and then løser`() {

        val applicationStarted = Event.create(EventName.APPLICATION_INITIATED,
                                        mapOf(DataFelt.APPLICATION_ID to "123"))
        this.publish(applicationStarted)
        pause()
        with(filter(EventName.APPLICATION_INITIATED, BehovName.FULL_NAME).first()) {
          Assert.assertEquals(this[DataFelt.APPLICATION_ID.str].asText(), "123")
        }

    }


}
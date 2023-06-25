package no.nav.reka.river.examples.basic_listener

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.EndToEndTest
import no.nav.reka.river.examples.basic_consumer.BehovName
import no.nav.reka.river.examples.basic_consumer.DataFelt
import no.nav.reka.river.examples.basic_consumer.EventName
import no.nav.reka.river.model.Event
import no.nav.reka.river.redis.RedisStore
import org.junit.Assert
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("My First test")
class BasicListenerTest : EndToEndTest() {

    override val appBuilder: (rapidConnection: RapidsConnection, redisStore: RedisStore) -> RapidsConnection
        get() =   {rapid: RapidsConnection,redisStore:RedisStore -> rapid.buildBaisListenerApp(redisStore)}
    @org.junit.jupiter.api.Test
    fun `trigger simple løser`() {

        val applicationStarted = Event.create(EventName.APPLICATION_INITIATED,
                                        mapOf(DataFelt.APPLICATION_ID to "123"))
        this.publish(applicationStarted)
        Thread.sleep(5000)
        with(filter(EventName.APPLICATION_INITIATED, BehovName.FULL_NAME).first()) {
          Assert.assertEquals(this[DataFelt.APPLICATION_ID.str].asText(), "123")
        }

    }


}
package no.nav.jantra.river.examples.example_1_basic_løser

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.jantra.river.EndToEndTest
import no.nav.jantra.river.model.Behov
import no.nav.jantra.river.redis.RedisStore
import no.nav.jantra.river.pause
import org.junit.Assert
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Example 1: demonstrate wiring of simple Løser.")
class Example1Test : EndToEndTest() {

    @org.junit.jupiter.api.Test
    fun `Trigger simple løser`() {

        val needFullName = Behov.create(EventName.APPLICATION_INITIATED,
                                        BehovName.FULL_NAME,
                                        mapOf(DataFelt.APPLICATION_ID to "123"))
        this.publish(needFullName)
        pause()
        with(filter(EventName.APPLICATION_INITIATED, datafelt = DataFelt.NAME).first()) {
          Assert.assertEquals(this[DataFelt.NAME.str].asText(), "Alexander Petrov")
        }

    }

    override val appBuilder: (rapidConnection: RapidsConnection, redisStore: RedisStore) -> RapidsConnection
        get() =   {rapid: RapidsConnection,redisStore:RedisStore -> rapid.buildBasicConsumerApp(redisStore)}
}
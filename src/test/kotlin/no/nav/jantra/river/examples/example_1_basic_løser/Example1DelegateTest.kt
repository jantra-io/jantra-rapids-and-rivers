package no.nav.jantra.river.examples.example_1_basic_løser

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.jantra.river.EndToEndTest
import no.nav.jantra.river.model.Behov
import no.nav.jantra.river.redis.RedisStore
import org.junit.Assert
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("My First test")
class Example1DelegateTest : EndToEndTest() {

    @org.junit.jupiter.api.Test
    fun `Trigger simple løser`() {

        val needFullName = Behov.create(EventName.APPLICATION_INITIATED,
                                        BehovName.FULL_NAME,
                                        mapOf(DataFelt.APPLICATION_ID to "123"))
        this.publish(needFullName)
        Thread.sleep(5000)
        with(filter(EventName.APPLICATION_INITIATED, datafelt = DataFelt.NAME).first()) {
          Assert.assertEquals(this[DataFelt.NAME.str].asText(), "Alexander Petrov")
        }

    }

    override val appBuilder: (rapidConnection: RapidsConnection, redisStore: RedisStore) -> RapidsConnection
        get() =   {rapid: RapidsConnection,redisStore:RedisStore -> rapid.buildBasicConsumerAppWithDelegation(redisStore)}
}
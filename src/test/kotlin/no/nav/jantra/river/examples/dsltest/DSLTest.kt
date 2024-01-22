package no.nav.jantra.river.examples.dsltest

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.jantra.river.EndToEndTest
import no.nav.jantra.river.redis.RedisStore
import org.junit.jupiter.api.Test

class DSLTest : EndToEndTest() {

    @Test
    fun testDsl() {

    }

    override val appBuilder: (rapidConnection: RapidsConnection, redisStore: RedisStore) -> RapidsConnection
        get() = { rapid: RapidsConnection, redisStore: RedisStore ->    rapid.testDsl()
        }
}
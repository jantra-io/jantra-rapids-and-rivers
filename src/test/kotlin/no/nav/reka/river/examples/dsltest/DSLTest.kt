package no.nav.reka.river.examples.dsltest

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.EndToEndTest
import no.nav.reka.river.redis.RedisStore
import org.junit.jupiter.api.Test

class DSLTest : EndToEndTest() {

    @Test
    fun testDsl() {

    }

    override val appBuilder: (rapidConnection: RapidsConnection, redisStore: RedisStore) -> RapidsConnection
        get() = { rapid: RapidsConnection, redisStore: RedisStore ->    rapid.testDsl()
        }
}
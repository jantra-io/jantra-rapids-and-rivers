package no.nav.reka.river.examples.dsltest

import no.nav.helse.rapids_rivers.KtorBuilder
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.EndToEndTest
import no.nav.reka.river.examples.composite_med_fail_listener.`setup EventListener reacting to Failure`
import no.nav.reka.river.redis.RedisStore
import org.junit.jupiter.api.Test

class DSLTest : EndToEndTest() {

    @Test
    fun testDsl() {

    }

    override val appBuilder: (rapidConnection: RapidsConnection, redisStore: RedisStore) -> RapidsConnection
        get() = { rapid: RapidsConnection, redisStore: RedisStore -> rapid.testDsl()}
}
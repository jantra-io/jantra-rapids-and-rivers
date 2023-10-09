package no.nav.reka.river.examples.example_2_basic_listener

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.examples.services.*

import no.nav.reka.river.redis.RedisStore



fun RapidsConnection.buildBasicListenerApp(redisStore: RedisStore): RapidsConnection {
    RetrieveFullNameService(this).start()
    ApplicationStartedListener(this).start()

    return this
}

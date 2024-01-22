package no.nav.jantra.river.examples.example_2_basic_listener

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.jantra.river.examples.services.basic.ApplicationStartedListener
import no.nav.jantra.river.examples.services.basic.RetrieveFullNameService

import no.nav.jantra.river.redis.RedisStore



fun RapidsConnection.buildBasicListenerApp(redisStore: RedisStore): RapidsConnection {
    RetrieveFullNameService(this).start()
    ApplicationStartedListener(this).start()

    return this
}

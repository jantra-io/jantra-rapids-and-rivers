package no.nav.reka.river.examples.basic_listener

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.examples.services.*

import no.nav.reka.river.redis.RedisStore



fun RapidsConnection.buildBasicListenerApp(redisStore: RedisStore): RapidsConnection {
    RetrieveFullNameService(this)
    ApplicationStartedListener(this).start()

    return this
}

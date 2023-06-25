package no.nav.reka.river.examples.basic_consumer

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.examples.services.*

import no.nav.reka.river.redis.RedisStore



fun RapidsConnection.buildBasicConsumerApp(redisStore: RedisStore): RapidsConnection {
    RetrieveFullNameService(this)
    return this
}

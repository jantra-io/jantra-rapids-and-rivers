package no.nav.reka.river.examples

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.examples.basic_consumer.FullNameConsumer

import no.nav.reka.river.redis.RedisStore



fun RapidsConnection.buildApp(redisStore: RedisStore): RapidsConnection {
    FullNameConsumer(this)
    return this
}

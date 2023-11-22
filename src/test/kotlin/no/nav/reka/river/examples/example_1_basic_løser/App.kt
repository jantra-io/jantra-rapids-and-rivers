package no.nav.reka.river.examples.example_1_basic_løser

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.examples.services.*

import no.nav.reka.river.redis.RedisStore



fun RapidsConnection.buildBasicConsumerApp(redisStore: RedisStore): RapidsConnection {
    RetrieveFullNameService(this).start()
    return this
}

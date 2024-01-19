package no.nav.reka.river.examples.example_1_basic_løser

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.Key
import no.nav.reka.river.bridge.BehovRiver
import no.nav.reka.river.demandValue
import no.nav.reka.river.examples.services.*

import no.nav.reka.river.redis.RedisStore



fun RapidsConnection.buildBasicConsumerApp(redisStore: RedisStore): RapidsConnection {
    RetrieveFullNameService(this).start()
    return this
}

fun RapidsConnection.buildBasicConsumerAppWithDelegation(redisStore: RedisStore): RapidsConnection {

    val retrieveFullNameService = RetrieveFullNameService(this)
    BehovRiver(this,retrieveFullNameService) {
        it.demandValue(Key.EVENT_NAME, EventName.APPLICATION_INITIATED)
        it.demandValue(Key.BEHOV, BehovName.FULL_NAME)
    }.start()

    return this
}


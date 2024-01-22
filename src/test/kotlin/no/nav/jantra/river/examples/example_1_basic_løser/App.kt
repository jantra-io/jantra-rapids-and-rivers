package no.nav.jantra.river.examples.example_1_basic_løser

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.jantra.river.Key
import no.nav.jantra.river.bridge.BehovRiver
import no.nav.jantra.river.demandValue
import no.nav.jantra.river.examples.services.basic.RetrieveFullNameService
import no.nav.jantra.river.examples.services.basic.RetrieveFullNameServiceDelegate
import no.nav.jantra.river.publish

import no.nav.jantra.river.redis.RedisStore



fun RapidsConnection.buildBasicConsumerApp(redisStore: RedisStore): RapidsConnection {
    RetrieveFullNameService(this).start()
    return this
}


fun RapidsConnection.buildBasicConsumerAppWithDelegation(redisStore: RedisStore): RapidsConnection {


    BehovRiver(this,
                             RetrieveFullNameServiceDelegate(this)) {
        it.demandValue(Key.EVENT_NAME, EventName.APPLICATION_INITIATED)
        it.demandValue(Key.BEHOV, BehovName.FULL_NAME)
    }.start()

    return this
}

fun RapidsConnection.buildBasicConsumerAppWithDelegation2(redisStore: RedisStore): RapidsConnection {

    BehovRiver(this,
        {
            behov -> this.publish(behov.createData(mapOf(DataFelt.NAME to "Alexander Petrov")))
        }
    ) {
        it.demandValue(Key.EVENT_NAME, EventName.APPLICATION_INITIATED)
        it.demandValue(Key.BEHOV, BehovName.FULL_NAME)
    }.start()


    return this
}


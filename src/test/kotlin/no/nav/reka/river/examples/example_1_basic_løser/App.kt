package no.nav.reka.river.examples.example_1_basic_løser

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.IBehovListener
import no.nav.reka.river.Key
import no.nav.reka.river.bridge.BehovRiver
import no.nav.reka.river.demandValue
import no.nav.reka.river.examples.services.basic.RetrieveFullNameService
import no.nav.reka.river.examples.services.basic.RetrieveFullNameServiceDelegate
import no.nav.reka.river.model.Behov
import no.nav.reka.river.publish

import no.nav.reka.river.redis.RedisStore



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


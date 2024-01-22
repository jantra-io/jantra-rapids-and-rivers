package no.nav.jantra.river.configuration.dsl

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.jantra.river.IDataFelt
import no.nav.jantra.river.IKey
import no.nav.jantra.river.MessageType
import no.nav.jantra.river.bridge.BehovRiver
import no.nav.jantra.river.bridge.DataRiver
import no.nav.jantra.river.bridge.EventRiver
import no.nav.jantra.river.bridge.FailRiver
import no.nav.jantra.river.composite.DelegatingFailKanal
import no.nav.jantra.river.composite.Saga
import no.nav.jantra.river.composite.SagaRunner
import no.nav.jantra.river.composite.StatefullDataKanal
import no.nav.jantra.river.composite.StatefullEventKanal
import no.nav.jantra.river.plus
import no.nav.jantra.river.redis.RedisStore


class SagaBuilder(private val rapid: RapidsConnection,
                  private val redisStore: RedisStore,
                  val løser: MutableList<BehovRiver> = mutableListOf(),
                  val failListeners: MutableList<FailRiver> = mutableListOf()
) {
    lateinit var eventName: MessageType.Event
    private lateinit var sagaRunner: SagaRunner
    private lateinit var eventListener: StatefullEventKanal
    lateinit var dataListener: StatefullDataKanal

    @DSLTopology
    fun implementation(implementation: Saga) {
        sagaRunner = SagaRunner(redisStore,rapid,implementation )
    }


    @DSLTopology
    fun eventListener(eventName: MessageType.Event ,block: SagaEventListenerBuilder.()-> Unit) {
        eventListener = SagaEventListenerBuilder(eventName,redisStore,rapid,sagaRunner).apply(block).build()
    }

    @DSLTopology
    fun dataListener(vararg datafelter: IKey) {
        dataListener = SagaDataListenerBuilder(eventListener.eventName,redisStore,rapid,sagaRunner).capture(*datafelter).start()
    }
    @DSLTopology
    fun løser(behov: MessageType.Behov, block: LøserBuilder.() -> Unit) {
        løser.add(LøserBuilder(behov,eventListener.eventName, rapid).apply (block ).build())
    }

    fun failListener(block:FailListenerBuilder.() -> Unit) {
        val builder = FailListenerBuilder(eventListener.eventName,rapid)
        builder.implementation =  DelegatingFailKanal(eventListener.eventName,sagaRunner,rapid)
        failListeners.add(builder.build())
    }

    fun start() {

        sagaRunner.dataKanal = dataListener
        EventRiver(rapid,eventListener,eventListener.accept()).start()
        DataRiver(rapid,dataListener,dataListener.accept()).start()

        løser.forEach{
            it.start()
        }
        failListeners.forEach {
            it.start()
        }
    }
}

class SagaDataListenerBuilder( private val eventName: MessageType.Event,
                               private val redisStore: RedisStore,
                               private val rapid: RapidsConnection,
                               private val messageListener: SagaRunner) {

    lateinit var datafelter: Array<IKey>

    fun capture(vararg datafelter: IKey) : SagaDataListenerBuilder {
        this.datafelter = datafelter.map { it }.toTypedArray()
        return this
    }

    fun start() : StatefullDataKanal {
        return StatefullDataKanal(eventName,datafelter,messageListener,redisStore,rapid)
    }

}

@DSLTopology
class SagaEventListenerBuilder(private val eventName: MessageType.Event,
                               private val redisStore: RedisStore,
                               private val rapid: RapidsConnection,
                               private val sagaRunner: SagaRunner
                            ) {
    @DSLTopology
    private lateinit var accept: River.PacketValidation
    private lateinit var capture: Array<IDataFelt>

    @DSLTopology()
    fun accepts(jsonMessage: (JsonMessage) -> Unit)  {
        accept = River.PacketValidation {
            jsonMessage.invoke(it)
        }
    }

    fun capture(vararg datafelter: IDataFelt) {
        if (::accept.isInitialized) {
            accept += {
                it.interestedIn(*datafelter.map { it.str }.toTypedArray())
            }
        }
        else accept = River.PacketValidation{
                it.interestedIn(*datafelter.map { it.str }.toTypedArray())
            }
        this.capture = datafelter.map { it }.toTypedArray()
    }


    internal fun build() : StatefullEventKanal {

        return StatefullEventKanal(eventName,redisStore,capture,sagaRunner)
    }

}

class SagaDSL {
}
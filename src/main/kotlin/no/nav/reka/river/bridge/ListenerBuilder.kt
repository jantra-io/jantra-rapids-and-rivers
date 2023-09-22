package no.nav.reka.river.bridge

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.Key
import no.nav.reka.river.MessageType
import no.nav.reka.river.IDataListener
import no.nav.reka.river.IEventListener
import no.nav.reka.river.IFailListener


class ListenerBuilder(val rapid:RapidsConnection) {

    lateinit var event: MessageType.Event
    private lateinit var eventRiver: EventRiver
    private lateinit var dataRiver:  DataRiver
    private lateinit var failRiver: FailRiver

    lateinit var eventlistenerBuilder: EventListenerBuilder


    fun eventListener(event: MessageType.Event) : EventListenerBuilder {
        this.event = event
        return EventListenerBuilder(this)
    }

    fun failListener() : FailListenerBuilder {
        return FailListenerBuilder(this)
    }

    fun dataListener() : DataListenerBuilder {
        return DataListenerBuilder(this)
    }

    fun start() {
        if (::eventRiver.isInitialized) eventRiver.start()
        if (::dataRiver.isInitialized)  dataRiver.start()
        if (::failRiver.isInitialized)  failRiver.start()
    }

    class EventListenerBuilder(private val listenerBuilder: ListenerBuilder) {
        lateinit var listener: IEventListener
        lateinit var accepts: River.PacketValidation

        fun implementation(listener: IEventListener) : EventListenerBuilder {
            this.listener = listener
            return this
        }

        fun accept(accepts: River.PacketValidation) {
            this.accepts = accepts
        }

        fun build() : ListenerBuilder {
            val validation = if (::accepts.isInitialized) accepts else listener.accept()
            listenerBuilder.eventRiver = EventRiver(listenerBuilder.rapid,listener, validation)
            return listenerBuilder
        }

    }

    class FailListenerBuilder(private val listenerBuilder: ListenerBuilder) {
        lateinit var riverValidation : River.PacketValidation
        lateinit var accepts: River.PacketValidation
        lateinit var listener: IFailListener

        fun implementation(listener: IFailListener) : FailListenerBuilder {
            this.listener = listener
            return this
        }

        fun accept(accepts: River.PacketValidation) : FailListenerBuilder {
            this.accepts = accepts
            return this
        }

        fun build() : ListenerBuilder {
            if (!this::riverValidation.isInitialized) riverValidation = River.PacketValidation {
                it.demandValue(Key.EVENT_NAME.str,listenerBuilder.event.value)
                accepts.validate(it)
            }
            listenerBuilder.failRiver = FailRiver(listenerBuilder.rapid,listener,riverValidation)
            return listenerBuilder
        }

    }

    class DataListenerBuilder(private val listenerBuilder: ListenerBuilder) {
        lateinit var riverValidation : River.PacketValidation
        lateinit var listenerValidation: River.PacketValidation
        lateinit var listener: IDataListener

        fun failListener(listener: IDataListener) : DataListenerBuilder {
            this.listener = listener
            return this
        }

        fun accept(validation: River.PacketValidation) {
            this.listenerValidation = validation
        }

        fun build() : IDataListener {
            if (!this::riverValidation.isInitialized) riverValidation = River.PacketValidation {
                it.demandValue(Key.EVENT_NAME.str,listenerBuilder.event.value)
            }
            listenerBuilder.dataRiver = DataRiver(listenerBuilder.rapid,listener,riverValidation)
            return listener
        }

    }

}










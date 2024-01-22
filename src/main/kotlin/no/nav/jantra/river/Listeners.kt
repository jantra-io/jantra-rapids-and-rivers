package no.nav.jantra.river

import no.nav.helse.rapids_rivers.River
import no.nav.jantra.river.model.Behov
import no.nav.jantra.river.model.Data
import no.nav.jantra.river.model.Event
import no.nav.jantra.river.model.Fail


interface IMessageListener

interface ValidatedMessage {
    fun accept(): River.PacketValidation
}

interface IEventListener : IMessageListener {

    fun onEvent(event: Event)


}

fun interface  IFailListener : IMessageListener {
    fun onFail(fail: Fail)

}

fun interface IDataListener : IMessageListener {

    fun onData(data: Data)
}

fun interface IBehovListener {

    fun onBehov(behov: Behov)
}


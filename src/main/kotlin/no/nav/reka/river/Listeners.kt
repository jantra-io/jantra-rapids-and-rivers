package no.nav.reka.river

import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.model.Behov
import no.nav.reka.river.model.Data
import no.nav.reka.river.model.Event
import no.nav.reka.river.model.Fail


interface IMessageListener

interface ValidatedMessage {
    fun accept(): River.PacketValidation
}

interface IEventListener : IMessageListener {

    fun onEvent(event: Event)


}

interface  IFailListener : IMessageListener {
    fun onFail(fail: Fail)

}

interface IDataListener : IMessageListener {

    fun onData(data: Data)
}

interface IBehovListener : IMessageListener {

    fun onBehov(behov: Behov)
}


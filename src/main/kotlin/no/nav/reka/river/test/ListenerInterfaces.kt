package no.nav.reka.river.test

import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.model.Data
import no.nav.reka.river.model.Event
import no.nav.reka.river.model.Fail


interface ValidatedMessage {
    fun accept(): River.PacketValidation
}

interface IEventListener : ValidatedMessage {

    fun onEvent(event: Event)
}


interface  IFailListener : ValidatedMessage {
    fun onFail(fail: Fail)

}

interface IDataListener : ValidatedMessage {

    fun onData(data: Data)
}


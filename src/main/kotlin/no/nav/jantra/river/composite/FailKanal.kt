package no.nav.jantra.river.composite

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.jantra.river.MessageType
import no.nav.jantra.river.model.Fail
import no.nav.jantra.river.IFailListener

abstract class FailKanal(open val eventName: MessageType.Event,val rapidsConnection: RapidsConnection) : IFailListener {


    abstract override fun onFail(packet: Fail)
}

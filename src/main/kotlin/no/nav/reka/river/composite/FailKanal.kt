package no.nav.reka.river.composite

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.Key
import no.nav.reka.river.MessageType
import no.nav.reka.river.demandValue
import no.nav.reka.river.model.Fail
import no.nav.reka.river.bridge.FailRiver
import no.nav.reka.river.IFailListener
import no.nav.reka.river.plus

abstract class FailKanal(open val eventName: MessageType.Event,val rapidsConnection: RapidsConnection) : IFailListener {


    abstract override fun onFail(packet: Fail)
}

package no.nav.reka.river.basic

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.IBehovListener
import no.nav.reka.river.Key
import no.nav.reka.river.MessageType
import no.nav.reka.river.ValidatedMessage
import no.nav.reka.river.bridge.BehovRiver
import no.nav.reka.river.demandValue
import no.nav.reka.river.plus

abstract class Løser(val rapidsConnection: RapidsConnection) : IBehovListener, ValidatedMessage {

    abstract val event: MessageType.Event
    fun start() {
        BehovRiver(rapidsConnection,this,accept() + {it.demandValue(Key.EVENT_NAME,event)}).start()
    }



}

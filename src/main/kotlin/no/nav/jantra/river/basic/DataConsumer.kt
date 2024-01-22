package no.nav.jantra.river.basic

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.jantra.river.IDataListener
import no.nav.jantra.river.Key
import no.nav.jantra.river.MessageType
import no.nav.jantra.river.ValidatedMessage
import no.nav.jantra.river.bridge.DataRiver
import no.nav.jantra.river.demandValue
import no.nav.jantra.river.plus

abstract class DataConsumer(val rapidsConnection: RapidsConnection): IDataListener,ValidatedMessage {

    abstract val event: MessageType.Event
    fun start() {
        DataRiver(rapidsConnection,this,accept() + {it.demandValue(Key.EVENT_NAME,event)}).start()
    }


}
package no.nav.reka.river.composite

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.MessageType
import no.nav.reka.river.bridge.DataRiver
import no.nav.reka.river.IDataListener
import no.nav.reka.river.Key
import no.nav.reka.river.ValidatedMessage
import no.nav.reka.river.demandValue

abstract class DataKanal(open val eventName: MessageType.Event) : IDataListener,ValidatedMessage {

    override fun accept() : River.PacketValidation {
        return River.PacketValidation {
            it.demandValue(Key.EVENT_NAME, eventName)
        }
    }

}

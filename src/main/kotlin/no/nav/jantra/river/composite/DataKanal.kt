package no.nav.jantra.river.composite

import no.nav.helse.rapids_rivers.River
import no.nav.jantra.river.MessageType
import no.nav.jantra.river.IDataListener
import no.nav.jantra.river.Key
import no.nav.jantra.river.ValidatedMessage
import no.nav.jantra.river.demandValue

abstract class DataKanal(open val eventName: MessageType.Event) : IDataListener,ValidatedMessage {

    override fun accept() : River.PacketValidation {
        return River.PacketValidation {
            it.demandValue(Key.EVENT_NAME, eventName)
        }
    }

}

package no.nav.reka.river.examples.capture_fail_from_listener.services

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.*
import no.nav.reka.river.examples.basic_consumer.BehovName
import no.nav.reka.river.examples.basic_consumer.DataFelt
import no.nav.reka.river.examples.basic_consumer.EventName
import no.nav.reka.river.model.Behov
import no.nav.reka.river.model.Event
import no.nav.reka.river.model.Fail
import no.nav.reka.river.test.EventListenerWithFail

class DocumentRecievedListener(rapidsConnection: RapidsConnection, ) : EventListenerWithFail(rapidsConnection) {
    override val event: MessageType.Event  get() = EventName.DOCUMENT_RECIEVED
    override fun accept(): River.PacketValidation = River.PacketValidation {
        it.demandValue(Key.EVENT_NAME,event)
        it.interestedIn(DataFelt.RAW_DOCUMENT)
    }

    override fun onEvent(packet: Event) {
        publishBehov(packet.createBehov(BehovName.FORMAT_DOCUMENT, mapOf(DataFelt.RAW_DOCUMENT to packet[DataFelt.RAW_DOCUMENT])))
    }

    override fun onFail(fail: Fail) {
        publishBehov(Behov.create(event,BehovName.FORMAT_DOCUMENT_IBM,mapOf(DataFelt.RAW_DOCUMENT to fail[DataFelt.RAW_DOCUMENT])))
    }

}
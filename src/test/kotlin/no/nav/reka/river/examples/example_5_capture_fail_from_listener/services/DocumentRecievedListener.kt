package no.nav.reka.river.examples.example_5_capture_fail_from_listener.services

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.*
import no.nav.reka.river.examples.example_1_basic_løser.BehovName
import no.nav.reka.river.examples.example_1_basic_løser.DataFelt
import no.nav.reka.river.examples.example_1_basic_løser.EventName
import no.nav.reka.river.model.Behov
import no.nav.reka.river.model.Event
import no.nav.reka.river.model.Fail
import no.nav.reka.river.IEventListener
import no.nav.reka.river.IFailListener

class DocumentRecievedListener(val rapidsConnection: RapidsConnection) : IEventListener, IFailListener {

    override fun onEvent(event: Event) {
        rapidsConnection.publish(
            event.createBehov(
                BehovName.FORMAT_DOCUMENT,
                mapOf(
                    DataFelt.RAW_DOCUMENT to event[DataFelt.RAW_DOCUMENT],
                    DataFelt.RAW_DOCUMENT_FORMAT to "ebcdic"
                )
            )
        )
    }

    override fun onFail(fail: Fail) {

        rapidsConnection.publish(
            fail.createBehov(
                BehovName.FORMAT_DOCUMENT_IBM,
                mapOf(DataFelt.RAW_DOCUMENT to fail[DataFelt.RAW_DOCUMENT])
            )
        )
    }

}


/*
    override fun accept(): River.PacketValidation  {
        return River.PacketValidation {
            it.demandValue(Key.EVENT_NAME,event)
            it.interestedIn(DataFelt.RAW_DOCUMENT)
        }
    }
*/
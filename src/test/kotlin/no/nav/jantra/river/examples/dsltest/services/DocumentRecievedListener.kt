package no.nav.jantra.river.examples.dsltest.services

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.jantra.river.*
import no.nav.jantra.river.examples.example_1_basic_løser.BehovName
import no.nav.jantra.river.examples.example_1_basic_løser.DataFelt
import no.nav.jantra.river.model.Event
import no.nav.jantra.river.model.Fail

class DocumentRecievedListener(val rapid: RapidsConnection ) : IEventListener, IFailListener {


    override fun onEvent(event: Event) {
        this.rapid.publish(event.createBehov(BehovName.FORMAT_DOCUMENT, mapOf(DataFelt.RAW_DOCUMENT to event[DataFelt.RAW_DOCUMENT],
            DataFelt.RAW_DOCUMENT_FORMAT to "ebcdic")))
    }

    override fun onFail(fail: Fail) {
        this.rapid.publish(fail.createBehov(BehovName.FORMAT_DOCUMENT_IBM,mapOf(DataFelt.RAW_DOCUMENT to fail[DataFelt.RAW_DOCUMENT])))
    }

}
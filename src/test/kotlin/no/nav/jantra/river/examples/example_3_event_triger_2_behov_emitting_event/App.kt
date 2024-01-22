package no.nav.jantra.river.examples.example_3_event_triger_2_behov_emitting_event

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.jantra.river.examples.example_3_event_triger_2_behov_emitting_event.services.DocumentRecievedListener
import no.nav.jantra.river.examples.example_3_event_triger_2_behov_emitting_event.services.FormatDokumentService
import no.nav.jantra.river.examples.example_3_event_triger_2_behov_emitting_event.services.PersistDocument


fun RapidsConnection.`setup EventTriggering 2 Behov And Emitting Event`(): RapidsConnection {
    DocumentRecievedListener(this).start()
    FormatDokumentService(this).start()
    PersistDocument(this).start()
    return this
}

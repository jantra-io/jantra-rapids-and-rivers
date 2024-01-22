package no.nav.jantra.river.examples

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.jantra.river.examples.example_3_event_triger_2_behov_emitting_event.services.DocumentRecievedListener
import no.nav.jantra.river.examples.example_3_event_triger_2_behov_emitting_event.services.FormatDokumentService
import no.nav.jantra.river.examples.example_3_event_triger_2_behov_emitting_event.services.PersistDocument
import no.nav.jantra.river.examples.services.basic.ApplicationStartedListener
import no.nav.jantra.river.examples.services.basic.RetrieveFullNameService

import no.nav.jantra.river.redis.RedisStore



fun RapidsConnection.buildApp(redisStore: RedisStore): RapidsConnection {
    RetrieveFullNameService(this)
    ApplicationStartedListener(this)
    DocumentRecievedListener(this)
    FormatDokumentService(this)
    PersistDocument(this)
    return this
}

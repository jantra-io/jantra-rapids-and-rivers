package no.nav.reka.river.examples.simple_saga

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.composite.DelegatingFailKanal
import no.nav.reka.river.composite.StatefullDataKanal
import no.nav.reka.river.composite.StatefullEventKanal
import no.nav.reka.river.examples.example_1_basic_løser.DataFelt
import no.nav.reka.river.examples.example_1_basic_løser.EventName
import no.nav.reka.river.examples.simple_saga.services.FormatDokumentService
import no.nav.reka.river.examples.simple_saga.services.LegacyIBMFormatter
import no.nav.reka.river.examples.simple_saga.services.PersistDocument

import no.nav.reka.river.redis.RedisStore



fun RapidsConnection.buildSagaScenario(redisStore: RedisStore): RapidsConnection {
    val formattingService = DocumentFormatingSaga(EventName.DOCUMENT_RECIEVED, redisStore, this)

    StatefullEventKanal(redisStore, EventName.DOCUMENT_RECIEVED, arrayOf(DataFelt.RAW_DOCUMENT), formattingService, this).start()
    val datakanal = StatefullDataKanal(arrayOf(DataFelt.FORMATED_DOCUMENT,DataFelt.FORMATED_DOCUMENT_IBM,DataFelt.DOCUMENT_REFERECE),EventName.DOCUMENT_RECIEVED,formattingService,this,redisStore)
    DelegatingFailKanal(EventName.DOCUMENT_RECIEVED,formattingService,this).start()

    formattingService.withDataKanal { datakanal }
    datakanal.start()

    FormatDokumentService(this).start()
    LegacyIBMFormatter(this).start()
    PersistDocument(this).start()
    return this
}

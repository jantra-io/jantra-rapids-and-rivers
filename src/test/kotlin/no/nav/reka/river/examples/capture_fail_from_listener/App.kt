package no.nav.reka.river.examples.capture_fail_from_listener

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.examples.capture_fail_from_listener.services.DocumentRecievedListener
import no.nav.reka.river.examples.capture_fail_from_listener.services.FormatDokumentService
import no.nav.reka.river.examples.capture_fail_from_listener.services.LegacyIBMFormatter
import no.nav.reka.river.examples.capture_fail_from_listener.services.PersistDocument
import no.nav.reka.river.redis.RedisStore

fun RapidsConnection.`setup EventListener reacting to Failure`(redisStore: RedisStore): RapidsConnection {
    DocumentRecievedListener(this).withFailhandling()
    FormatDokumentService(this)
    LegacyIBMFormatter(this)
    PersistDocument(this)
    return this
}
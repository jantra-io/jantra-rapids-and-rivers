package no.nav.reka.river.examples

import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.examples.twostepcomposite.FullNameLøser

import no.nav.reka.river.redis.RedisStore

fun main() {
    val env = mutableMapOf<String, String>()
    with(env) {
        put("KAFKA_RAPID_TOPIC", "helsearbeidsgiver.inntektsmelding")
        put("KAFKA_BOOTSTRAP_SERVERS", "PLAINTEXT://localhost:9092")
        put("KAFKA_CONSUMER_GROUP_ID", "HAG")
    }
    RapidApplication
        .create(env)
        .buildApp()
        .start()
}

fun RapidsConnection.buildApp(): RapidsConnection {
    val redisStore = RedisStore("redis://localhost:6379/0")
    FullNameLøser(this)
    return this
}

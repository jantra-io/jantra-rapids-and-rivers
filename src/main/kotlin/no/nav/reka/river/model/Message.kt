package no.nav.reka.river.model

import com.fasterxml.jackson.databind.JsonNode
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.IKey

interface Message {

    operator fun get(key: IKey): JsonNode

    operator fun set(key: IKey, value: Any)
    fun toJsonMessage(): JsonMessage

    fun publishOn(rapidsConnection: RapidsConnection) {
        rapidsConnection.publish(this.toJsonMessage().toJson())
    }
}
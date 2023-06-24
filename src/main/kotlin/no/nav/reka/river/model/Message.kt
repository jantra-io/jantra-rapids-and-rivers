package no.nav.reka.river.model

import no.nav.helse.rapids_rivers.JsonMessage

interface Message {

    fun toJsonMessage(): JsonMessage
}
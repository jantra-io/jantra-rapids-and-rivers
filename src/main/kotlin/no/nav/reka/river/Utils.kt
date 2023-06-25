package no.nav.reka.river

import no.nav.helse.rapids_rivers.JsonMessage

fun JsonMessage.demandValue(key: IKey, messageType: MessageType) {
    this.demandValue(key.str,messageType.toString())
}

fun JsonMessage.demandValue(key: IKey, datafelt: IDataFelt) {
    this.demandValue(key.str,datafelt.toString())
}

fun JsonMessage.interestedIn(vararg keys: IKey) {
    this.interestedIn(*keys.map { it.str }.toTypedArray())
}
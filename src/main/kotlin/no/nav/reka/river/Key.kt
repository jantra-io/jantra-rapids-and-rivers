package no.nav.reka.river

import com.fasterxml.jackson.databind.JsonNode
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.isMissingOrNull

enum class Key(override val str: String) : IKey{
    // Predefinerte fra rapids-and-rivers-biblioteket
    EVENT_NAME("@event_name"),
    BEHOV("@behov"),
    FAILED_BEHOV("failed-behov"),
    EVENT_TIME("@opprettet"),
    UUID("uuid"),
    CLIENT_ID("client_id"),
    TRANSACTION_ORIGIN("transaction_origin"),
    DATA("data"),
    FAIL("fail"),
    APP_KEY("app_key");

    override fun toString(): String =
            str
    fun str(): String = str

    companion object {
        internal fun fromJson(json: String): Key =
            Key.values().firstOrNull {
                json == it.str
            }
                ?: throw IllegalArgumentException("Fant ingen Key med verdi som matchet '$json'.")
    }

}

interface IKey {
    open val str: String
}
interface IDataFelt : IKey{
}


fun JsonMessage.value(key: Key): JsonNode =
    this[key.str]

fun JsonMessage.valueNullable(key: Key): JsonNode? =
    value(key).takeUnless(JsonNode::isMissingOrNull)

fun JsonMessage.valueNullableOrUndefined(key: Key): JsonNode? =
    try { value(key).takeUnless(JsonNode::isMissingOrNull) } catch (e: IllegalArgumentException) { null }


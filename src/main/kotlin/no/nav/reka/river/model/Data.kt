package no.nav.reka.river.model

import com.fasterxml.jackson.databind.JsonNode
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.*
import no.nav.reka.river.InternalBehov
import no.nav.reka.river.InternalEvent

class Data(val event: MessageType.Event, private val jsonMessage: JsonMessage) : Message,TxMessage  {

    init {
        packetValidator.validate(jsonMessage)
        jsonMessage.demandValue(Key.EVENT_NAME.str(),event.value)
    }
    companion object {
        val packetValidator = River.PacketValidation {
            it.demandKey(Key.EVENT_NAME.str())
            it.rejectKey(Key.BEHOV.str())
            it.demandKey(Key.DATA.str())
            it.rejectKey(Key.FAIL.str())
            it.interestedIn(Key.UUID.str())
        }

        fun create(event: MessageType.Event, map: Map<IDataFelt, Any> = emptyMap() ) : Data {
            return Data(event, JsonMessage.newMessage(event.value, mapOf(Key.DATA.str() to "") + map.mapKeys { it.key.str }))
        }

        fun create(jsonMessage: JsonMessage) : Data {
            return Data(InternalEvent(jsonMessage[Key.EVENT_NAME.str()].asText()), jsonMessage)
        }

    }

    override operator fun get(key: IKey): JsonNode =  jsonMessage[key.str]

    override operator fun set(key: IKey, value: Any) { jsonMessage[key.str] = value }

    override fun uuid() = jsonMessage[Key.UUID.str()].asText()

    override fun toJsonMessage(): JsonMessage {
        return jsonMessage
    }

}
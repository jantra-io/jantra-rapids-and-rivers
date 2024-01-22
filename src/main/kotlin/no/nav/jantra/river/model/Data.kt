package no.nav.jantra.river.model

import com.fasterxml.jackson.databind.JsonNode
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.River
import no.nav.jantra.river.*
import no.nav.jantra.river.InternalEvent

class Data internal constructor(val event: MessageType.Event, private val jsonMessage: JsonMessage) : Message,TxMessage  {

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
            it.interestedIn(Key.RIVER_ID.str())
        }

        fun create(event: MessageType.Event, map: Map<IDataFelt, Any> = emptyMap() ) : Data {
            return Data(event, JsonMessage.newMessage(event.value, mapOf(Key.DATA.str() to "") + map.mapKeys { it.key.str }))
        }

        fun create(jsonMessage: JsonMessage) : Data {
            return Data(InternalEvent(jsonMessage[Key.EVENT_NAME.str()].asText()), jsonMessage)
        }

    }

    fun createBehov(behov: MessageType.Behov,map: Map<IDataFelt, Any>): Behov {
        return Behov(event, behov, JsonMessage.newMessage(event.value,mapOf(Key.BEHOV.str() to behov.value) + mapOfNotNull(Key.RIVER_ID.str() to riverId()) + map.mapKeys { it.key.str }))
    }

    override operator fun get(key: IKey): JsonNode =  jsonMessage[key.str]

    override operator fun set(key: IKey, value: Any) { jsonMessage[key.str] = value }

    override fun riverId() = jsonMessage[Key.RIVER_ID.str()].asText()

    override fun toJsonMessage(): JsonMessage {
        return jsonMessage
    }

}
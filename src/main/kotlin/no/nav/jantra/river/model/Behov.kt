package no.nav.jantra.river.model

import com.fasterxml.jackson.databind.JsonNode
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.River
import no.nav.jantra.river.IDataFelt
import no.nav.jantra.river.IKey
import no.nav.jantra.river.InternalBehov
import no.nav.jantra.river.InternalEvent
import no.nav.jantra.river.Key
import no.nav.jantra.river.MessageType
import no.nav.jantra.river.interestedIn
import no.nav.jantra.river.mapOfNotNull

class Behov internal constructor(val event: MessageType.Event,
            val behov: MessageType.Behov,
            private val jsonMessage: JsonMessage) : Message,TxMessage {

    init {
        packetValidator.validate(jsonMessage)
        jsonMessage.demandValue(Key.EVENT_NAME.str(),event.value)
        jsonMessage.demandValue(Key.BEHOV.str(),behov.value)
    }
    companion object {
        val packetValidator = River.PacketValidation {
            it.demandKey(Key.EVENT_NAME.str())
            it.demandKey(Key.BEHOV.str())
            it.rejectKey(Key.DATA.str())
            it.rejectKey(Key.FAIL.str())
            it.interestedIn(Key.RIVER_ID.str())
            it.interestedIn(Key.EVENT_TIME)
            it.interestedIn(Key.EVENT_ORIGIN)
        }

        fun create(event: MessageType.Event, behov: MessageType.Behov ,map: Map<IKey, Any> = emptyMap() ) : Behov {
            return Behov(event, behov ,JsonMessage.newMessage(event.value, mapOf(Key.BEHOV.str() to behov.value) + map.mapKeys { it.key.str }))
        }

        fun create(jsonMessage: JsonMessage) : Behov {
            return Behov(InternalEvent(jsonMessage[Key.EVENT_NAME.str()].asText()), InternalBehov(jsonMessage[Key.BEHOV.str()].asText()), jsonMessage)
        }

    }

    override operator fun get(key: IKey): JsonNode =  jsonMessage[key.str]

    override operator fun set(key: IKey, value: Any) { jsonMessage[key.str] = value }

    fun createData(map: Map<IDataFelt, Any>): Data {
        return Data(event, JsonMessage.newMessage(event.value, mapOfNotNull(Key.DATA.str() to "", Key.RIVER_ID.str() to riverId()) + map.mapKeys { it.key.str }))
    }

    fun createFail(feilmelding:String, data: Map<IKey,Any> = emptyMap()) : Fail {
        return Fail.create(event, behov,feilmelding , mapOfNotNull(Key.RIVER_ID to riverId()) + data.mapKeys { it.key as IKey })
    }

    fun createBehov(behov: MessageType.Behov,data: Map<IKey, Any>) : Behov {
        return Behov(this.event,behov, JsonMessage.newMessage(event.value, mapOfNotNull(Key.RIVER_ID.str() to riverId(),Key.BEHOV.str() to behov.value) + data.mapKeys { it.key.str }))
    }

    override fun riverId() = jsonMessage[Key.RIVER_ID.str()].asText()

    override fun toJsonMessage(): JsonMessage {
        return jsonMessage
    }


}
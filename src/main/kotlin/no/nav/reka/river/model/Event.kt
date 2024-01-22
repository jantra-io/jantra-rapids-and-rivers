package no.nav.reka.river.model

import com.fasterxml.jackson.databind.JsonNode
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.River
import no.nav.helse.rapids_rivers.isMissingOrNull
import no.nav.reka.river.IDataFelt
import no.nav.reka.river.IKey
import no.nav.reka.river.InternalEvent
import no.nav.reka.river.MessageType
import no.nav.reka.river.Key
import no.nav.reka.river.interestedIn
import no.nav.reka.river.mapOfNotNull

class Event(val event:MessageType.Event, private val jsonMessage:JsonMessage, val clientId: String?=null) : Message, TxMessage {

    @Transient var riverId:String? = null

    init {
        packetValidator.validate(jsonMessage)
        jsonMessage.demandValue(Key.EVENT_NAME.str(),event.value)
    }
    companion object {
        val packetValidator = River.PacketValidation {
            it.demandKey(Key.EVENT_NAME.str())
            it.rejectKey(Key.BEHOV.str())
            it.rejectKey(Key.DATA.str())
            it.rejectKey(Key.FAIL.str())
            it.rejectKey(Key.RIVER_ID.str())
            it.interestedIn(Key.RIVER_ORIGIN.str)
            it.interestedIn(Key.CLIENT_ID.str)
            it.interestedIn(Key.EVENT_TIME)
            it.interestedIn(Key.APP_KEY)
        }

        fun create(event:MessageType.Event,clientId: String? = null, map: Map<IKey, Any> = emptyMap() ) : Event {
            return Event(event, JsonMessage.newMessage(event.value, mapOfNotNull(Key.CLIENT_ID.str() to clientId) + map.mapKeys { it.key.str }))
        }
        fun create(event:MessageType.Event, map: Map<IKey, Any> = emptyMap() ) : Event {
            return create(event, null ,map = map)
        }
        fun create(jsonMessage: JsonMessage) : Event {
            val event = InternalEvent(jsonMessage[Key.EVENT_NAME.str()].asText())
            val clientID = jsonMessage[Key.CLIENT_ID.str()].takeUnless { it.isMissingOrNull() }?.asText()
            return Event(event, jsonMessage, clientID)
        }
    }

    override operator fun get(key: IKey): JsonNode =  jsonMessage[key.str]

    override operator fun set(key: IKey, value: Any) { jsonMessage[key.str] = value }

    fun createBehov(behov: MessageType.Behov,map: Map<IDataFelt, Any>): Behov {
        return Behov(event, behov, JsonMessage.newMessage(event.value, mapOfNotNull(Key.BEHOV.str() to behov.value, Key.RIVER_ID.str() to riverId, Key.EVENT_ORIGIN.str() to jsonMessage.id) + map.mapKeys { it.key.str }))
    }

    override fun riverId() = this.riverId?:""

    override fun toJsonMessage(): JsonMessage {
       return jsonMessage
    }
}
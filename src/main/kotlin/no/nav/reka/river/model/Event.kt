package no.nav.reka.river.model

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.IDataFelt
import no.nav.reka.river.IKey
import no.nav.reka.river.InternalEvent
import no.nav.reka.river.MessageType
import no.nav.reka.river.Key

class Event(val event:MessageType.Event, private val jsonMessage:JsonMessage, val clientId: String?=null) : Message, TxMessage {

    @Transient var uuid:String? = null

    init {
        packetValidator.validate(jsonMessage)
        jsonMessage.demandValue(Key.EVENT_NAME.str(),event.name)
    }
    companion object {
        val packetValidator = River.PacketValidation {
            it.demandKey(Key.EVENT_NAME.str())
            it.rejectKey(Key.BEHOV.str())
            it.rejectKey(Key.DATA.str())
            it.rejectKey(Key.FAIL.str())
            it.rejectKey(Key.UUID.str())
            it.interestedIn(Key.TRANSACTION_ORIGIN.str)
            it.interestedIn(Key.CLIENT_ID.str)
        }

        fun create(event:MessageType.Event, map: Map<IKey, Any> = emptyMap() ) : Event {
            return Event(event, JsonMessage.newMessage(event.name,map.mapKeys { it.key.str }))
        }
        fun create(jsonMessage: JsonMessage) : Event {
            val event = InternalEvent(jsonMessage[Key.EVENT_NAME.str()].asText())
            val clientID = jsonMessage[Key.CLIENT_ID.str()]?.asText()
            return Event(event, jsonMessage, clientID)
        }
    }

    operator fun get(key: IKey): JsonNode =  jsonMessage[key.str]

    operator fun set(key: IKey, value: Any) { jsonMessage[key.str] = value }

    fun createBehov(behov: MessageType.Behov,map: Map<IDataFelt, Any>): Behov {
        return Behov(event, behov, JsonMessage.newMessage(event.name,mapOf(Key.BEHOV.str() to behov.name) + map.mapKeys { it.key.str }))
    }

    override fun uuid() = this.uuid?:""

    override fun toJsonMessage(): JsonMessage {
       return jsonMessage
    }
}
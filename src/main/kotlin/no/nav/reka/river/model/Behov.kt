package no.nav.reka.river.model

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.IDataFelt
import no.nav.reka.river.IKey
import no.nav.reka.river.InternalBehov
import no.nav.reka.river.InternalEvent
import no.nav.reka.river.Key
import no.nav.reka.river.MessageType

class Behov(private val event: MessageType.Event,
            private val behov: MessageType.Behov,
            private val jsonMessage: JsonMessage) : Message,TxMessage {

    init {
        packetValidator.validate(jsonMessage)
        jsonMessage.demandValue(Key.EVENT_NAME.str(),event.name)
        jsonMessage.demandValue(Key.BEHOV.str(),behov.name)
    }
    companion object {
        val packetValidator = River.PacketValidation {
            it.demandKey(Key.EVENT_NAME.str())
            it.demandKey(Key.BEHOV.str())
            it.rejectKey(Key.DATA.str())
            it.rejectKey(Key.FAIL.str())
            it.interestedIn(Key.UUID.str())
        }

        fun create(event: MessageType.Event, behov: MessageType.Behov ,map: Map<IKey, Any> = emptyMap() ) : Behov {
            return Behov(event, behov ,JsonMessage.newMessage(event.name, mapOf(Key.BEHOV.str() to behov.name) + map.mapKeys { it.key.str }))
        }

        fun create(jsonMessage: JsonMessage) : Behov {
            return Behov(InternalEvent(jsonMessage[Key.EVENT_NAME.str()].asText()), InternalBehov(jsonMessage[Key.BEHOV.str()].asText()), jsonMessage)
        }

    }

    fun createData(map: Map<IDataFelt, Any>): Data {
        return Data(event, JsonMessage.newMessage(event.name, mapOf(Key.DATA.str() to "") + map.mapKeys { it.key.str }))
    }

    fun createFail(feilmelding:String, data: Map<IKey,Any>) : Fail {
        return Fail.create(event, behov,feilmelding ,data.mapKeys { it.key as IKey })
    }

    override fun uuid() = jsonMessage[Key.UUID.str()].asText()

    override fun toJsonMessage(): JsonMessage {
        return jsonMessage
    }


}
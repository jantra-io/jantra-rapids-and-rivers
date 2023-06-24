package no.nav.reka.river.model

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.IDataFelt
import no.nav.reka.river.Key
import no.nav.reka.river.MessageType

class Data(val event: MessageType.Event, private val jsonMessage: JsonMessage) : Message,TxMessage  {

    init {
        packetValidator.validate(jsonMessage)
        jsonMessage.demandValue(Key.EVENT_NAME.str(),event.name)
    }
    companion object {
        val packetValidator = River.PacketValidation {
            it.demandKey(Key.EVENT_NAME.str())
            it.rejectKey(Key.BEHOV.str())
            it.demandKey(Key.DATA.str())
            it.rejectKey(Key.FAIL.str())
            it.interestedIn(Key.UUID.str())
        }

        fun create(event: MessageType.Event, behov: MessageType.Behov, map: Map<IDataFelt, Any> = emptyMap() ) : Data {
            return Data(event, JsonMessage.newMessage(event.name, map.mapKeys { it.key.str }))
        }

    }

    override fun uuid() = jsonMessage[Key.UUID.str()].asText()

    override fun toJsonMessage(): JsonMessage {
        return jsonMessage
    }

}
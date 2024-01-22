package no.nav.reka.river.model

import com.fasterxml.jackson.databind.JsonNode
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.River
import no.nav.helse.rapids_rivers.isMissingOrNull
import no.nav.reka.river.*
import no.nav.reka.river.InternalBehov
import no.nav.reka.river.InternalEvent

class Fail internal constructor(val event: MessageType.Event,
           val behov: MessageType.Behov? = null,
           val feilmelding: String,
           private val jsonMessage: JsonMessage) : Message, TxMessage {

    init {
        packetValidator.validate(jsonMessage)
        jsonMessage.demandValue(Key.EVENT_NAME.str(),event.value)
    }
    companion object {
        val packetValidator = River.PacketValidation {
            it.demandKey(Key.EVENT_NAME.str())
            it.rejectKey(Key.BEHOV.str())
            it.rejectKey(Key.DATA.str())
            it.demandKey(Key.FAIL.str())
            it.interestedIn(Key.UUID.str())
            it.interestedIn(Key.FAILED_BEHOV)
            it.interestedIn(Key.EVENT_TIME)
        }

        fun create(event:MessageType.Event, behov: MessageType.Behov? = null,feilmelding:String, data: Map<IKey, Any> = emptyMap() ) : Fail {
            return Fail(event, behov, feilmelding,
                jsonMessage =  JsonMessage.newMessage(event.value, data.mapKeys { it.key.str }).also {
                    if (behov!=null) it[Key.FAILED_BEHOV.str()] = behov.value
                    it[Key.FAIL.str()] = feilmelding
                })
        }

        fun create(jsonMessage: JsonMessage) : Fail {
            val behov = jsonMessage[Key.FAILED_BEHOV.str]
                        .takeUnless { it.isMissingOrNull()}
                        ?.let {
                            InternalBehov(it.asText())
                        }


            return Fail(InternalEvent(jsonMessage[Key.EVENT_NAME.str()].asText()), behov, jsonMessage[Key.FAIL.str].asText(), jsonMessage)
        }
    }

    override operator fun get(key: IKey): JsonNode =  jsonMessage[key.str]

    override operator fun set(key: IKey, value: Any) { jsonMessage[key.str] = value }

     fun createBehov(behov: MessageType.Behov,map: Map<IDataFelt, Any>): Behov {
        return Behov(event, behov, JsonMessage.newMessage(event.value, mapOfNotNull(Key.BEHOV.str() to behov.value, Key.UUID.str() to uuid(), Key.EVENT_ID.str() to jsonMessage.id) + map.mapKeys { it.key.str }))
    }

    override fun uuid(): String {
        return jsonMessage[Key.UUID.str()].asText()
    }

    override fun toJsonMessage(): JsonMessage {

        return jsonMessage
    }
}
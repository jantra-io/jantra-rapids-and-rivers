package no.nav.reka.river.model

import com.fasterxml.jackson.databind.JsonNode
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.River
import no.nav.helse.rapids_rivers.isMissingOrNull
import no.nav.reka.river.*
import no.nav.reka.river.InternalBehov
import no.nav.reka.river.InternalEvent

class Fail(val event: MessageType.Event,
           val behov: MessageType.Behov? = null,
           val feilmelding: String,
           val uuid: String? = null,
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
        }

        fun create(event:MessageType.Event, behov: MessageType.Behov? = null,feilmelding:String, data: Map<IKey, Any> = emptyMap() ) : Fail {
            return Fail(event, behov, feilmelding,
                jsonMessage =  JsonMessage.newMessage(event.value, data.mapKeys { it.key.str }).also {
                    if (behov!=null) it[Key.FAILED_BEHOV.str()] = behov.value
                    it[Key.FAIL.str()] = feilmelding
                })
        }

        fun create(jsonMessage: JsonMessage) : Fail {
            val behovText = jsonMessage[Key.FAILED_BEHOV.str]?.asText()
            val behov = jsonMessage[Key.FAILED_BEHOV.str]
                        .takeUnless { it.isMissingOrNull() }
                        ?.mapNotNull {
                            InternalBehov(it.asText())
                        }
                        ?.first()
            val uuid  = jsonMessage[Key.UUID.str()]
                        .takeUnless { it[Key.UUID.str].isMissingOrNull() }
                        ?.mapNotNull {
                            it.asText()
                        }
                        ?.first()


            return Fail(InternalEvent(jsonMessage[Key.EVENT_NAME.str()].asText()), behov, jsonMessage[Key.FAIL.str].asText(), jsonMessage[Key.UUID.str].asText(), jsonMessage)
        }
    }

    override operator fun get(key: IKey): JsonNode =  jsonMessage[key.str]

    override operator fun set(key: IKey, value: Any) { jsonMessage[key.str] = value }

    override fun uuid(): String {
        return uuid!!
    }

    override fun toJsonMessage(): JsonMessage {

        return jsonMessage
    }
}
package no.nav.helsearbeidsgiver.felles.rapidsrivers

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.InternalEvent
import no.nav.reka.river.Key
import no.nav.reka.river.MessageType
import no.nav.reka.river.composite.DataKanal
import no.nav.reka.river.model.Fail
import no.nav.reka.river.redis.IRedisStore
import no.nav.reka.river.redis.RedisKey
import org.slf4j.LoggerFactory

class StatefullDataKanal(
    private val dataFelter: Array<String>,
    override val eventName: MessageType.Event,
    private val mainListener: River.PacketListener,
    rapidsConnection: RapidsConnection,
    val redisStore: IRedisStore
) : DataKanal(
    rapidsConnection
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun accept(): River.PacketValidation {
        return River.PacketValidation {
            it.demandValue(Key.EVENT_NAME.str, eventName.value)
            it.demandKey(Key.DATA.str)
            dataFelter.forEach { datafelt ->
                it.interestedIn(datafelt)
            }
        }
    }

    override fun onData(packet: JsonMessage) {
        if (packet[Key.UUID.str].asText().isNullOrEmpty()) {
            log.error("TransaksjonsID er ikke initialisert for ${packet.toJson()}")
            rapidsConnection.publish(
                Fail.create(InternalEvent(packet[Key.EVENT_NAME.str()].asText()),
                    feilmelding = "TransaksjonsID / UUID kan ikke vare tom da man bruker Composite Service" )
                    .toJsonMessage().toJson()
            )
        } else if (collectData(packet)) {
            log.info("data collected for event ${eventName.value} med packet ${packet.toJson()}")
            mainListener.onPacket(packet, rapidsConnection)
        } else {
            log.warn("Mangler data for $packet")
            // @TODO fiks logging logger.warn("Unrecognized package with uuid:" + packet[Key.UUID.str])
        }
    }

    private fun collectData(message: JsonMessage): Boolean {
        // Akkuratt nå bare svarer med 1 data element men kan svare med mange
        val data = dataFelter.filter { dataFelt ->
            !message[dataFelt].isMissingNode
        }.map { dataFelt ->
            Pair(dataFelt, message[dataFelt])
        }.ifEmpty {
            return false
        }.first()
        val str = if (data.second.isTextual) { data.second.asText() } else data.second.toString()
        redisStore.set(message[Key.UUID.str].asText() + data.first, str)
        return true
    }

    fun isAllDataCollected(key: RedisKey): Boolean {
        return redisStore.exist(*dataFelter.map { key.toString() + it }.toTypedArray()) == dataFelter.size.toLong()
    }
    fun isDataCollected(vararg keys: RedisKey): Boolean {
        return redisStore.exist(*keys) == keys.size.toLong()
    }
}

package no.nav.reka.river.composite

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.*
import no.nav.reka.river.InternalEvent
import no.nav.reka.river.model.Data
import no.nav.reka.river.model.Fail
import no.nav.reka.river.redis.IRedisStore
import no.nav.reka.river.redis.RedisKey
import org.slf4j.LoggerFactory

class StatefullDataKanal(
    eventName: MessageType.Event,
    private val dataFelter: Array<IKey>,
    private val mainListener: MessageListener,
    val redisStore: IRedisStore,
    val rapidsConnection: RapidsConnection
) : DataKanal(eventName
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

    override fun onData(data: Data) {
        if (data.riverId().isNullOrEmpty()) {
            log.error("TransaksjonsID er ikke initialisert for ${data.toJsonMessage().toJson()}")
            rapidsConnection.publish(
                Fail.create(InternalEvent(data.event.value),
                    feilmelding = "TransaksjonsID / UUID kan ikke vare tom da man bruker Composite Service" )
                    .toJsonMessage().toJson()
            )
        } else if (collectData(data)) {
            log.info("data collected for event ${eventName.value} med packet ${data.toJsonMessage().toJson()}")
            mainListener.onMessage(data)
        } else {
            log.warn("Mangler data for $data")
            // @TODO fiks logging logger.warn("Unrecognized package with uuid:" + packet[Key.UUID.str])
        }
    }

    private fun collectData(message: Data): Boolean {
        // Akkuratt nå bare svarer med 1 data element men kan svare med mange
        val data = dataFelter.filter { dataFelt ->
            !message[dataFelt].isMissingNode
        }.map { dataFelt ->
            Pair(dataFelt, message[dataFelt])
        }.ifEmpty {
            return false
        }.first()
        val str = if (data.second.isTextual) { data.second.asText() } else data.second.toString()
        redisStore.set(message[Key.RIVER_ID].asText() + data.first, str)
        return true
    }

    fun isAllDataCollected(key: RedisKey): Boolean {
        return redisStore.exist(*dataFelter.map { key.toString() + it }.toTypedArray()) == dataFelter.size.toLong()
    }

}

package no.nav.reka.river.composite

import com.fasterxml.jackson.databind.JsonNode
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.IDataFelt
import no.nav.reka.river.MessageType
import no.nav.reka.river.model.Event
import no.nav.reka.river.redis.IRedisStore
import org.slf4j.LoggerFactory
import java.util.UUID

class StatefullEventKanal(
    val redisStore: IRedisStore,
    override val event: MessageType.Event,
    private val dataFelter: Array<IDataFelt>,
    override val mainListener: MessageListener,
    rapidsConnection: RapidsConnection
) : AbstractDelegatingEventListener(
    mainListener,
    rapidsConnection
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun accept(): River.PacketValidation {
        return River.PacketValidation {
            it.interestedIn(*dataFelter.map { it.str }.toTypedArray())
        }
    }

    private fun collectData(packet: Event) {
        val transactionId = UUID.randomUUID().toString()
        packet.uuid = transactionId

        dataFelter.map { dataFelt ->
            Pair(dataFelt, packet[dataFelt])
        }.forEach { data ->
            val str = if ((data.second as JsonNode).isTextual) { data.second?.asText()?:"" } else data.second.toString()
            redisStore.set(transactionId + data.first, str)
        }
    }
    override fun onEvent(packet: Event) {
        log.info("Statefull event listener for event ${event.value}" + " med paket  ${packet.toString()}")
        collectData(packet)
        mainListener.onMessage(packet)
    }
}

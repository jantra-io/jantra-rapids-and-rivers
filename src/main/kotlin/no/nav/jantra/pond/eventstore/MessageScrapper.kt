package no.nav.jantra.pond.eventstore


import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.jantra.pond.eventstore.db.EventStoreRepo
import no.nav.jantra.pond.eventstore.db.RiverRepo
import no.nav.jantra.river.IBehovListener
import no.nav.jantra.river.IFailListener
import no.nav.jantra.river.model.Behov
import no.nav.jantra.river.model.Event
import no.nav.jantra.river.model.Fail


internal class EventScrapper(val rapidsConnection: RapidsConnection,val eventStore:EventStoreRepo,val keyProvider:(Event) -> String) : River.PacketListener {


     fun start() {
        configure(
            River(rapidsConnection)
        ).register(this)
    }

    private fun configure(river: River): River {
        return river.validate {
            Event.packetValidator.validate(it)
        }
    }
    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        val event = Event.create(packet)
        val key = keyProvider.invoke(event)
        eventStore.put(key, event)

    }


}

internal class BehovScrapper(val riverStore: RiverRepo) : IBehovListener {
    override fun onBehov(behov: Behov) {
        if (behov.behov.value == "create-river") {
             println("Invoked")
            riverStore.createRiver(behov)
        }
        else {
            riverStore.put(behov)
        }

    }


}

internal class FailScrapper(val riverStore: RiverRepo) : IFailListener {
    override fun onFail(fail: Fail) {
        riverStore.put(fail)
    }

}


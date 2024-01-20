package no.nav.reka.pond.eventstore


import no.nav.helse.rapids_rivers.River
import no.nav.reka.pond.eventstore.db.EventStoreRepo
import no.nav.reka.pond.eventstore.db.RiverRepo
import no.nav.reka.river.IBehovListener
import no.nav.reka.river.IEventListener
import no.nav.reka.river.IFailListener
import no.nav.reka.river.model.Behov
import no.nav.reka.river.model.Event
import no.nav.reka.river.model.Fail


class EventScrapper(val eventStore:EventStoreRepo,val keyProvider:(Event) -> String) : IEventListener {
    override fun onEvent(event: Event) {
        val key = keyProvider.invoke(event)
        eventStore.put(key, event)
    }

    override fun accept() = River.PacketValidation {}

}

class BehovScrapper(val riverStore: RiverRepo) : IBehovListener {
    override fun onBehov(behov: Behov) {
        riverStore.put(behov)
    }

    override fun accept() = River.PacketValidation {}

}

class FailScrapper(val riverStore: RiverRepo) : IFailListener {
    override fun onFail(fail: Fail) {
        riverStore.put(fail)
    }

    override fun accept() = River.PacketValidation {}

}


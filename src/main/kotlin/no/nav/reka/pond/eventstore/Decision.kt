package no.nav.reka.pond.eventstore

import no.nav.reka.river.model.Event

interface Decision {

    fun decide(predicate: (Event, IEventStoreObs) -> Boolean)
}
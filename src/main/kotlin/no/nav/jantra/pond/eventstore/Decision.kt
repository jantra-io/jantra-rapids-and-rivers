package no.nav.jantra.pond.eventstore

import no.nav.jantra.river.model.Event

interface Decision {

    fun decide(predicate: (Event, IEventStoreObs) -> Boolean)
}
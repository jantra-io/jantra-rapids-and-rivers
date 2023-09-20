package no.nav.reka.river

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.model.Behov
import no.nav.reka.river.model.Data
import no.nav.reka.river.model.Event

class Rapid(val rapidsConnection: RapidsConnection) {
    fun publishEvent(event: Event) {
        rapidsConnection.publish(event.toJsonMessage().toJson())
    }

    fun publishBehov(behov: Behov)  {
        rapidsConnection.publish(behov.toJsonMessage().toJson())
    }

    fun publishData(data: Data)  {
        rapidsConnection.publish(data.toJsonMessage().toJson())
    }
}
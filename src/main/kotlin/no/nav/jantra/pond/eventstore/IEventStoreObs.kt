package no.nav.jantra.pond.eventstore

import no.nav.jantra.river.MessageType
import no.nav.jantra.river.model.Event
import no.nav.jantra.river.model.Message
import java.util.UUID

interface IEventStoreObs {

    fun put(event:Event)

    fun get(uuid:UUID): List<Message>

    fun findByOrigin(origin:UUID,eventName: MessageType.Event? = null): List<Message>
    fun findByAppKey(applicationKet:String, eventName: MessageType.Event? = null): List<Message>



}
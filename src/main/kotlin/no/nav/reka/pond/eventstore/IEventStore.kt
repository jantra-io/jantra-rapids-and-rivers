package no.nav.reka.pond.eventstore

import no.nav.reka.river.MessageType
import no.nav.reka.river.model.Event
import no.nav.reka.river.model.Message
import java.util.UUID

interface IEventStore {

    fun put(event:Event)

    fun get(uuid:UUID): List<Message>

    fun findByOrigin(origin:UUID,eventName: MessageType.Event? = null): List<Message>
    fun findByAppKey(applicationKet:String, eventName: MessageType.Event? = null): List<Message>



}
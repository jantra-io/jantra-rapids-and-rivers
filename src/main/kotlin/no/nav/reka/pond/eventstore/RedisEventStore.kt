package no.nav.reka.pond.eventstore

import io.lettuce.core.RedisClient
import no.nav.reka.river.MessageType
import no.nav.reka.river.model.Event
import no.nav.reka.river.model.Message
import org.slf4j.LoggerFactory
import java.util.*

class RedisEventStore(redisUrl:String) : IEventStore {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val redisClient = redisUrl.let(RedisClient::create)
    private val connection = redisClient.connect()
    private val syncCommands = connection.sync()
    override fun put(event: Event) {
        syncCommands.set(event.uuid()!!, event.toJsonMessage().toJson())
    }

    override fun get(uuid: UUID): List<Message> {
        TODO("Not yet implemented")
    }


    override fun findByOrigin(origin: UUID, eventName: MessageType.Event?): List<Message> {
        TODO("Not yet implemented")
    }

    override fun findByAppKey(applicationKet: String, eventName: MessageType.Event?): List<Message> {
        TODO("Not yet implemented")
    }

}
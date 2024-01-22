package no.nav.jantra.river.redis

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.lettuce.core.RedisClient
import io.lettuce.core.SetArgs
import org.slf4j.LoggerFactory

class RedisStore(redisUrl: String) : IRedisStore {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val redisClient = redisUrl.let(RedisClient::create)
    private val connection = redisClient.connect()
    private val syncCommands = connection.sync()

    override fun set(key: String, value: String?, ttl: Long) {
        log.debug("Setting in redis: $key -> $value")
        syncCommands.set(key, value, SetArgs().ex(ttl))
    }

    override fun set(key: RedisKey, value: String?, ttl: Long) {
        log.debug("Setting in redis: $key -> $value")
        syncCommands.set(key.toString(), value, SetArgs().ex(ttl))
    }

    override fun set(key: RedisKey, value: JsonNode?, ttl: Long) {
        log.debug("Setting in redis: $key -> $value")
        syncCommands.set(key.toString(), value.toString(), SetArgs().ex(ttl))
    }

    override fun get(key: String): String? {
        val value = syncCommands.get(key)
        log.debug("Getting from redis: $key -> $value")
        return value
    }

    override fun get(key: RedisKey): String? {
        val value = syncCommands.get(key.toString())
        log.debug("Getting from redis: $key -> $value")
        return value
    }

    override fun <T : Any> get(key: RedisKey, clazz: Class<T>): T? {
        val value = syncCommands.get(key.toString())
        if (value.isNullOrEmpty()) return null else return jacksonObjectMapper().readValue(value, clazz)
    }

    override fun exist(vararg keys: String): Long {
        val count = syncCommands.exists(*keys)
        log.debug("Checking exist in redis: ${keys.contentToString()} -> $count")
        return count
    }

    override fun exist(vararg keys: RedisKey): Long {
        val count = syncCommands.exists(*keys.map { it.toString() }.toTypedArray())
        log.debug("Checking exist in redis: ${keys.contentToString()} -> $count")
        return count
    }

    override fun shutdown() {
        connection.close()
        redisClient.shutdown()
    }
}

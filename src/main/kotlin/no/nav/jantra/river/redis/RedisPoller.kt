package no.nav.jantra.river.redis

import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import kotlinx.coroutines.delay

// TODO Bruke kotlin.Result istedenfor exceptions?
class RedisPoller(redisUrl:String) {
    private val redisClient = RedisClient.create(
        redisUrl
    )
    private lateinit var connection: StatefulRedisConnection<String, String>
    private lateinit var syncCommands: RedisCommands<String, String>

    private fun redisCommand(): RedisCommands<String, String> {
        if (!::connection.isInitialized) {
            connection = redisClient.connect()
            syncCommands = connection.sync()
        }
        return syncCommands
    }

    suspend fun hent(key: String, maxRetries: Int = 10, waitMillis: Long = 500): String {
        return getString(key, maxRetries, waitMillis)
    }

    suspend fun getString(key: String, maxRetries: Int, waitMillis: Long): String {
        repeat(maxRetries) {
            if (redisCommand().exists(key.toString()) == 1.toLong()) {
                return syncCommands.get(key.toString())
            }
            delay(waitMillis)
        }

        throw RedisPollerTimeoutException(key)
    }
}

sealed class RedisPollerException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

class RedisPollerJsonParseException(message: String, cause: Throwable) : RedisPollerException(message, cause)

class RedisPollerTimeoutException(uuid: String) : RedisPollerException(
    "Brukte for lang tid på å svare ($uuid)."
)

package no.nav.reka.river.redis

import no.nav.reka.river.IDataFelt
import no.nav.reka.river.MessageType

sealed class RedisKey(open val uuid: String) {
    abstract override fun toString(): String

    companion object {
        fun dataKey(uuid: String, dataFelt: IDataFelt): RedisKey {
            return DataKey(uuid, dataFelt)
        }

        fun clientKey(uuid: String): RedisKey {
            return ClientKey(uuid)
        }

        fun transactionKey(uuid: String, eventname: MessageType.Event): RedisKey {
            return TransactionKey(uuid, eventname)
        }
    }
}

private data class DataKey(override val uuid: String, val datafelt: IDataFelt) : RedisKey(uuid) {
    override fun toString(): String {
        return uuid + datafelt
    }
}

private data class TransactionKey(override val uuid: String, val eventName: MessageType.Event) : RedisKey(uuid) {
    override fun toString(): String {
        return uuid + eventName.value
    }
}

private data class ClientKey(override val uuid: String) : RedisKey(uuid) {
    override fun toString(): String {
        return uuid
    }
}

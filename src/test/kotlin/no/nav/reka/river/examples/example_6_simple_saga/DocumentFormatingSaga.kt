package no.nav.reka.river.examples.example_6_simple_saga

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helsearbeidsgiver.felles.rapidsrivers.composite.Transaction
import no.nav.reka.river.MessageType
import no.nav.reka.river.Rapid
import no.nav.reka.river.composite.Saga
import no.nav.reka.river.examples.example_1_basic_løser.BehovName
import no.nav.reka.river.examples.example_1_basic_løser.DataFelt
import no.nav.reka.river.mapOfNotNull
import no.nav.reka.river.model.Data
import no.nav.reka.river.model.Event
import no.nav.reka.river.model.Fail
import no.nav.reka.river.model.TxMessage
import no.nav.reka.river.publish
import no.nav.reka.river.redis.RedisKey
import no.nav.reka.river.redis.RedisStore

class DocumentFormatingSaga(val event: MessageType.Event, redisStore: RedisStore, rapid: RapidsConnection) : Saga(event,redisStore, rapid) {



    override fun dispatchBehov(message: TxMessage, transaction: Transaction) {
        if (transaction == Transaction.NEW) {
            val event = message as Event
            val rawDocument = redisStore.get(RedisKey.dataKey(message.uuid(),DataFelt.RAW_DOCUMENT))!!
            rapid.publish(event.createBehov(BehovName.FORMAT_DOCUMENT,
                                            mapOf(DataFelt.RAW_DOCUMENT to rawDocument)))
            rapid.publish(event.createBehov(BehovName.FORMAT_DOCUMENT_IBM, mapOf(DataFelt.RAW_DOCUMENT to rawDocument)))
        }
        else if(isDataCollected(*step1data(message.uuid()))) {
            takeIf { message is Data }.apply {
                (message as Data).createBehov(BehovName.PERSIST_DOCUMENT, mapOfNotNull(
                    DataFelt.FORMATED_DOCUMENT to  redisStore.get(RedisKey.dataKey(message.uuid(),DataFelt.FORMATED_DOCUMENT)).takeIf { it!="" },
                    DataFelt.FORMATED_DOCUMENT_IBM to redisStore.get(RedisKey.dataKey(message.uuid(),DataFelt.FORMATED_DOCUMENT_IBM))!!)
                ).also {
                    rapid.publish(it)
                }
            }

        }
    }

    override fun onError(feil: Fail): Transaction {
        if (feil.behov!!.equals(BehovName.FORMAT_DOCUMENT))
            return Transaction.IN_PROGRESS.also { redisStore.set(RedisKey.dataKey(feil.uuid!!,DataFelt.FORMATED_DOCUMENT),null as? String) }

        return Transaction.TERMINATE
    }



    override fun finalize(message: TxMessage) {

    }

    override fun terminate(message: TxMessage) {
        
    }

    private fun step1data(uuid: String): Array<RedisKey> = arrayOf(
        RedisKey.dataKey(uuid, DataFelt.RAW_DOCUMENT),
        RedisKey.dataKey(uuid, DataFelt.FORMATED_DOCUMENT),
        RedisKey.dataKey(uuid, DataFelt.FORMATED_DOCUMENT_IBM)
    )


}
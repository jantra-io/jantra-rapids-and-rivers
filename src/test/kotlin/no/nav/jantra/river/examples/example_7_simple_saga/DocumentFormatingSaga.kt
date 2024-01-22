package no.nav.jantra.river.examples.example_7_simple_saga

import no.nav.helsearbeidsgiver.felles.rapidsrivers.composite.Transaction
import no.nav.jantra.river.MessageType
import no.nav.jantra.river.composite.Saga
import no.nav.jantra.river.examples.example_1_basic_løser.BehovName
import no.nav.jantra.river.examples.example_1_basic_løser.DataFelt
import no.nav.jantra.river.mapOfNotNull
import no.nav.jantra.river.model.Data
import no.nav.jantra.river.model.Event
import no.nav.jantra.river.model.Fail
import no.nav.jantra.river.model.TxMessage
import no.nav.jantra.river.publish
import no.nav.jantra.river.redis.RedisKey

class DocumentFormatingSaga(val event: MessageType.Event) : Saga(event) {



    override fun dispatchBehov(message: TxMessage, transaction: Transaction) {
        if (transaction == Transaction.NEW) {
            val event = message as Event
            val rawDocument = redisStore.get(RedisKey.dataKey(message.riverId(),DataFelt.RAW_DOCUMENT))!!
            rapid.publish(event.createBehov(BehovName.FORMAT_DOCUMENT,
                                            mapOf(DataFelt.RAW_DOCUMENT to rawDocument)))
            rapid.publish(event.createBehov(BehovName.FORMAT_DOCUMENT_IBM, mapOf(DataFelt.RAW_DOCUMENT to rawDocument)))
        }
        else if(isDataCollected(*step1data(message.riverId()))) {
            message.takeIf { message is Data }
                ?.let { message as Data }!!
                .let {
                    it.createBehov(BehovName.PERSIST_DOCUMENT, mapOfNotNull(
                        DataFelt.FORMATED_DOCUMENT to  redisStore.get(RedisKey.dataKey(message.riverId(),DataFelt.FORMATED_DOCUMENT)).takeIf { it!="" },
                        DataFelt.FORMATED_DOCUMENT_IBM to redisStore.get(RedisKey.dataKey(message.riverId(),DataFelt.FORMATED_DOCUMENT_IBM))!!)
                ).also {
                    rapid.publish(it)
                }
            }

        }
    }

    override fun onError(feil: Fail): Transaction {
        if (feil.behov!!.equals(BehovName.FORMAT_DOCUMENT))
            return Transaction.IN_PROGRESS.also { redisStore.set(RedisKey.dataKey(feil.riverId(),DataFelt.FORMATED_DOCUMENT),null as? String) }

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
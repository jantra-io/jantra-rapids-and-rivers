package no.nav.reka.river.examples.example_8_retrieving_data_from_client.services

import no.nav.helsearbeidsgiver.felles.rapidsrivers.composite.Transaction
import no.nav.reka.river.MessageType
import no.nav.reka.river.composite.Saga
import no.nav.reka.river.examples.example_1_basic_løser.BehovName
import no.nav.reka.river.examples.example_1_basic_løser.DataFelt
import no.nav.reka.river.examples.example_1_basic_løser.EventName
import no.nav.reka.river.mapOfNotNull
import no.nav.reka.river.model.Data
import no.nav.reka.river.model.Event
import no.nav.reka.river.model.Fail
import no.nav.reka.river.model.TxMessage
import no.nav.reka.river.publish
import no.nav.reka.river.redis.RedisKey

class DocumentFormatingSaga(event: MessageType.Event) : Saga(event) {



    override fun dispatchBehov(message: TxMessage, transaction: Transaction) {
        if (transaction == Transaction.NEW) {
            val event = message as Event
            val rawDocument = redisStore.get(RedisKey.dataKey(message.uuid(),DataFelt.RAW_DOCUMENT))!!
            rapid.publish(event.createBehov(BehovName.FORMAT_DOCUMENT,
                                            mapOf(DataFelt.RAW_DOCUMENT to rawDocument)))
            rapid.publish(event.createBehov(BehovName.FORMAT_DOCUMENT_IBM, mapOf(DataFelt.RAW_DOCUMENT to rawDocument)))
        }
        else if(isDataCollected(*step1data(message.uuid()))) {
            message.takeIf { message is Data }
                ?.let { message as Data }!!
                .let {
                    it.createBehov(BehovName.PERSIST_DOCUMENT, mapOfNotNull(
                        DataFelt.FORMATED_DOCUMENT to  redisStore.get(RedisKey.dataKey(message.uuid(),DataFelt.FORMATED_DOCUMENT)).takeIf { it!="" },
                        DataFelt.FORMATED_DOCUMENT_IBM to redisStore.get(RedisKey.dataKey(message.uuid(),DataFelt.FORMATED_DOCUMENT_IBM))!!)
                ).also {
                    rapid.publish(it)
                }
            }

        }
    }

    override fun onError(feil: Fail): Transaction {
        if (feil.behov!!.equals(BehovName.FORMAT_DOCUMENT)) {
            redisStore.set(feil.uuid(),"Unabled to format document. Proceeding with IBM formatter")
            return Transaction.IN_PROGRESS.also { redisStore.set(RedisKey.dataKey(feil.uuid!!,DataFelt.FORMATED_DOCUMENT),null as? String) }
        }


        return Transaction.TERMINATE
    }



    override fun finalize(message: TxMessage) {
        val formatedDokument = redisStore.get(RedisKey.dataKey(message.uuid(),DataFelt.FORMATED_DOCUMENT))
        val feil = redisStore.get(RedisKey.feilKey(message.uuid()))
        val resultat =
            """{
                "formatteddocumen":"$formatedDokument",
                "feil":"$feil"
        }""".trimIndent()
        val clientId = redisStore.get(RedisKey.transactionKey(message.uuid(),this.eventName))

        redisStore.set(RedisKey.clientKey(clientId!!),resultat)
    }

    override fun terminate(message: TxMessage) {
        
    }

    private fun step1data(uuid: String): Array<RedisKey> = arrayOf(
        RedisKey.dataKey(uuid, DataFelt.RAW_DOCUMENT),
        RedisKey.dataKey(uuid, DataFelt.FORMATED_DOCUMENT),
        RedisKey.dataKey(uuid, DataFelt.FORMATED_DOCUMENT_IBM)
    )


}
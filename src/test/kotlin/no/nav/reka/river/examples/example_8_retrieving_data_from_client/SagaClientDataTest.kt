package no.nav.reka.river.examples.example_8_retrieving_data_from_client

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.contains
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.EndToEndTest
import no.nav.reka.river.Key
import no.nav.reka.river.examples.example_1_basic_løser.BehovName
import no.nav.reka.river.examples.example_1_basic_løser.DataFelt
import no.nav.reka.river.examples.example_1_basic_løser.EventName
import no.nav.reka.river.examples.example_8_retrieving_data_from_client.frontend.DocumentRecievedProducer
import no.nav.reka.river.model.Event
import no.nav.reka.river.redis.RedisStore
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SagaClientDataTest() : EndToEndTest() {
 val RAW_DOCUMNET = "This is my raw document"
    val ILLEGAL_CHARACTER = "%"

    @BeforeEach
    fun clean() {
        this.meldinger.clear()
        this.messages.clear()
    }
    @Test
    fun `2 step saga example`() {

        val resultat = DocumentRecievedProducer(this.rapid,this.redisPoller).publish("My unformatted focument")
         val jsonNode = ObjectMapper().readTree(resultat)
        println(resultat)


    }


    override val appBuilder: (rapidConnection: RapidsConnection, redisStore: RedisStore) -> RapidsConnection
        get() = {rapid: RapidsConnection, redisStore: RedisStore -> rapid.`client retrieving data SAGA`(redisStore) }
}
package no.nav.reka.river

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.prometheus.client.CollectorRegistry
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helsearbeidsgiver.inntektsmelding.integrasjonstest.filter.findMessage
import no.nav.reka.river.redis.RedisStore
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class EndToEndTest : ContainerTest(), RapidsConnection.MessageListener {

    private lateinit var thread: Thread

    private val logger = LoggerFactory.getLogger(this::class.java)

    lateinit var redisStore: RedisStore

    private val rapid by lazy {
        RapidApplication.create(
            mapOf(
                "KAFKA_RAPID_TOPIC" to TOPIC,
                "KAFKA_CREATE_TOPICS" to TOPIC,
                "RAPID_APP_NAME" to "HAG",
                "KAFKA_BOOTSTRAP_SERVERS" to kafkaContainer.bootstrapServers,
                "KAFKA_CONSUMER_GROUP_ID" to "HAG"
            )
        )
    }



    val meldinger = mutableListOf<JsonNode>()
    val results = mutableListOf<String>()


    var filterMessages: (JsonNode) -> Boolean = { true }

    @BeforeAll
    fun beforeAllEndToEnd() {
        redisStore = RedisStore(redisContainer.redisURI)
/*
        rapid.buildApp(
            redisStore,
            database,
            imRepository,
            forespoerselRepository,
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            dokarkivClient,
            mockk(relaxed = true),
            arbeidsgiverNotifikasjonKlient,
            NOTIFIKASJON_LINK,
            priProducer,
            altinnClient,
            mockk(relaxed = true)
        )
        */

        rapid.register(this)
        thread = thread {
            rapid.start()
        }
        Thread.sleep(2000)
    }

    fun resetMessages() {
        meldinger.clear()
        results.clear()
    }

    override fun onMessage(message: String, context: MessageContext) {
        logger.info("onMessage: $message")
        if (filterMessages.invoke(jacksonObjectMapper().readTree(message))) {
            results.add(message)
        }
        meldinger.add(jacksonObjectMapper().readTree(message))
    }

    fun filter(event: MessageType.Event, behovType: MessageType.Behov? = null, datafelt: MessageType.Data? = null): List<JsonNode> {
        return findMessage(meldinger, event, behovType, datafelt)
    }

    @AfterAll
    fun afterAllEndToEnd() {
        CollectorRegistry.defaultRegistry.clear()
        rapid.stop()
        thread.interrupt()
        logger.info("Stopped")
    }

    fun publish(value: Any) {
        val json = jacksonObjectMapper().writeValueAsString(value)
        println("Publiserer melding: $json")
        rapid.publish(json)
    }

    fun getMessages(t: (JsonNode) -> Boolean): List<JsonNode> {
        return results.map { jacksonObjectMapper().readTree(it) }.filter(t).toList()
    }

    fun getMessage(index: Int): JsonNode {
        return jacksonObjectMapper().readTree(results[index + 1])
    }

    fun getMessageCount(): Int {
        return results.size - 1
    }
}

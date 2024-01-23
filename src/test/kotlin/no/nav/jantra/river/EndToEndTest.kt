package no.nav.jantra.river

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.contains
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.prometheus.client.CollectorRegistry
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.isMissingOrNull
import no.nav.helsearbeidsgiver.inntektsmelding.integrasjonstest.filter.findMessage
import no.nav.jantra.river.examples.example_1_basic_løser.BehovName
import no.nav.jantra.river.examples.example_1_basic_løser.EventName
import no.nav.jantra.river.model.Message
import no.nav.jantra.river.redis.RedisStore
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread
import no.nav.jantra.river.redis.RedisPoller


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class EndToEndTest : ContainerTest(), RapidsConnection.MessageListener {

    private lateinit var thread: Thread

    private val logger = LoggerFactory.getLogger(this::class.java)

    lateinit var redisStore: RedisStore
    lateinit var redisPoller: RedisPoller

    val rapid by lazy {
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

    abstract val appBuilder: (rapidConnection:RapidsConnection,redisStore: RedisStore) -> RapidsConnection



    val meldinger = mutableListOf<JsonNode>()
    val messages: Messages = Messages()


    @BeforeAll
    fun beforeAllEndToEnd() {
        redisStore = RedisStore(redisContainer.redisURI)
        redisPoller = RedisPoller(redisContainer.redisURI)
       // rapid.buildApp(redisStore)
        appBuilder.invoke(rapid,redisStore)
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
    }

    override fun onMessage(message: String, context: MessageContext) {
        logger.info("onMessage: $message")

        meldinger.add(jacksonObjectMapper().readTree(message))
        messages.add(jacksonObjectMapper().readTree(message))
    }

    fun filter(event: MessageType.Event, behovType: MessageType.Behov? = null,datafelt: IDataFelt? = null): List<JsonNode> {
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

    fun publish(value: Message) {
        rapid.publish(value.toJsonMessage().toJson())
    }



    fun getMessageCount(): Int {
        return messages.unwrap().size
    }
}


class Messages(private val messages: MutableList<JsonNode> = mutableListOf()) {

    private val anyEvent: (JsonNode) -> Boolean = {it.contains(Key.EVENT_NAME.str())}

    fun add(message:JsonNode) {
        messages.add(message)
    }

    fun clear() = messages.clear()

    fun withEventName(event: EventName) : Messages {
        return Messages(messages.filter { it[Key.EVENT_NAME.str()].asText() == event.value }.toMutableList())
    }

    fun withBehovName(behov: BehovName) : Messages {
        return Messages(messages.filter {  it[Key.BEHOV.str()]!=null && !it[Key.BEHOV.str()].isMissingOrNull() }.filter { it[Key.BEHOV.str()].asText() == behov.value }.toMutableList())
    }

    fun withAnyEvent() : Messages {
        return Messages(messages.filter { it.contains(Key.EVENT_NAME.str()) }.toMutableList())
    }

    fun withData(datafelter: List<IDataFelt>) : Messages {
        return Messages(messages.filter(anyEvent).filter {jsonNode ->
            datafelter.count { datafelt ->
                jsonNode.contains(datafelt.str)
        } == datafelter.size }.toMutableList())
    }

    fun single() : JsonNode {
        return when (messages.size) {
            0 -> throw NoSuchElementException("List is empty.")
            1 -> messages.first()
            else -> throw IllegalArgumentException("List has more than one element.")
        }
    }

    fun allDataMessages(): Messages {
        return Messages(messages.filter { it.contains(Key.DATA.str()) }.toMutableList())
    }

    fun unwrap():List<JsonNode> = messages.map { it }.toList()
}


fun pause() = Thread.sleep(5000)

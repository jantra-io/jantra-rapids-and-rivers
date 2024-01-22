package no.nav.jantra.river

import com.redis.testcontainers.RedisContainer
import com.zaxxer.hikari.HikariConfig
import no.nav.jantra.pond.eventstore.db.EVENTSTORE_TABLE
import no.nav.jantra.pond.eventstore.db.Database
import no.nav.jantra.pond.eventstore.db.RIVER_EVENT
import no.nav.jantra.pond.eventstore.db.RIVER_TABLE
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import java.util.Properties

open class ContainerTest {
    // Containers
    val kafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"))
    val redisContainer = RedisContainer(RedisContainer.DEFAULT_IMAGE_NAME.withTag(RedisContainer.DEFAULT_TAG))
    lateinit var db: Database
    var postgreSQLContainer = PostgreSQLContainer<Nothing>("postgres:14")

    val TOPIC = "helsearbeidsgiver.inntektsmelding"

    @BeforeAll
    fun startContainers() {
        println("Starter containerne...")
        println("Starter Kafka...")
        kafkaContainer.start()
        val props = Properties()
        props.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.bootstrapServers)
        val adminClient: AdminClient = AdminClient.create(props)
        adminClient.createTopics(listOf(NewTopic(TOPIC, 1, 1.toShort())))
        println("Starter Redis...")
        redisContainer.start()
        postgreSQLContainer = eventStorePostgres()
        db = Database(postgreSQLContainer.testConfiguration())
            .configureFlyway()
        val tables = listOf(EVENTSTORE_TABLE,RIVER_TABLE,RIVER_EVENT)
        transaction (db.db) {
            tables.forEach { it.deleteAll() }
        }
        println("Containerne er klare!")
    }

    @AfterAll
    fun stopContainers() {
        println("Stopper containere...")
        kafkaContainer.stop()
        redisContainer.stop()
        postgreSQLContainer.stop()
        println("Containere er stoppet!")
    }

/*
    @BeforeAll
    fun beforeEach() {
        postgreSQLContainer = eventStorePostgres()
        db = Database(postgreSQLContainer.testConfiguration())
            .configureFlyway()
        val tables = listOf(EVENTSTORE_TABLE)
        transaction (db.db) {
            tables.forEach { it.deleteAll() }
        }
    }

 */

    fun eventStorePostgres(): PostgreSQLContainer<Nothing> =
    PostgreSQLContainer<Nothing>("postgres:14").apply {
        withReuse(true)
        withLabel("app-navn", "cpa-repo")
        start()
        println(
            "Databasen er startet opp, portnummer: $firstMappedPort, jdbcUrl: jdbc:postgresql://localhost:$firstMappedPort/test, credentials: test og test"
        )
    }
}

private fun Database.configureFlyway(): Database =
    also {
        Flyway.configure()
            .dataSource(it.dataSource)
            .failOnMissingLocations(true)
            .cleanDisabled(false)
            .load()
            .also(Flyway::clean)
            .migrate()
    }

fun PostgreSQLContainer<Nothing>.testConfiguration(): HikariConfig {
     return HikariConfig().apply {
        jdbcUrl = this@testConfiguration.jdbcUrl
        username = this@testConfiguration.username
        password = this@testConfiguration.password
        maximumPoolSize = 5
        minimumIdle = 1
        idleTimeout = 500001
        connectionTimeout = 10000
        maxLifetime = 600001
        initializationFailTimeout = 5000
    }
}

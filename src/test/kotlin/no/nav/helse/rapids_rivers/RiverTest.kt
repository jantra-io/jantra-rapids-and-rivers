package no.nav.helse.rapids_rivers

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.util.*

internal class RiverTest {

    @Test
    internal fun `sets id if missing`() {
        river.onMessage("{}", context)
        assertTrue(gotMessage)
        assertDoesNotThrow { gotPacket.id.toUUID() }
    }

    @Test
    internal fun `sets custom id if missing`() {
        val expected = "notSoRandom"
        river = configureRiver(River(rapid) { expected })
        river.onMessage("{}", context)
        assertTrue(gotMessage)
        assertEquals(expected, gotPacket.id)
    }

    @Test
    internal fun `invalid json`() {
        river.onMessage("invalid json", context)
        assertFalse(gotMessage)
    }

    @Test
    internal fun `no validations`() {
        river.onMessage("{}", context)
        assertTrue(gotMessage)
    }


    private val context = object : MessageContext {
        override fun publish(message: String) {}
        override fun publish(key: String, message: String) {}
        override fun rapidName(): String {return "test"}
    }

    private var gotMessage = false
    private lateinit var gotPacket: JsonMessage
    private lateinit var river: River
    private val rapid = object : RapidsConnection() {
        override fun publish(message: String) {}

        override fun publish(key: String, message: String) {}
        override fun rapidName(): String {
            return "test"
        }

        override fun start() {}

        override fun stop() {}
    }

    @BeforeEach
    internal fun setup() {
        river = configureRiver(River(rapid))
    }

    private fun configureRiver(river: River): River =
        river.register(object : River.PacketListener {
            override fun onPacket(packet: JsonMessage, context: MessageContext) {
                gotPacket = packet
                gotMessage = true
            }


        })
}

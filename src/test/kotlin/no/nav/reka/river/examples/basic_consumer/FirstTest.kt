package no.nav.reka.river.examples.basic_consumer

import no.nav.reka.river.EndToEndTest
import no.nav.reka.river.examples.BehovName
import no.nav.reka.river.examples.EventName
import no.nav.reka.river.model.Behov
import org.junit.Assert
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("My First test")
class FirstTest : EndToEndTest() {

    @org.junit.jupiter.api.Test
    fun `trigger simple løser`() {

        val needFullName = Behov.create(EventName.APPLICATION_INITIATED,
                                        BehovName.FULL_NAME,
                                        mapOf(DataFelt.APPLICATION_ID to "123"))
        this.publish(needFullName)
        Thread.sleep(5000)
        with(filter(EventName.APPLICATION_INITIATED, datafelt = DataFelt.NAME).first()) {
          Assert.assertEquals(this[DataFelt.NAME.str].asText(), "Alexander Petrov")
        }

    }
}
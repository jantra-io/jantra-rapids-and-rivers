package no.nav.reka.river.examples.basic_consumer

import no.nav.reka.river.IDataFelt
import no.nav.reka.river.MessageType

enum class Need(str: String) : MessageType.Behov {
    FULL_NAME("full-name")
}
enum class DataFelt(override val str: String) : IDataFelt {
    APPLICATION_ID("123"),
    NAME("name")
}
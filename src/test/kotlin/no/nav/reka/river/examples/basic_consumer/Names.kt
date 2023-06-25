package no.nav.reka.river.examples.basic_consumer

import no.nav.reka.river.IDataFelt
import no.nav.reka.river.MessageType


enum class EventName(override val value: String)  : MessageType.Event{
    APPLICATION_INITIATED("registration-started"),
    APPLICATION_RECIEVED("registration-accepted"),

    DOCUMENT_RECIEVED("document-recieved"),
    DOCUMENT_PERSISTED("document-persisted");
    override fun toString() : String {
        return value
    }
}
enum class BehovName(override val value: String) : MessageType.Behov {
    FULL_NAME("full-name"),
    FORMAT_DOCUMENT("format-document"),
    FORMAT_XML("format-xml"),
    FORMAT_JSON("format-json"),
    PERSIST_DOCUMENT("persist-document");
    override fun toString() : String {
        return value
    }
}
enum class DataFelt(override val str: String) : IDataFelt {
    APPLICATION_ID("123"),
    FORMATED_DOCUMENT("formated-document"),
    RAW_DOCUMENT("raw-document"),
    DOCUMENT_REFERECE("document-reference"),
    NAME("name");
    override fun toString() : String {
        return str
    }
}
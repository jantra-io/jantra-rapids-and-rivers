package no.nav.reka.pond.eventstore

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object EVENTSTORE_TABLE : Table("eventstore") {
    val id: Column<String> = varchar("cpa_id",256)
    val uuid: Column<String>  = varchar("uuid",256)
    val originUuid: Column<String> = varchar("uuid", 256)
    val appKey: Column<String> = varchar("application_key",256)
    val eventName: Column<String> = varchar("event_name", length = 50)
    val behovName: Column<String> = varchar("behov_name", length = 50)
    val eventtime: Column<LocalDateTime> = datetime("eventtime")
    val message = text("message")
}
package no.nav.reka.pond.eventstore.db

import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Local
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.math.BigDecimal
import java.time.LocalDateTime

object EVENTSTORE_TABLE : Table("eventstore") {
    val id: Column<String> = varchar("id",256)
    val originRiver: Column<String> = varchar("origin_river", 256)
    val appKey: Column<String> = varchar("application_key",256)
    val eventName: Column<String> = varchar("event_name", length = 50)
    val eventTime: Column<LocalDateTime> = datetime("event_time")
    val message = text("message")
}

object RIVER_EVENT : Table("event_to_river") {
    val id: Column<ULong> = ulong("id")
    val eventId: Column<String> = varchar("event_id",256)
    val riverId: Column<String> = varchar("river_id",256)
}

object RIVER_TABLE : Table("riverstore") {
    val id: Column<ULong> = ulong("id")
    val riverId: Column<String> = varchar("river_id",256)
  //  val eventId: Column<String> = varchar("event_id",256)
    val eventName: Column<String> = varchar("event_name",50)
    val behovName: Column<String?> = varchar("behov_name",50).nullable()
    val isFail: Column<Boolean> = bool("is_fail")
    val eventTime: Column<LocalDateTime> = datetime("event_time")
    val message = text("message")

}
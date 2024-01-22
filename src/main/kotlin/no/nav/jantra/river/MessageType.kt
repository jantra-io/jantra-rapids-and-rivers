package no.nav.jantra.river

interface MessageType {
    open val value: String
    interface Event : MessageType
    interface Behov : MessageType
    interface Data : MessageType
    interface Fail : MessageType
    override fun toString(): String
}

internal class InternalEvent(override val value: String) : MessageType.Event {
    override fun toString(): String {
        return this.value
    }

    override fun equals(other: Any?) : Boolean {
        if (other !is MessageType.Event) return false
        return this.value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

}


internal class InternalBehov(override val value: String) : MessageType.Behov {
    override fun toString(): String {
        return this.value
    }

    override fun equals(other: Any?) : Boolean {
        if (other !is MessageType.Behov) return false
        return this.value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

}



internal class InternalData(override val value: String) : MessageType.Data {
    override fun toString(): String {
        TODO("Not yet implemented")
    }

}

internal class InternalFeil(override val value: String) : MessageType.Fail {
    override fun toString(): String {
        TODO("Not yet implemented")
    }

}




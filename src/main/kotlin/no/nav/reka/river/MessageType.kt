package no.nav.reka.river

interface MessageType {
    open val name: String
    interface Event : MessageType
    interface Behov : MessageType
    interface Data : MessageType
    interface Fail : MessageType

    fun equals(name: MessageType) : Boolean {
        return this == name
    }

}

internal class InternalEvent(override val name: String) : MessageType.Event {

}
internal class InternalBehov(override val name: String) : MessageType.Behov {

}

internal class InternalData(override val name: String) : MessageType.Data {

}

internal class InternalFeil(override val name: String) : MessageType.Fail {

}




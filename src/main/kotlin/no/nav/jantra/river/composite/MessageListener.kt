package no.nav.jantra.river.composite

import no.nav.jantra.river.model.Message

interface MessageListener {


    fun onMessage(message: Message)
}
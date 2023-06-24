package no.nav.reka.river.composite

import no.nav.reka.river.model.Message

interface MessageListener {

    fun onMessage(message: Message)
}
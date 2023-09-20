package no.nav.reka.river.composite

import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.model.Message

interface MessageListener {


    fun onMessage(message: Message)
}
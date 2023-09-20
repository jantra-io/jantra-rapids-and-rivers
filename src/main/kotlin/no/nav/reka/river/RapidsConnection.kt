package no.nav.reka.river

import no.nav.helse.rapids_rivers.RapidApplication

val rapidsConnection = RapidApplication
    .create(System.getenv())
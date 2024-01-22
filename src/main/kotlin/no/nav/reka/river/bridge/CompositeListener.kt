package no.nav.reka.river.bridge

import no.nav.reka.river.Rapid
import no.nav.reka.river.IDataListener
import no.nav.reka.river.IEventListener
import no.nav.reka.river.IFailListener

interface CompositeListener : IEventListener, IDataListener, IFailListener {
}
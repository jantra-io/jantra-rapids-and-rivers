package no.nav.jantra.river.bridge

import no.nav.jantra.river.IDataListener
import no.nav.jantra.river.IEventListener
import no.nav.jantra.river.IFailListener

interface CompositeListener : IEventListener, IDataListener, IFailListener {
}
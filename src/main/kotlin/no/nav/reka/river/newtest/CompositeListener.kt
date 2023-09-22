package no.nav.reka.river.newtest

import no.nav.reka.river.Rapid
import no.nav.reka.river.IDataListener
import no.nav.reka.river.IEventListener
import no.nav.reka.river.IFailListener

abstract class CompositeListener(val rapid: Rapid) : IEventListener, IDataListener, IFailListener {

}
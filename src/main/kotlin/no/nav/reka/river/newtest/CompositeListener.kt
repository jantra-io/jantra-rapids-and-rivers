package no.nav.reka.river.newtest

import no.nav.reka.river.Rapid
import no.nav.reka.river.test.IDataListener
import no.nav.reka.river.test.IEventListener
import no.nav.reka.river.test.IFailListener

abstract class CompositeListener(val rapid: Rapid) : IEventListener,IDataListener,IFailListener {

}
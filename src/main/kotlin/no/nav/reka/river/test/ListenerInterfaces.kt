package no.nav.reka.river.test

import no.nav.reka.river.model.Data
import no.nav.reka.river.model.Event
import no.nav.reka.river.model.Fail

interface IEventListener {

    fun onEvent(event: Event)
}


interface  IFailListener {
    fun onFail(fail: Fail)

}

interface IDataListener {

    fun onData(data: Data)
}


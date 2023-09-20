package no.nav.reka.river.configuration

import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.model.Event
import no.nav.reka.river.model.Fail
import no.nav.reka.river.test.IEventListener
import no.nav.reka.river.test.IFailListener

class ExampleListenere: IEventListener, IFailListener {
    override fun onEvent(event: Event) {
        TODO("Not yet implemented")
    }

    override fun onFail(fail: Fail) {
        TODO("Not yet implemented")
    }

    override fun accept(): River.PacketValidation {
        TODO("Not yet implemented")
    }


}
fun test() {

}
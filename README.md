# JANTRA rapids and rivers interpretation

## Motivation
JANTRA is acknowledging NAV implementation of rapids and rivers pattern as a Rapid provider. On top it provides an opinionated intepretation of the Rapids and Rivers (and Ponds) pattern.

## Event model

There are 4 types of messages EVENT, NEED, DATA and FAIL. Originaly @fred george talks about Solution as a responce to a NEED. In practice very often a microservice is wrapping a integration method and high level response object 
is not practicle. A solution can represent a DATA it can represent a FAIL or it can represent both , so it is a kind of superposition between the two and Jantra does not find it suitable as a response to a simple NEED.
Here is a simple layout of the different messages:

![Event](/doc/messagetype.jpg)

| Message Type | Required Keys       |
|--------------|---------------------|
| Event        | event-name          |
| NEED         | event-name<br/>need |
| DATA         | event-name<br/>data |
| Fail         | event-name<br/>fail |

Each model class ([Event](https://github.com/jantra-io/jantra-river-pond/blob/main/src/main/kotlin/no/nav/jantra/river/model/Event.kt),[Need](https://github.com/jantra-io/jantra-river-pond/blob/main/src/main/kotlin/no/nav/jantra/river/model/Need.kt),[Data](https://github.com/jantra-io/jantra-river-pond/blob/main/src/main/kotlin/no/nav/jantra/river/model/Data.kt),[Fail](https://github.com/jantra-io/jantra-river-pond/blob/main/src/main/kotlin/no/nav/jantra/river/model/Fail.kt)) defines its own specification in the form of message validator. Here is example of a Event specification:
```
 companion object {
        val packetValidator = River.PacketValidation {
            it.demandKey(Key.EVENT_NAME.str())
            it.rejectKey(Key.BEHOV.str())
            it.rejectKey(Key.DATA.str())
            it.rejectKey(Key.FAIL.str())
            it.rejectKey(Key.RIVER_ID.str())
            it.interestedIn(Key.RIVER_ORIGIN.str)
            it.interestedIn(Key.CLIENT_ID.str)
            it.interestedIn(Key.EVENT_TIME)
            it.interestedIn(Key.APP_KEY)
        }
```
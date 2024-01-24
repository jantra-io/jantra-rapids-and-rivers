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

## The Rapid, the River and The Pond
JANTRA opinion about the RAPID is that it does not hold knowledge about what kind of message it holds. From the RAPID perspective only messages exist, without any constraints. Only after a message is consumed by a River it becomes clear that is an Event, 
NEED(Behov), Data or FAIL The different messages have different nature. An event makes sense only in relation to the POND. Why ? Because it is static it is a Fact and it is unchangeable. At the same time a NEED makes sense in the context of a River, same goes for the Data.
The data is not a single source of thruth as all Data may be a subject to change and it may be invalidated or discarded.
## The River
In contrast to NAV implementation JANTRA consideres all messages between two events to be part of a River. Each River is assigned a unique identifier and each Event can spawn multiple Rivers ending up with one or more Events.


# JANTRA rapids and rivers pattern interpretation

## Examples
If you are impatient you can accept this as a form of event buss and jump to the examples https://github.com/jantra-io/jantra-river-pond/tree/main/src/test/kotlin/no/nav/jantra/river/examples

## Motivation
JANTRA is acknowledging NAV implementation of rapids and rivers pattern as a Rapid provider. On top it provides an opinionated intepretation of the Rapids and Rivers (and Ponds) pattern.

## Event model

There are 4 types of messages this interpretation defines EVENT, NEED, DATA and FAIL. Originaly @fred george talks about Solution as a responce to a NEED. Practice in NAV shows that the microservices very often are tiny, they may wrap a single integration method and a high level response object such as Solution is not practicle. A solution can represent a DATA it can represent a FAIL or it can represent both , so it is a kind of superposition between the two and Jantra does not find it suitable as a response to a simple NEED. On the other hand a 
Solution is ok as a high level response to a River (JANTRA consideres everything that happens between two consequent events to be part of the same River) execution.
Here is a simple layout of the different messages:

![Event](/doc/messagetype.jpg)

| Message Type | Required Keys       |
|--------------|---------------------|
| Event        | event-name          |
| NEED         | event-name<br/>need |
| DATA         | event-name<br/>data |
| Fail         | event-name<br/>fail |

All message types share a single key - event-name. The motivation is that every Need, Data or Fail is derived from an originaly occuring event.
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
The data is not a single source of thruth as all Data may be a subject to change and it may be invalidated or discarded.One such example is when a River terminates with FAIL. All data messages are then invalid.
## The River
In contrast to NAV implementation JANTRA consideres all messages between two events to be part of a River. Each River is assigned a unique identifier and each Event can spawn multiple Rivers ending up with one or more Events.
## The Pond
JANTRA implements the pond as an Event store. In addition it holds a log of the River execution.
## Identity managment
Each message has a set of keys.

| Message Type           | Identity     | Description                                                                                                                                                                               |
|------------------------|--------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Message(every message) | key          | Unique identifier for each message type                                                                                                                                                   |
| Event                  | river-id     | In the context of Event riverID is assigned when the message is consumed by the river.<br/> riverID is never persisted on the rapid together with the Event                               |
|                        | river-origin | Identying the river that has spawned the Event                                                                                                                                            |
| NEED(Behov)            | river-id     | river-id is aways persistent for a NEED Behov and it is assigned once a NEED is constructed from an Event                                                                                 |
|                        | event-id     | points to the Event key that has spawned the NEED(Behov)                                                                                                                                  |
|  Other types of keys   | client-id    | It is used to retrieve a result from a River execution from a Client or API. In this implementation once a River is executed the solution is positioned<br/> in Redis under the client-id | 

![Event](/doc/identity-managment.jpg)



# torganized-play-users

> What man is a man who does not make the world better.
>
> -- Balian, Kingdom of Heaven

## Abstract

TBD

## License

The license for the software is LGPL 3.0 or newer.

## Structure

The SCS is a maven multi module build.

Module | Description | Deployment
----|----|----
dcis-users-amqp | The asynchronous part of the dcis-users SCS. It listens to AMQP for it's actions. | Java Library
dcis-users | The synchronous part of the dcis-users. The web ui and the APIs provided resides in this deployment. | Java Library
torganized-play-users-api | The model for the users part. It depends on spring but not on spring-boot. | OCI Container
torganized-play-users-persistence | The persistence layer. It depends on spring-data-jpa. | OCI Container

## Architecture

tl;dr (ok, only the bullshit bingo words):

* Immutable Objects
* Relying heavily on generated code
* 100 % test coverage of human generated code
* Every line of code not written is bug free!

Code test coverage for human generated code should be 100%, machine generated code is considered bug free until proven wrong.
But every line that needs not be written is a bug free line without need to test it. So aim for not writing code.

## A note from the author

If someone is interested in getting it faster, we may team up.
I'm open for that.
But be warned: I want to do it _right_.
So no short cuts to get faster.
And be prepared for some basic discussions about the architecture or software design :-).

---
Berlin, 2024-06-01
Bensheim, 2024-10-06

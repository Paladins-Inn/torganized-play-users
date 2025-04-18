# dcis-users

> What man is a man who does not make the world better.
>
> -- Balian, Kingdom of Heaven

[![Build Containers](https://github.com/Paladins-Inn/torganized-play-users/actions/workflows/build-artifacts.yaml/badge.svg)](https://github.com/Paladins-Inn/torganized-play-users/actions/workflows/build-artifacts.yaml)

## Abstract

This SCS manages all user centric data and actions.
Users need to register to the SSO provider configured in the applications.
So no direct user credentials are managed here.
But the user action log and the ratings of players and GMs are stored within this SCS.

It provides a  WebUI and Components and an AMQP sink for asynchronous work.

## License

The license for the software is LGPL 3.0 or newer.

## Structure

The build is done as pure java maven based build on github actions.
The result is then copied via docker into the Kaiserpfalz EDV-Service java runner image.
The SCS is a maven multi module build.


## Architecture

tl;dr (ok, only the bullshit bingo words):

* Self Contained Systems
* Event Driven
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

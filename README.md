## [Wiki](https://github.com/entangledloops/heuristicSearch/wiki/Semiprime-Factorization) ##

## Downloads ##

#### [Download the Semiprime Factorization Client (latest stable)](https://github.com/entangledloops/semiprime/blob/master/build/semiprime-client.jar?raw=true) ####

#### [Download the Semiprime Factorization Server (latest stable)](https://github.com/entangledloops/semiprime/blob/master/build/semiprime-server.jar?raw=true) ####

#### [View Global Semiprime Factorization Server Status](https://semiprime.azurewebsites.net) ####

Build from source if you want the very latest.

You do **not** need the server unless you are attempting to run a private distributed key factorization. 
The client is capable of running a local factorization as well as contribute to the current global factorization effort, so this may be enough for your own needs.

## What is this? ##

This software attempts to factor a semiprime (typically a public key, such as RSA) into its prime factors (reproducing the private key) using heuristic search methods. 

The client can be used to either contribute toward an [ongoing cloud effort](https://semiprime.azurewebsites.net) or a local search hosted on a private server.

## Screenshots ##

![Search Tab](http://www.entangledloops.com/img/semiprime/search-0.4.4a.png)

![Connect Tab](http://www.entangledloops.com/img/semiprime/connect-0.4.4a.png)

## Run Requirements ##

You need at least [Java 1.8](https://www.java.com/en/download/) installed.

## Build Requirements ##

If you want to build from source, you'll need to install the [Java 1.8 JRE](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) or later.

If you want to build from source, you'll need to install [Java 1.8 JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) or later.

Pull this git repo:

`git clone https://github.com/entangledloops/semiprime/semiprime.git`

Open a command prompt/terminal in the SemiprimeFactor subdirectory.
To run the client:

**Linux / OS X:**

`./gradlew desktop:client`

**Windows:**

`gradlew desktop:client`

**Other tasks available:**

`desktop:server` - hosts a server

`wrapper` - updates gradle wrapper 

Be sure to run `createWrapper` from the parent directory so gradle files can be overwritten. A copy of gradle w/only this task is provided in the `heuristicSearch` dir to make this easier for you.

# play2-neo4j [![Build Status](https://travis-ci.org/sim51/play2-neo4j.png?branch=master)](https://travis-ci.org/sim51/play2-neo4j)

A dedicated Neo4J module for the Play Framework! 2.


## Goals

The very first intent of this plugin is to help developers bootstrapping a web application using Play 2, scala and Neo4J. So it's main focus won't (at first) be put on the communication layer, but on the application layer.

Thus, examples of important features will be:

* starting a fresh neo4j with the Play application comes for free
* for domain types, the persistence and query layers will be available for free (using helper and most probably macros)
* a dedicated user interface will be generated to manage your domain data and to browse it

Warning! don't be confusing, this module doesn't want to restrit the application to be end-to-end DDD. But it will reduce boilerplate when DDD is needed. In a broader sense, the Neo4J layers won't be hidden!

## Getting Started

The simplest way to get started with this module is to:

* clone the repo
* build the module in *module* using SBT
* use the Play2 application in *samples* as a template

The sample will by default create a node when using the [http://localhost:9000/](http://localhost:9000/) URL. 

Also, a 2.0 Neo4J instance will run on the 7575 port with its web admin interface available on [http://localhost:7575/browser](http://localhost:7575/browser)

## Configuration

### Module configuration

#### Embedded Neo4j server (default)

By default (ie. without configuration), this module start an embedded database that is available at [http://localhost:7575](http://localhost:7575), and Neo4j'data are stored into a folder name **neo4j** at root of your play! project.
But with configuration in your play! **application.conf** file, you can specify :

* the folder where data are stored by adding the key **neo4j.embedded.path** : 

    neo4j.embedded.path="data"

* the port of Neo4j server by adding the **neo4j.embedded.port** : 

    neo4j.embedded.port=7575

Also, you can configure the neo4j embedded database by creating **conf/neo4j.properties** file. To know what to put in this file, check the [neo4j documentation](http://docs.neo4j.org/chunked/milestone/server-configuration.html)

#### Distant Neo4j server

You can use this module with an existant neo4j database.
To do that, you only have to specify the url of your neo4j instance into **application.conf** like that : 

    neo4j.url="http://localhost:7474"

### Database evolutions

Like play for JDBC, this module take care of the state of your graph. This module reproduce the play! feature but for Neo4j with some cypger script. 

#### Evolution Script

All yours evolution script must be under the folder **conf/evolutions/neo4j**. And don't forget, cypher extension script is **.cql**

So you will have *1_up.cql*, *1_down.cql*, *2_up.cql*, *2_down.cql* ...

Have you see ? Yes this feature is not really clone of the play one. Here we don't use the same file to put together up & down script. We have two distinct files, simply named X_up.cql & X_down.cql (where X is the number of your evolution script).

#### Configure

This feature has three modes :

* **auto** : evolution are apply without asking the user, it's totaly transparent. This mode was made for testing purphose
* **enable** : like play feature, an error page with the script that must be apply, will be display to the user when an evolution is needed.
* **disable** : the feature is disabled. This is the default configuration

To configure it, you simply have to add this key into your application.conf :

    neo4j.evolution="auto"
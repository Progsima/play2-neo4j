play2-neo4j
===========
A dedicated Neo4J module for the Play Framework! 2.

Goals
-----
The very first intent of this plugin is to help developers bootstrapping a web application using Play 2, scala and Neo4J. So it's main focus won't (at first) be put on the communication layer, but on the application layer.

Thus, examples of important features will be:

* starting a fresh neo4j with the Play application comes for free
* for domain types, the persistence and query layers will be available for free (using helper and most probably macros)
* a dedicated user interface will be generated to manage your domain data and to browse it

Warning! don't be confusing, this module doesn't want to restrit the application to be end-to-end DDD. But it will reduce boilerplate when DDD is needed. In a broader sense, the Neo4J layers won't be hidden!

Getting Started
---------------
The simplest way to get started with this module is to:

- clone the repo
- build the module in *module* using SBT
- use the Play2 application in *samples* as a template

The sample will by default create a node when using the (http://localhost:9000/)[http://localhost:9000/] URL. Also, a 2.0 Neo4J instance will run on the 7575 port with its web admin interface available on [http://localhost:7575/browser](http://localhost:7575/browser)

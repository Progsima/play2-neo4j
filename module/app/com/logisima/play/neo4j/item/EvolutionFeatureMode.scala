package com.logisima.play.neo4j.item

/**
 * Enumeration that represent the mode of the evolution feature.
 * This can be :
 *  - auto : evolution are apply without asking the user (good for test)
 *  - enable : if your database needs a evolution, you will see an error page that ask you to apply a script
 *  - disable : evolution feature is turn off
 */
object EvolutionFeatureMode extends Enumeration {
  type EvolutionFeatureMode = Value
  val auto = Value("auto")
  val enable =  Value("enable")
  val disable = Value("disable")
}

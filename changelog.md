# Limit-Mob-Spawn

[![](http://cf.way2muchnoise.eu/versions/limit-mob-spawn.svg)][download page]
[![](http://cf.way2muchnoise.eu/full_limit-mob-spawn_downloads.svg)][download page]

[![Java CI with Gradle](https://github.com/Kotori316/LimitMobSpawn/actions/workflows/gradle.yml/badge.svg)][action details]

[download page]: https://www.curseforge.com/minecraft/mc-mods/limit-mob-spawn

[action details]: https://github.com/Kotori316/LimitMobSpawn/actions/workflows/gradle.yml

## v16.3

* Commands can now add rules of "Add" / "Or" / "Not".
  * All rules can be added by commands.
* Changed default setting of position limit
  * Before, if y-max is omitted, y-max=255
  * After, if y-max is omitted, y-max=256
  * No changes if y-max is presented.

## v16.2

* Added commands to add new rules.
  * Only simple rules.
* Added commands to remove rules.
* Fixed an issue where array of rules can't be parsed correctly.
* Made this mod required only in the server.
  * In the multi-players mode, client **does not need** to install this mod.
  * In the single-player mode, client **needs** to install this mod.
* Internal changes
  * Use JUnit to test commands
    * To test commands can be successfully parsed.
    * To test commands can suggest proper items.
  * Test in CI.

## v16.1

* Serialization was improved to use Dynamic, which can accept both Json and NBT.
* Serialization of And/Or condition was changed to use array, instead of map of "t1", "t2"...
  * The map type object can still be used.
* Fixed an issue where deserialization fails in some cases.
* Added "All" and "Spawn Reason" limitations.
* Reduced spams of log.
* Added capability to save limitations in the world.
* Added commands to query rules.

## v16.0

The first release of this mod.

* Setting is loaded from json in datapack.
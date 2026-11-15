# Wildfire's Female Gender Mod — Minecraft 1.12.2 (Ornithe/Fabric)

A port of [Wildfire's Female Gender Mod](https://github.com/WildfireRomeo/WildfireFemaleGenderMod)
3.1 (originally for Minecraft 1.20.2) to **Minecraft 1.12.2**, built on the
[Ornithe](https://ornithemc.net/) toolchain (Feather mappings, Fabric Loader and the
Ornithe Standard Libraries).

## Features

- Female, male and other genders, with per-player gender, breast size, separation, height,
  depth, rotation (cleavage), dual-physics (uniboob), breast physics, bounce intensity,
  breast momentum, "hide in armor" and armor physics override.
- Full wardrobe UI (gender selection, appearance settings, character settings) with player
  preview and the original layout/textures.
- Breast rendering on players (skin layer, jacket overlay) and chestplate rendering /
  enchantment glint on players and armor stands.
- Physics driven breast movement (walking, falling, sneaking, sleeping, boats, minecarts,
  horses, pigs, arm swings, breathing animation, armor resistance/tightness).
- Custom OSL networking to synchronise settings between client and server, including
  syncing on join and broadcasting changes to all other players.
- Armor stand support: chestplates equipped by a player copy that player's breast settings
  into the item's NBT, and armor stands render the corresponding breast armor.
- All original translations converted to the 1.12.2 `.lang` format, plus the original
  textures.

## Differences from the original mod

- The preset feature is not included. The 1.20.2 source ships an unfinished, development-only
  preset implementation (hardcoded "Test Preset", empty delete action, missing thumbnail
  texture); it was removed from this port rather than ported half-working.
- The female hurt sound feature is not included; hurt sounds are the vanilla ones.
- Netherite armor and armor trims do not exist in 1.12.2 and are therefore not supported.

## Building

Java 8 is required to run the game; the Gradle build itself requires a newer JDK because
Loom 1.18 needs Java 25+. The build is configured to launch the game with a Java 8
toolchain automatically (any locally installed JDK 8 is detected by Gradle).

```
# Gradle needs a recent JVM (Java 25+), while the game runs on Java 8
./gradlew build
```

The remapped mod jar is written to `build/libs/`.

Development runs:

```
./gradlew runClient
./gradlew runServer
```

## License

This mod is licensed under the GNU Lesser General Public License v3.0; see [LICENSE](./LICENSE).
The original mod is Copyright (C) 2023 WildfireRomeo.

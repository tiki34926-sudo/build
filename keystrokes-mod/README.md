# Keystrokes

A clean client-side keystrokes HUD for Minecraft **1.21.11** (Fabric).

- W / A / S / D keys, LMB and RMB with live CPS counters, and a space bar
- Smooth fade between idle and pressed
- Click sound when you press W, A, S or D (`KeystrokesClient.soundEnabled`)

## Requirements
- Minecraft 1.21.11
- [Fabric Loader](https://fabricmc.net/use/) 0.18.1+
- [Fabric API](https://modrinth.com/mod/fabric-api)

## Build
Needs JDK 21.

```
gradle wrapper      # first time only, if gradlew is missing
./gradlew build
```

The jar is written to `build/libs/`. Drop it in your `mods` folder.

## Customize
Edit `KeystrokesHud.java`: position (`X`, `Y`), key size (`S`), gap (`G`) and the colors at the top.

## License
MIT

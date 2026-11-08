# Living Civilization on Windows

This guide targets Windows 10 and Windows 11 (64-bit), Minecraft Java Edition 26.3, and the Fabric release configured by this project. Windows gameplay compatibility has not yet been verified on a Windows machine.

## Install and launch

1. Install Minecraft Java Edition using the official Minecraft Launcher and launch version 26.3 once.
2. Ensure the launcher uses a 64-bit Java 25 runtime. The launcher-managed runtime is suitable when it reports Java 25; if selecting a custom runtime, point it to Java 25.
3. Download and run the [official Fabric Installer](https://fabricmc.net/use/installer/). Select Minecraft 26.3 and Fabric Loader 0.20.1, then install the client profile.
4. Download Fabric API version `0.165.2+26.3` from the [Fabric API project page](https://modrinth.com/mod/fabric-api). Put its JAR in `%APPDATA%\.minecraft\mods`.
5. Put `living-civilization-0.1.0.jar` in the same `mods` folder. Keep both JARs intact; do not extract them.
6. In the Minecraft Launcher, select the Fabric 26.3 profile and launch the game.

For a custom game directory, put both mod JARs in that directory's `mods` folder instead.

## Verify the installation

The main menu should load without a mod error. To confirm the mod initialized, check `latest.log` for `Living Civilization initialized` and the mod ID `livingcivilizations`.

For Step 1.1 testing, create a world with cheats enabled (or use an operator account) and run `/livingciv spawn human`. Expect one human NPC to appear nearby on a safe, loaded position. The NPC currently has no autonomous jobs. Visually confirm the model and texture render, and check `latest.log` for errors. Windows in-game rendering and spawning have not yet been verified by the development team.

The default log path is `%APPDATA%\.minecraft\logs\latest.log`. For a custom game directory, use `<game directory>\logs\latest.log`. Crash reports are in the adjacent `crash-reports` folder.

## Build from source on Windows

Install a 64-bit Java 25 JDK and open PowerShell in the project directory. The checked-in Gradle wrapper downloads and runs the configured Gradle version:

```powershell
.\gradlew.bat clean build
.\gradlew.bat test
.\gradlew.bat verifyModJar
.\gradlew.bat runClient
```

The distributable JAR is generated in `build\libs\living-civilization-0.1.0.jar`.

## Report a compatibility problem

Include the Windows edition and build, Minecraft version, Java version, Fabric Loader version, Fabric API version, mod version, steps to reproduce, and the relevant `latest.log` or crash report. Remove account names, tokens, or other private information before sharing logs.
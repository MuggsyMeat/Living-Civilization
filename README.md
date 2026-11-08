# Living Civilization

Fabric mod for Minecraft Java Edition 26.3, using Fabric Loader, Fabric API, Loom, Gradle, and Java 25.

## Platform support

Windows 10 and Windows 11 (64-bit) are the primary player targets. Runtime code must remain platform-independent and use Java, Minecraft, and Fabric APIs rather than operating-system paths, shell commands, or native libraries. The CI workflow builds and runs tests on Ubuntu and a Windows runner.

CI coverage is not equivalent to testing Minecraft in-game on Windows 10 or Windows 11. Windows runtime compatibility has not yet been manually verified.

## Build and test

```sh
./gradlew clean build
./gradlew test
```

The distributable mod JAR is written to `build/libs/`. Start the Fabric development client with `./gradlew runClient`. The client log is at `run/logs/latest.log`.

## VS Code and diagnostics

The workspace enables automatic Gradle project import and uses the Gradle wrapper. Java 25 is available on the development PATH; the Java extension also runs its language server on its bundled Java 25 runtime. VS Code tasks provide clean build, tests, client launch, and client launch with diagnostics.

The diagnostics task sets `LIVING_CIVILIZATIONS_DEBUG=true`. This enables lightweight component-tagged messages through `LivingCivilization.debug`; use it for future entity registration, settlement startup, and NPC behavior. Errors can be logged with `LivingCivilization.error` and a throwable. Diagnostics are off by default. Logs are written to `run/logs/latest.log`.

The installed Test Runner for Java currently contributes an `org.objectweb.asm` bundle that conflicts with the Java language server’s bundle of the same version. The VS Code log records the duplicate-bundle initialization failure; Gradle import and builds still work. If Java files appear as non-project files, disable **Test Runner for Java** for this workspace from the Extensions view (gear menu, **Disable (Workspace)**), then run **Developer: Reload Window**. Continue to run tests with the Gradle task or `./gradlew test`; this does not uninstall or disable the extension in other workspaces.

On Windows, use the Gradle wrapper batch file:

```powershell
.\gradlew.bat clean build
.\gradlew.bat test
.\gradlew.bat runClient
$env:LIVING_CIVILIZATIONS_DEBUG = "true"
.\gradlew.bat runClient
```

## Project layout

- `src/main/java`: shared mod initialization and game logic
- `src/client/java`: client-only rendering and integration
- `src/main/resources`: mod metadata and shared assets
- `src/client/resources`: client-only assets

Civilization jobs and settlements are not implemented yet. Step 1.1 adds one command-spawned human NPC with an original placeholder texture; it has no autonomous work behavior.

## Human NPC test command

In a development world, an operator or a single-player world with cheats enabled can run `/livingciv spawn human`. The command searches nearby loaded positions with a solid floor and clear headroom; it reports failure without changing blocks if no safe position is available. The temporary, original placeholder texture is `src/main/resources/assets/livingcivilizations/textures/entity/human.png`.

The starter is based on FabricMC's CC0-licensed example mod template.

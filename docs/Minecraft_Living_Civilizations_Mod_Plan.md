# Living Civilizations — Minecraft Mod Design & Development Plan

**Working concept:** Autonomous tribes that gather resources, construct structures block by-block, alter terrain, grow, trade, migrate, and sometimes fight, making single-player worlds feel inhabited.

**Target version:** Minecraft Java 1.20.1 (provisional, based on earlier discussion). Mod loader to decide before implementation.

## 1. Agreed design vision

- Each tribe consists of one creature species, with specialized roles: builder, lumberjack, farmer, miner, hunter, warrior, scout, and trader (introduced in stages).
- Tribes appear naturally and operate independently of the player.
- Each settlement has food, construction materials, population, housing, and a shared stockpile/resource ledger.
- NPCs physically look at, break, and place blocks; mining uses tool-appropriate speeds, with diamond-tool-equivalent strength as an initial reference to tune.
- Structures are authored as NBT templates; workers place their blocks individually, rather than instantly spawning the entire structure.
- Settlements choose suitable sites and prepare terrain before building; they can clear forests, excavate, farm, construct roads and bridges, and expand.
- Architecture and available resources reflect the surrounding biome and landscape.
- Population grows when food and housing permit. Scarcity may trigger job reassignment, migration, or decline.
- Successful settlements can send settlers to establish new tribes. Daughter settlements may remain allied or become independent.
- Mature settlements may trade based on shortages and surpluses; relations can turn friendly, hostile, or neutral.
- Raids and wars can lead to occupied settlements and persistent ruins.
- Leadership is **abstract**, represented by civilization/settlement decision systems rather than dependence on one chief NPC.
- Player actions influence reputation, rewards, hostility, and potential loyalty; players might support a tribe into becoming a powerful ally.
- Limit settlement growth and environmental impact; protect player builds.
- Nearby activity is simulated visibly; distant development uses a lightweight model without keeping distant chunks loaded.

## 2. Architecture

**Civilization / faction manager:** faction identity, traits (aggression, cooperation, ambition), diplomacy, relationships, settlement membership, expansion decisions.

**Settlement manager:** settlement center and boundaries, stockpile, population, food consumption, job allocation, construction queue, local terrain surveys, building limits.

**NPC behavior:** job state machines, navigation, block interactions, gathering, hauling, depositing, constructing, combat (later).

**World services:** blueprint loader, build-site planner, block-change queue, protection checks, persistent saved data, chunk-load-aware simulation.

**Simulation modes:**
- **Near player:** real NPC movement and block interactions in loaded chunks.
- **Far away:** periodic abstract resource/population/project updates. Never silently alter unloaded terrain; queue changes until chunks are loaded and validated.

## 3. First playable prototype (MVP)

**Scope:** One species, one settlement, three jobs (lumberjack, builder, farmer), one hut NBT blueprint, wood and food resources. No diplomacy, war, migration, or distant terrain changes yet.

**Core gameplay loop:** spawn settlement → assign lumberjack → find permitted tree → break logs → return/deposit wood → builder checks stockpile → navigate to site → place blueprint blocks one at a time → farmer harvests/replants food → stockpile updates.

**Definition of done:**
1. A new settlement can spawn or be spawned by a test command.
2. NPCs move to reachable targets and visibly harvest logs.
3. Harvested logs increase settlement stockpile exactly once.
4. Builder consumes materials and completes a small hut from a blueprint without instant placement.
5. NPCs pause/resume tasks after saving and reloading the world.
6. NPCs do not modify protected player blocks or unloaded chunks.
7. A small debug overlay/command shows jobs, stockpile, and current building task.

## 4. Development phases

| Phase | Deliverable | Test gate |
|---|---|---|
| 0 — Setup | Choose loader; create mod workspace; launch dev Minecraft | Game starts with mod loaded |
| 1 — NPC | Custom creature entity, navigation, simple job AI | NPC reliably reaches test targets |
| 2 — Block work | Target selection, block-break animation/progress, harvesting, placement | One NPC mines and places designated blocks |
| 3 — Blueprints | Parse NBT template into ordered construction tasks | NPC completes a tiny test structure |
| 4 — Economy | Shared wood/food ledger; gathering, depositing, consuming | Build pauses when wood runs out, resumes when supplied |
| 5 — Settlement | Site selection, housing, job assignment, food needs | Autonomous settlement sustains itself |
| 6 — Terrain | Clearing, leveling, foundations, protected areas | Builds on uneven terrain without griefing |
| 7 — Growth | Population and additional buildings | Village expands under resource limits |
| 8 — Migration | Settler parties and daughter settlements | New independent settlement forms |
| 9 — Diplomacy | Traits, reputation, trade and trade routes | Two settlements exchange surplus/needed resources |
| 10 — Conflict | Raids, conquest, occupation, ruins | Persistent ownership changes survive reload |
| 11 — Optimization | Distant abstract simulation, throttling, diagnostics | Performance remains acceptable at agreed settlement counts |

## 5. Technical recommendations

- Use **Minecraft Java Edition** with one chosen mod loader (NeoForge or Forge for 1.20.1; confirm exact compatible versions). Avoid mixing loader APIs.
- Store world-level settlement/faction data persistently; save individual entity task state separately as needed.
- Author NBT blueprints using Minecraft structure blocks or compatible structure tooling.
- Convert templates to ordered block tasks: terrain clearance → foundations → floor → walls → roof → details.
- Before each block change, verify loaded chunk, permissions, obstruction, required resource, and pathing feasibility.
- Keep a per-tick budget for searches, path requests, and block edits. Prefer event-driven/periodic planning over scanning large regions every tick.
- Protect claimed player areas and offer configurable world-edit limits; distinguish player-placed blocks where feasible, and provide manual protection zones.
- Test edge cases: blocked paths, missing resources, broken tools, removed blueprint blocks, death mid-task, unload/reload, and settlement overlap.

## 6. First development session

1. Confirm **Java 1.20.1** and select **Forge or NeoForge**.
2. Install matching JDK, IDE, and mod development kit.
3. Generate mod skeleton; run client.
4. Register one custom entity with a basic model/placeholder appearance.
5. Add a debug command to spawn it.
6. Implement a simple state machine: `IDLE → FIND_TARGET → MOVE → WORK → RETURN`.
7. Demonstrate the entity breaking **one permitted log block** and depositing one wood unit.
8. Only then begin NBT construction.

## 7. Open design decisions

- Which mod loader and exact Minecraft version?
- Which creature species and initial settlement biome?
- Should the first economy use exact item/block counts or simplified resource units?
- How should players claim/protect land?
- How fast should population growth and migration occur?
- What settlement cap, radius, and simulation budget are acceptable?

**Recommended immediate next step:** choose the mod loader and build a single block-breaking NPC before attempting multi-tribe simulation.

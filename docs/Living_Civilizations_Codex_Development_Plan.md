# Living Civilizations — Codex Implementation Plan

**Revision:** 2026-11-07  
**Project:** Living Civilizations (`livingcivilizations`)  
**Development host:** Ubuntu workstation, Samsung 990 PRO; existing working Fabric/Gradle project currently called *Biome Foundations*.  
**Player target:** Windows 10/11 64-bit, Minecraft Java Edition with Fabric Loader.  
**Current reported working stack:** Minecraft 26.3, Java 25, Fabric. **Codex must inspect the actual installed Minecraft/Fabric/Java versions and compatibility; never silently change the working stack.**  
**Implementation agent:** Codex. **Creative direction/art/Blockbench/NBT architectural authorship:** user. Codex may create temporary test-only assets, but must provide asset specifications and never treat placeholders as final art.

> **Scope note:** The phase and step numbers below are the implementation roadmap, not guaranteed software release numbers. This document incorporates the original and updated Living Civilizations design documents; previous Forge/1.20.1 targets are obsolete. Do not confuse the Ubuntu development operating system with the Windows player runtime requirement.

## A. Operating contract for Codex (every step)

1. Before coding, read this document, the full design brief (`Living_Civilizations_Complete_Mod_Design_and_Codex_Brief_Updated.md`), the original plan (`Minecraft_Living_Civilizations_Mod_Plan.md`), project README, and the current source tree. Ask for the briefs if not present locally; do not invent unspecified design decisions.
2. **Perform only the next approved step.** First inspect existing code and tests. Present a short implementation plan, then implement, build, run tests, fix failures, document results, and **STOP for user approval**. Do not autonomously begin the following step.
3. Keep all gameplay state server-authoritative; make networked client rendering optional where practical. Use Fabric APIs that actually exist for the installed versions; do not invent APIs or versions.
4. Cross-platform runtime: standard Fabric JAR, no Linux-only native dependencies, system commands, or hard-coded `/home/...` paths. Use Java/Fabric abstractions. Assets and file paths must be case-correct for Windows and cross-platform loading.
5. Protect player-built areas; never edit unloaded chunks; no resource creation/duplication; no forced long-range chunk loading; budget pathfinding and block changes. Add tests for conservation and save/reload as soon as the relevant systems exist.
6. Distinguish **compiled**, **unit/integration tested**, **in-game tested on Ubuntu**, and **tested on Windows**. A Linux client launch does not prove Windows compatibility. If Windows testing cannot be performed, report it as pending.
7. Use small commits with descriptive messages. Check `git status` before modifying; preserve user work; do not push remotely without authorization. Exclude secrets, caches, compiled output, and personal files.
8. After each step, provide: changed files, design choices, build command/results, tests/results, actual in-game observations, JAR location, commit hash (if committed), manual Windows/Java test instructions, known risks, and next-step proposal. Do not claim a step passed unless evidence supports it.
9. No unapproved privileged system changes, disk changes, external publication, credential access, or replacing the user's creative assets. User approval is a **feature gate**, not permission for unrelated risky machine operations.
10. Preserve old save compatibility where feasible; use versioned persisted data and documented migrations as persistence expands. Any proposed breaking save changes require approval.

## B. Standard Codex command for each step

Paste this with the **step ID** you want:

```text
Read Living_Civilizations_Codex_Development_Plan.md and both Living Civilizations design briefs. Inspect the existing project and git status. Execute ONLY STEP <ID> from the plan. Make the smallest correct implementation, write appropriate tests, run Gradle build and tests, launch or run a GameTest when feasible, and repair failures. Preserve the current working Fabric/Minecraft/Java setup and Windows-compatible runtime. Provide the required evidence report, user test checklist, JAR path, and commit. STOP and wait for my OK before proceeding to the next step. If required design documents, assets, or a major decision are missing, ask rather than assume.
```

For step zero, substitute `STEP 0.1`. Each following step is a separate Codex assignment, not one enormous request.

## C. Step-by-step implementation roadmap

### PHASE 0 — Preserve the working project and development discipline

**0.1 — Audit and protect the baseline**  
**Build:** Inspect source, Gradle, Java/Fabric versions, mod identity, tests, `git status`, and JAR. Add existing design briefs and this plan to repo; establish baseline commit without overwriting uncommitted work. Record exact working commands.  
**Gate:** Clean build and tests reproduce existing successful report; baseline commit exists; docs accessible; no generated/secrets committed.

**0.2 — Set Living Civilizations identity**  
**Build:** Rename mod display name, mod ID where safe, Gradle metadata, package paths (only if worthwhile), artifact name, README, launcher run configuration. Avoid breaking mappings and Fabric entrypoints.  
**Gate:** Mod launches as Living Civilizations; built artifact has intended name and valid `fabric.mod.json`; no stale starter registration.

**0.3 — Dev ergonomics and diagnostics**  
**Build:** Investigate VS Code Java/Gradle project recognition; add reproducible launch/test tasks and helpful logs. Missing `libflite.so` narrator warning is non-blocking; change system packages only if safe and actually needed.  
**Gate:** Java sources recognized by IDE if achievable; otherwise document precise workaround; build and launch still pass.

**0.4 — Cross-platform CI/build discipline**  
**Build:** Add GitHub Actions or other approved CI workflow with Linux build/test and Windows Gradle build/test jobs using correct JDK. No GitHub publication unless user authorizes. Add smoke-test checklist for a genuine Windows Minecraft client.  
**Gate:** Windows CI build/test passes when remote CI is available, or local cross-platform checks run with pending CI clearly recorded. Windows runtime gameplay verification remains separate.

### PHASE 1 — A real, observable human worker

**1.1 — Entity registration and placeholder renderer**  
**Build:** Register one custom human entity, stable identifiers, attributes, Fabric client renderer, temporary vanilla-style model/texture, and `/livingciv spawn human` dev command.  
**Gate:** Entity spawns and renders; no client/server crash; registration tested.

**1.2 — Navigation and identity persistence**  
**Build:** Give NPC safe navigation toward specified reachable test coordinates. Persist identity, job assignment, settlement link placeholder, and task status.  
**Gate:** NPC reaches a test target; survives reload; blocked/unreachable targets resolve without freeze.

**1.3 — Modular job state machine**  
**Build:** Implement IDLE → FIND_TARGET → MOVE → WORK → COLLECT → RETURN → DEPOSIT → RECOVER; retry limits, timeout and task reset; state diagnostics.  
**Gate:** Automated state transition/recovery tests; no infinite high-frequency search loop.

**1.4 — Safe tree target selector**  
**Build:** Test-only work zone/permission controls; choose loaded, permitted, reachable log positions; don't touch player structures; avoid broad chunk scans.  
**Gate:** Correct target selection, protected area rejection, unloaded chunk rejection, and unreachable target handling.

**1.5 — Visible lumberjack work**  
**Build:** Path to a permitted log, face target, show breaking progress over time, harvest the real block server-side with appropriate drops.  
**Gate:** Player observes noninstant harvesting of only authorized logs; no ghost blocks or double drops.

**1.6 — Inventory and stockpile transactions**  
**Build:** Add bounded worker carrying inventory and a basic persistent settlement wood stockpile; real pickup/delivery and atomic deposit with duplication protection. Add `/livingciv stockpile` and job/debug commands.  
**Gate:** One harvested log produces exactly the expected delivered resource, even under interruption/reload; no negatives or duplication.

**1.7 — Lumberjack end-to-end acceptance**  
**Build:** Integrate entity, navigation, job, authorized trees, harvesting, carrying, return, deposit, recovery and debugging into a reliable game loop.  
**Gate:** In-game repeated cycle, save/reload recovery, offline/unloaded safety, and performance counters; user accepts before construction.

### PHASE 2 — Builders and NBT-driven construction

**2.1 — NBT structure loader and metadata**  
**Build:** Read version-appropriate NBT structure templates. Specify footprint, orientation, palette, build cost, entry point, phases and terrain assumptions. One minimal temporary test hut blueprint only.  
**Gate:** Test can parse blueprint, produce deterministic required block list/costs, reject corrupt templates.

**2.2 — Build task planner and reservations**  
**Build:** Transform blueprint into safe ordered work: clear permitted vegetation → foundation → floor → walls → roof → details. Reserve site and material budgets; prevent overlap with protected builds and other projects.  
**Gate:** Accurate ordered tasks, predictable rotations, valid site rejection, no resource spending before transaction commit.

**2.3 — Builder NPC placement loop**  
**Build:** Assign builder profession, navigate to safe reachable work positions, look at target and place one block at a time, update plan progress.  
**Gate:** Visually constructs a tiny NBT test structure, no instant bulk paste, no unloaded edits.

**2.4 — Resource consumption and resupply**  
**Build:** Builder withdraws finite stockpile materials or receives delivery; blocks cannot be placed free; handle shortages, refunds for aborted transactions, and job reassignment.  
**Gate:** Build stops when wood runs out; resumes on resupply; exact conservation and duplicate prevention.

**2.5 — Construction persistence, collision and recovery**  
**Build:** Persist blueprint identity, rotation, completed blocks, reserved materials and worker progress. Recover from death, save/reload, blocked sites and externally changed blocks.  
**Gate:** Restart mid-hut continues safely with no duplicated resources or extra blocks.

**2.6 — Site preparation and player protection**  
**Build:** Add bounded surface survey, foliage removal within permissions, basic leveling/foundations with clear configured environmental limits and claim/protection support.  
**Gate:** Simple uneven terrain building succeeds; protected areas untouched.

### PHASE 3 — First self-sustaining five-person human camp

**3.1 — Settlement manager, identity and ledger**  
**Build:** Persistent settlement center, boundary, membership, inventory, housing, work queue, building registry, production metrics, population-scaled finite starting supply.  
**Gate:** Settlement data survives restart; predictable resource accounting and unique IDs.

**3.2 — Spawn founding group and starter camp**  
**Build:** Admin/test command spawns five human NPCs, assigns settlements and initial finite resources; create stockpile and small starter camp tasks.  
**Gate:** Exactly one settlement with five NPCs and correct founding resources; no overlap or duplication.

**3.3 — Farmer and food production**  
**Build:** Safe crop finding, tilling/planting where authorized, growth/harvest, replant, delivery and food inventory.  
**Gate:** Crop loop physically operates; accounting conserves produce/seeds as designed; reload-safe.

**3.4 — Job allocator and food/housing needs**  
**Build:** Assign lumberjack/builder/farmer by current needs; food consumption, basic housing capacity; stable priorities, no thrashing.  
**Gate:** Five-person camp gathers wood, builds a hut and grows food, with failure states for scarcity.

**3.5 — Phase 3 long-run acceptance**  
**Build:** Run repeated simulation, restart and edge-case tests; audit no protected edits, duplicates, excessive pathfinding or stalled projects.  
**Gate:** Five-person settlement works autonomously for an agreed test duration and survives reload; user approves.

### PHASE 4 — Settlement growth and human building tiers

**4.1 — Building archetypes and asset contract**  
**Build:** Define NBT + metadata schemas and user art handoff for houses, farms, production, storage, defense, civic and infrastructure. Include rotations, footprint, cost, tier and biome palette.  
**Gate:** User-authored NBT building imports without code change; missing art reports exact requirement.

**4.2 — Population and housing growth**  
**Build:** Population increases only with food/housing; capped rates and resource costs; reassignment for shortages.  
**Gate:** No growth without capacity, appropriate scarcity behavior, persisted counts.

**4.3 — New professions and production chains**  
**Build:** Add miner, hunter, crafting/production where needed; real inventories and appropriate harvesting rules.  
**Gate:** Inputs/outputs test correctly and construction never consumes unavailable materials.

**4.4 — Settlement expansion, zoning and new projects**  
**Build:** Plan new structures within bounded territory, roads and farms, respecting terrain and nearby settlements.  
**Gate:** Expands autonomously with site validation and without griefing.

**4.5 — Roads, bridges and maintenance**  
**Build:** Segment-based infrastructure projects constructed by NPCs; material cost, navigation connectivity, repair and upgrade options.  
**Gate:** A worker-built road/bridge can be seen appearing incrementally; protected routes preserved.

**4.6 — Five-tier human architectural progression**  
**Build:** Data-driven human progression Tier 1 Anglo-Saxon huts → Tier 2 medieval rural village → Tier 3 timber-framed town → Tier 4 Norman fortified kingdom → Tier 5 advanced medieval kingdom. Tie unlocking to infrastructure/resources/population, not arbitrary timers.  
**Gate:** Each tier has verified prereqs, required assets and a representative structure; users supply final NBT designs.

**4.7 — Natural settled tribe spawning**  
**Build:** Biome/site-aware generation of founding camps; population-scaled supplies, rarity, spacing, protection limits; configurable world-gen switch.  
**Gate:** New worlds spawn settlements in valid places at acceptable frequency; existing worlds aren't indiscriminately edited.

### PHASE 5 — Species variety and architecture

**5.1 — Species data model and trait separation**  
**Build:** Generic species traits, biome preferences, architecture/tier configuration, entity/animation hooks and faction traits; reuse shared AI.  
**Gate:** Adding a species does not require duplicating settlement/work code.

**5.2 — Primitive humanoid entity and art pipeline**  
**Build:** Integrate user Blockbench model/texture, placeholder until available, species-safe animations and optional cosmetics; working name Varakai is provisional.  
**Gate:** NPC renders, navigates, works and saves correctly.

**5.3 — Primitive humanoid settlements (four tiers)**  
**Build:** Nomadic tents → roundhouses and timber villages → defended clan compounds → monumental timber confederations; hunting and optional distinct behavioral tendencies.  
**Gate:** Distinct architecture and progression; no accidental forced medieval-stone transition.

**5.4 — Underground reptilian entity and art pipeline**  
**Build:** Integrate custom reptilian appearance (modified head, simple tail) and model hooks; working name Drevari is provisional.  
**Gate:** Entity works with base AI and persistence.

**5.5 — Underground site search and excavation**  
**Build:** Cave/depth selection, tunnel and chamber plans, safe excavation, stability/obstruction rules, no unloaded-block edits; optimize pathfinding.  
**Gate:** Controlled underground test project carved by real workers without surface/player griefing.

**5.6 — Underground civilization progression (five tiers)**  
**Build:** Cave alcoves → carved tunnels/mushroom farms → chambered cities → engineered mining halls → monumental rock-cut empire, each requiring actual resources.  
**Gate:** Config-driven NBT construction and excavations work at representative tiers.

**5.7 — Multi-species simulation**  
**Build:** Allow several settlements of all species to coexist, cap populations/search budgets, validate territorial separation.  
**Gate:** Multi-settlement load/persistence/performance test passes.

### PHASE 6 — Migration, factions, trade and currency

**6.1 — Faction identities and settlement affiliations**  
**Build:** Faction membership, personality traits (aggression/cooperation/ambition), settlement affiliations, territory and independent decisions. Abstract leadership; no single chief as control point.  
**Gate:** Faction memberships/traits persist; simulated leader death does not stop faction.

**6.2 — Settler caravans and temporary camps**  
**Build:** Traveling group evaluates terrain/food/competition, carries finite cargo, pitches temporary camps, avoids unloaded chunk issues.  
**Gate:** Group arrives and establishes a real starter camp without resource duplication.

**6.3 — Daughter settlements and independence**  
**Build:** Mature settlement dispatches settlers with deducted supplies; new settlement forms allied or independent according to rules.  
**Gate:** Parent inventory decreases; daughter inventory appears once; relationships persist.

**6.4 — Merchant caravans and nomadic traders**  
**Build:** Two distinct caravan behaviors with inventories, guards when needed, wayfinding and unload/reload robustness.  
**Gate:** Cargo is deducted at departure and delivered once at arrival.

**6.5 — Crowns item currency and settlement treasuries**  
**Build:** Common physical currency called Crowns, inventory/treasury integration, item transfers, no minting without explicit economic rule.  
**Gate:** Crowns behave as real, transferable items; treasuries persist and reconcile.

**6.6 — Market supply/demand and player trading**  
**Build:** Prices based on shortages/surpluses within bounds; player-to-faction trades and vanilla village interaction where appropriate without overwriting vanilla villagers.  
**Gate:** Transaction tests, no item or currency duplication; game trading works.

**6.7 — Routes, trading posts and economic effects**  
**Build:** Repeated routes motivate roads/bridges/outposts; defend/suspend routes when unsafe; resource shortages alter settlement priorities.  
**Gate:** Two settlements exchange complementary resources and choose rational projects.

### PHASE 7 — Reputation and diplomacy

**7.1 — Seven-band player reputation (-100 to +100)**  
**Build:** Persist signed reputation per faction; seven named configurable bands, no passive decay.  
**Gate:** Exact boundary tests and save/reload; neutral/new-player defaults explicit.

**7.2 — Reputation-changing events and restrictions**  
**Build:** Positive/negative events, suspicion, restricted trade, discounts, gifts and assistance based on band.  
**Gate:** Reputation changes only due to defined events and produces consistent responses.

**7.3 — Relationship repair, quests and reparations**  
**Build:** Meaningful quests, aid, reparations and rare faction-dependent sabotage with cross-faction consequences.  
**Gate:** Quest state and rewards persist; no passive reputation restoration.

**7.4 — Interfaction diplomacy, alliances and mercenaries**  
**Build:** Faction-to-faction relations, alliances, rivalries, negotiated aid and limited mercenary support.  
**Gate:** Diplomatic decisions reflect traits/history; relationships survive reload.

### PHASE 8 — Combat, warfare, conquest and ruins

**8.1 — Warriors, guards and defensive AI**  
**Build:** Faction-aware threat detection, combat behaviors, retreat, target limits, friendly-fire safeguards and performance budgets.  
**Gate:** Controlled combat scenarios pass; nearby peaceful factions not attacked arbitrarily.

**8.2 — Raids and player settlement threats**  
**Build:** Raiding parties, escalating hostility, protected-area policy, optional high-hostility arson/mercenary attacks with explicit configuration and safety boundaries.  
**Gate:** Raids trigger only under defined conditions; can be disabled/configured.

**8.3 — Territorial conflict and sieges**  
**Build:** War declarations/conflicts, staged attacks, defenders, fortification interaction, resource/supply constraints.  
**Gate:** Two settlements can fight over a defined territory in controlled tests.

**8.4 — Conquest, occupation, ownership transfer**  
**Build:** Transfer control of a defeated settlement, register damage/occupants, enforce valid ownership.  
**Gate:** Conquest survives restart; no duplicate settlements/factions.

**8.5 — Persistent ruins, reconstruction and history**  
**Build:** Abandoned/damaged buildings, repair opportunities, event history and persistent ownership narrative.  
**Gate:** Ruins remain discoverable; reconstruction consumes resources and updates saved history.

### PHASE 9 — Scale, balance and cross-platform hardening

**9.1 — Distant abstract simulation**  
**Build:** Advance faraway population/resources/projects with low-frequency bounded math; never force-load distant chunks or mutate unloaded terrain. Reconcile pending physical actions on load.  
**Gate:** Distant progression consistent with near simulation, no duplication or surprise edits.

**9.2 — Budgets, telemetry and configurable limits**  
**Build:** Per-tick pathfinding/block-edit budgets, NPC/settlement caps, debug performance counters, queue backpressure and diagnostic commands.  
**Gate:** Stress-test shows bounded server impact for agreed test sizes.

**9.3 — Long-duration stability and migration tests**  
**Build:** Simulate many settlements/saves/restarts; verify no persistent leaks, duplication, overflow, missing factions or corrupt saves. Document data migration for old saves.  
**Gate:** Overnight/long-run test evidence and reproducible benchmarks.

**9.4 — Actual Windows player validation**  
**Build:** Test the standard Fabric JAR on supported Windows 10/11 installations with a matching Minecraft/Fabric/Java stack; validate asset paths, config/write behavior, rendering, save/reload and networked/client-server use if applicable.  
**Gate:** Documented Windows in-game pass. CI compilation alone is insufficient.

### PHASE 10 — Release readiness

**10.1 — Balance and content completeness**  
**Build:** Tune population, food, trade, reputation, construction speed, biome placement, progression prerequisites, war frequency and accessibility. Replace temporary art with supplied content.  
**Gate:** Representative world-play tests across species and tiers.

**10.2 — Documentation, configuration and packaging**  
**Build:** Windows installation guide, compatible versions, settings, debug/admin commands, save compatibility notes, credits/licensing, changelog and troubleshooting guide.  
**Gate:** Fresh Windows player follows guide and loads mod successfully.

**10.3 — Release candidates and 1.0**  
**Build:** Regression test matrix, cross-platform game checks, crash review, licensing/asset review, release JAR reproducibility; release/publish only with user authorization.  
**Gate:** User approves candidate based on full test evidence and intended scope.

## D. Test requirements that repeat throughout

- **Compile:** `./gradlew clean build` (or equivalent Gradle wrapper command) succeeds for current environment.
- **Automated tests:** Unit tests for logic; Fabric GameTests or integration tests where supported; no invented green test claims.
- **In-game:** Provide exact commands, expected behavior and observations; record whether a real human verified results.
- **Persistence:** On every data-model step, test save, quit, reload, and task interruption.
- **Security/game safety:** Protected areas, loaded chunks, permissions, no duplication, no surprise destruction.
- **Windows:** Windows build CI at earliest practical time; real Windows client gameplay smoke test for first playable milestone, each major phase and release.
- **Performance:** Measure tick cost, pathfinding calls, queue size and entity count for expanding simulation steps.

## E. Asset handoff conventions

- Temporary placeholder visuals are allowed; user-owned models and textures replace them via clear resource paths and schemas.
- Default art direction: Minecraft-compatible low-poly/blocky, Steve-sized human base when useful, Blockbench, initial 64×64 textures where appropriate, species-specific costumes/features, faction colors and banners.
- Structure blueprints: Minecraft NBT files with metadata for footprint, orientation, building category, tier, required block counts, site constraints and phases. Codex owns the importer, placement simulation and verification; user owns final architectural design.
- Architecture tiers: Humans 5 (Anglo-Saxon → medieval English → town → Norman fortified → advanced medieval); primitive humanoids 4 (nomadic → roundhouse → clan → confederation); underground reptilians 5 (cave → carved settlement → city → deep-earth engineering → monumental empire).
- Names **Varakai** and **Drevari** are working proposals until user approves them.

## F. Decision log (not yet finalized)

1. Exact starting supplies and building costs; placeholder documented, don't freeze balancing early.
2. Player protection/claim mechanism and acceptable destructive edits.
3. World spawn rates, settlement caps, boundaries and population speed.
4. Initial biomes and species-specific appearance/names.
5. Faction-specific architecture variations beyond each species' base historical style.
6. Exact reputation band names/boundaries, Crown minting/economic rules and market pricing.
7. Near/distant transition distance, abstract simulation interval and update budget.
8. Save migration policy for older pre-release versions.

**Immediate next assignment:** `STEP 0.1`, unless the existing repo already has a verified baseline and the documents in place; in that case inspect and proceed with user approval to `STEP 0.2` or the first incomplete task. No phase skipping based solely on a successful JAR build.

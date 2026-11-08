# Living Civilizations — Complete Mod Design & Codex Development Brief

**Status:** Design specification; not yet implemented.  
**Primary goal:** Make single-player Minecraft worlds feel inhabited by autonomous tribes that build, survive, trade, migrate, and compete.  
**Provisional target:** Minecraft Java 1.20.1, Forge (confirm before setup).  
**Development approach:** AI coding agent (Codex) writes and tests code; user directs design and validates gameplay.

## 1. Core vision
Civilizations spawn naturally, live independently of players, physically gather resources and build from NBT blueprints, reshape terrain, and develop over time. Players can discover thriving settlements, caravans, wars, abandoned ruins, and changing alliances. The simulation should create emergent stories without requiring player participation.

## 2. Tribe spawning and starter camps
- Two founding modes: **settled tribes**, spawning at a validated building site, and **settler caravans**, traveling to find one.
- Tribe sizes vary; **starting supplies scale with initial population**.
- Example *placeholder* per member: 16 logs, 8 cobblestone, 4 food; five members = 80/40/20, ten = 160/80/40. Tune to actual blueprint costs and early survival.
- Biome and species modify starting materials and camp architecture.
- Members may visibly carry supplies, establish a stockpile, and build a starter camp; materials are finite.
- Camp size scales with tribe size. Tools/equipment are separate from shared building and food resources.
- Settler caravans evaluate terrain, food, resources, threats, and separation from other settlements; they can pitch temporary camps.

## 3. Species and roles
Initial goal: **2–3 species**, introduced sequentially.
1. **Humans:** baseline for farms, cottages, villages, castles.
2. **Primitive humanoids:** huts, palisades, hunting and potential raids.
3. **Underground species:** mining, tunnels, underground structures.
Species share the initial core AI and differ through configuration, blueprints, biome preferences, architectural palettes, and personality. Special abilities can come later.

Potential jobs: lumberjack, farmer, builder, miner, hunter, scout, warrior, trader, caravan guard, and road engineer. **First prototype:** lumberjack, farmer, builder.

## 4. Economy and survival
- Real resource requirements for construction and survival, not merely cosmetic animations.
- Shared settlement stockpile/ledger for building blocks and food.
- Workers gather, deliver, deposit, and consume resources; builders cannot place blocks without stockpile materials.
- Food supports population; shortages cause job reassignment, halted growth, migration, or decline.
- Settlements choose building palettes influenced by locally available materials.
- Resource surpluses and deficits eventually drive trade and specialization.

## 5. Construction and terrain
- Buildings authored as **NBT structure blueprints**; NPCs place blocks one by one in a sensible sequence.
- NPCs look toward targets and visibly break blocks, with block/tool-appropriate progress (diamond-tool-equivalent mining capability discussed as a balancing starting point).
- Plan sites before work: survey, clear vegetation, excavate, level, add foundations, then construct.
- Villages may clear forests, excavate mines, build farms, bridges, roads, walls, and later castles.
- Road construction is **physical NPC work**, using materials and divided into manageable segments. Roads may evolve from dirt to gravel to stone and require maintenance.
- Protect player builds with configurable protection/claims; enforce territorial rules and limits on destructive edits.

## 6. Population, expansion, and politics
- Growth depends on food, housing, and resources.
- Mature settlements may dispatch settler caravans carrying a share of resources to found **daughter settlements**.
- Daughter settlements may remain allied or become independent tribes.
- Each settlement tracks territory, stockpile, population, buildings, and tasks; civilizations/factions track relations and wider goals.
- **Leadership is abstract:** settlement/civilization AI makes decisions; any visible chief is a representative, not a single point of failure.
- Personality traits (aggression, cooperation, ambition, etc.) can change in response to events.
- Tribes may raid, fight, occupy castles, change territory ownership, and leave lasting ruins.
- Players may trade, aid, attack, earn rewards, alter reputation, and potentially gain loyal allies/subjects.

## 7. Caravans, trade, and infrastructure
Three caravan categories:
- **Settler caravans:** seek permanent new homes.
- **Merchant caravans:** transport real inventory between settlements.
- **Permanently nomadic trading tribes:** roam, trade, and use temporary camps without settling.
Trade routes arise from complementary shortages/surpluses. Cargo is deducted at departure and delivered on arrival, with protection against duplication or loss during unload/reload. Caravans may have guards, encounter raids, and prompt defensive infrastructure. Repeated routes can motivate roads, bridges, outposts, and trading posts.

## 8. Architecture and advancement
- Each species has distinctive architecture and progression; environment changes materials and variants.
- Broad stages: primitive → developing → established → advanced, with species-specific blueprints.
- **Technology sharing is deferred.** Later, simple capability flags (e.g., `stone_masonry`) can unlock techniques through trade without changing a species' identity.

## 9. Simulation architecture
- **Faction layer:** diplomacy, traits, territory, expansion, relations.
- **Settlement layer:** stockpile, population, job allocation, construction queue, site planning.
- **NPC layer:** navigation, harvesting, hauling, placing, fighting.
- **Near player:** visible physical actions in loaded chunks.
- **Far away:** bounded abstract updates; do not force-load broad areas or edit unloaded terrain. Queue and validate physical changes on chunk load.
- Persist data across save/reload, enforce per-tick budgets, cap settlement counts and construction rates, and prevent resource duplication.

## 10. Development roadmap
**Phase 1 — First autonomous human settlement**
1. Set up a working Minecraft 1.20.1 mod project.
2. Create one custom NPC and test navigation.
3. Implement one safe block-breaking task, then block placement.
4. Load an NBT blueprint and convert it to an ordered build queue.
5. Add shared wood/food resources and basic delivery.
6. Add lumberjack, builder, farmer roles.
7. Spawn 5–10 humans with population-scaled supplies and construct a starter camp.
8. Save/reload test and verify basic performance.

**Phase 2 — Multiple settlements and trade**
1. Add primitive humanoid species and species-specific architecture.
2. Add population growth, new buildings, and daughter settlements.
3. Add settled spawning, wandering settler caravans, temporary camps.
4. Add merchant caravans and permanently nomadic traders.
5. Add simple supply/demand trade and initial relationships.

**Phase 3 — Dynamic civilizations**
1. Add underground species and advanced mining/terrain planning.
2. Add roads, bridges, engineers, and maintenance.
3. Add territory, warfare, raids, conquest, and occupation.
4. Add persistent ruins and more sophisticated world history.
5. Optimize and stress-test larger world simulations.

**Later:** cross-species technology sharing, deeper diplomacy, specialized abilities.

## 11. First milestone acceptance test
Spawn one human tribe with five NPCs. They receive a finite starter stockpile; a lumberjack gathers wood from a permitted tree and deposits it; a builder consumes wood and places a small hut blueprint block by block; a farmer produces food; the camp survives a save/reload without duplicated resources, missing work, or edits to protected player structures.

## 12. Instructions for Codex
Treat this document as the design source of truth. Implement **only the first milestone** before expanding scope. First inspect the environment and confirm Minecraft version, mod loader, Java version, Gradle setup, and build commands. Create small, reviewable changes. Run the build and automated tests, report actual results and failures, and provide in-game verification instructions. Never claim that compilation alone proves NPC behavior. Do not silently add features, alter the design, or skip tests. Keep species definitions and NBT blueprints modular.

### Suggested initial Codex prompt
> Build the first milestone described in `Living_Civilizations_Complete_Mod_Design_and_Codex_Brief.md`. Start by setting up a Minecraft Java 1.20.1 Forge project (ask me to confirm the version/loader if not yet finalized). Implement one custom NPC that can navigate to and visibly break a permitted log block, collect/deposit its resource, and later place blocks from a small NBT blueprint. Work incrementally, run build/tests, and report evidence after each step. Do not implement diplomacy, caravans, or warfare yet.

## 13. Decisions still open
- Confirm Minecraft version and mod loader.
- Choose original species designs, models, and initial biomes.
- Tune starting resource quantities and hut costs.
- Define player-protected areas, settlement caps, and growth speeds.
- Define exact near/far simulation transition and persistence behavior.

**Next action when at the Windows PC:** install/configure Codex and the Java modding toolchain, then begin the single-NPC milestone.


## Historical Architecture Reference Guide — November 7, 2026

### Design principle
Each species has its own recognizable architecture and technology progression, inspired by real historical building traditions rather than copied exactly. All structures are built in Minecraft, exported as NBT templates, and constructed by NPC workers using gathered materials. Each faction may express its identity through banner colors, emblems, and modest palette variations. Structure libraries must support foundations, terrain adaptation, construction phases, and upgrades.

### Humans — Medieval European inspiration (five tiers)
- **Tier 1, early settlement:** Anglo-Saxon and early medieval timber dwellings. Small rectangular oak-log and plank huts, thatch-like stair roofs, dirt paths, fenced crop plots, simple wooden palisades. Palette: oak logs/planks, spruce stairs, cobblestone, dirt paths, fences.
- **Tier 2, established village:** Medieval English rural villages. Larger timber houses, barns, granaries, workshops, quarry, blacksmith, reinforced palisades and gates. Palette: stripped oak, spruce, cobblestone, stone bricks.
- **Tier 3, medieval town:** York-style timber-framed houses, narrow streets, projecting upper floors, markets, barracks, watchtowers, masonry walls. Palette: oak beams, white terracotta or calcite infill, stone bricks, spruce roofs.
- **Tier 4, fortified kingdom:** Norman stone castles and curtain walls. Gatehouses, battlements, towers, keeps, military compounds. Palette: stone bricks, cobblestone, andesite, iron bars.
- **Tier 5, advanced kingdom:** Elaborate later-medieval civic buildings, large castles, guild halls, grand markets, decorated fortifications. Palette: varied masonry, deepslate accents, copper roofs where appropriate.
- **Reference:** The Shambles and other medieval timber-framed streets in York, England; Norman castles in England. Use historic examples as visual inspiration, not claims of exact reconstruction.

### Primitive humanoids — Prehistoric and tribal inspiration (four tiers)
- **Tier 1, nomadic tribe:** Lightweight temporary camps with hide-like wool tents, fire pits, log supports, basic storage.
- **Tier 2, tribal village:** Neolithic and Iron Age-inspired roundhouses, rough timber huts, thatch-like roofs, hunting lodges, food-drying racks. Palette: logs, fences, wool, hay bales, coarse dirt, cobblestone.
- **Tier 3, established clan:** Larger communal longhouses, timber palisades, raised guard platforms, woodworking areas, chieftain hall.
- **Tier 4, tribal confederation:** Monumental timber halls, layered wooden defenses, fortified clan compounds and ceremonial spaces. Keep a rugged timber-and-hide identity; do not automatically turn these into European stone castles.
- **References:** Reconstructed Iron Age roundhouses in Britain and Ireland; Neolithic village reconstructions and timber palisade sites. These are inspirations drawn from multiple periods, not a single historically uniform culture.

### Underground reptilians — Rock-cut and subterranean inspiration (five tiers)
- **Tier 1, cave dwellers:** Simple natural cave alcoves and sheltered chambers, moss, stone, basic lighting.
- **Tier 2, underground settlement:** Carved rooms, narrow tunnels, mushroom farms, primitive mines, storage niches. Palette: tuff, stone, cobblestone, moss.
- **Tier 3, subterranean city:** Interconnected chambers, markets, copperwork, water channels, reinforced tunnels and defensive choke points. Palette: deepslate, tuff bricks, copper.
- **Tier 4, deep-earth civilization:** Large engineered mining halls, basalt columns, blackstone reinforcement, controlled collapsible tunnel defenses.
- **Tier 5, subterranean empire:** Monumental rock-cut facades, ceremonial chambers, vaulted or pillared spaces, extensive subterranean waterways and grand entrances.
- **References:** Derinkuyu underground city in Cappadocia, Türkiye, for carved living spaces and passage networks; Petra, Jordan, for monumental rock-cut facades. Petra is a surface rock-cut site, used as an artistic reference rather than an underground city.

### NBT structure-production workflow
1. For each species and tier, define a block palette and a small set of building archetypes: housing, production, defense, storage, civic/special, and infrastructure.
2. Build the first variants in a creative test world with consistent origin points and dimensions; export named NBT templates.
3. Record per-template footprint, entrance direction, terrain constraints, required materials, construction order, and allowed rotations.
4. Separate optional decorative blocks from required structural blocks to support construction stages.
5. Make higher-tier upgrades replace or expand existing buildings where feasible; require real production infrastructure, resources, and population before unlocking technology.
6. Begin with one home, one production building, and one defensive structure for each species. Add further faction variations only after base templates and worker construction pass testing.

### Open design decision
Whether different factions of the same species should share a consistent historical architectural style or branch into distinct historical inspirations (e.g., different human factions influenced by medieval England, Japan, or Byzantium). **Not yet decided.**

### Supporting entity-art direction
Vanilla Minecraft visual style. Use one Steve-proportioned base model per species where practical; humans closely match Steve, primitive humanoids use slight blocky proportion changes, and reptilians use a modified head and simple tail. Use Blockbench, initial 64×64 pixel textures, randomized hair and clothing, and faction colors/emblems. Reuse models and simple animations; introduce GeckoLib only if necessary. NPCs remain interchangeable in behavior and may switch jobs based on faction needs.

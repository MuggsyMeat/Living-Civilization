# Asset manifest

Narrow record for the one texture this branch changes. No other runtime art is in the repository. This file does not approve a final rig, model, or appearance.

Robert Clapp owns final creature appearance. The texture below is a marked placeholder. Nothing in this manifest says he approved how it looks.

## `human.png`

| Field | Value |
|---|---|
| Status | PLACEHOLDER. Visible marker added 2026-11-08. Not final art. |
| Runtime path | `src/main/resources/assets/livingcivilizations/textures/entity/human.png` |
| Runtime texture ID | `livingcivilizations:textures/entity/human.png` |
| ID construction | `HumanEntityRenderer` sets `LivingCivilization.id("textures/entity/human.png")`. `LivingCivilization.MOD_ID` is `livingcivilizations`. |
| Packaged entry | `assets/livingcivilizations/textures/entity/human.png` |
| Dimensions | 64×64 |
| Format | 8-bit RGBA PNG, color type 6, non-interlaced, filterable scanlines. Chunks written: IHDR, IDAT, IEND. |
| Alpha | Binary only: 0 or 255. The marker edit did not change any alpha value. |
| Sampled UV used for the marker | Body cube from `HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F)`, texture size 64×64 in `HumanEntityModel.createLayer`. Decompiled 26.3 `HumanoidModel.createMesh` (unofficial Mojang-named source, see the handoff) gives the body `texOffs(16, 16)` and `addBox(-4, 0, -2, 8, 12, 4)` with no mirror. `ModelPart.Cube` maps `Direction.NORTH` to pixel rectangle x=20..27, y=20..31. |
| Marker | 4×4 checker, origin (24, 24), inside that front rectangle, on pixels that were opaque teal `(42, 105, 116, 255)`. Even `x+y` is magenta `(255, 0, 255, 255)`; odd `x+y` is black `(0, 0, 0, 255)`. 16 pixels. |
| Model layer | `livingcivilizations:human#main` (`HumanEntityModel.LAYER`). Not changed. |
| Entity ID | `livingcivilizations:human`. Not changed. |

### Provenance that is actually known

The imported file is the blob at this path in commit `81275e185f0277282053c5a2c6d82de86bae02ab` (`Import Living Civilization baseline from Drive snapshot (2026-11-08)`). That commit message says original history, including `f8db1b9`, was not available, and that the snapshot contains steps 0.1–0.4 plus unaccepted step 1.1 work. The README in that commit calls this path the temporary original placeholder texture.

The imported PNG has no text chunks and no embedded author, tool, or license. The repository has no `.bbmodel`, no separate source PNG, and no asset-license file for this texture.

| Provenance field | Value |
|---|---|
| Original author | Unknown. Not established. |
| Original tool | Unknown. Not established. |
| Original asset license | Unknown. Not established. |
| Imported file SHA-256 | `963137f19e8e604689fae86a0f3c66a3ab4de50651b04dd7fb07f72744c5b9ad` |
| Imported decoded RGBA SHA-256 | `f7f0b6b916b158d546849a38c09eb08aa2613567c513fe5e163ef1620b5c8f88` (64×64×4, row-major, no filter bytes) |

The git author of the import commit is Robert Clapp. That records who imported the snapshot. It does not record who painted the pixels.

`LICENSE` and `fabric.mod.json` say CC0-1.0. The README says the starter is based on FabricMC's CC0 example mod template. Those statements cover the repository/template license. They are not evidence that every imported image was created by the template authors, or that CC0 is the license of this texture. The texture's author and asset license stay unknown.

### This modification

| Field | Value |
|---|---|
| Date | 2026-11-08 |
| Tool | `art/validate_human_placeholder.py` (Python standard library only) |
| Python | 3.12.3 (main, Aug 31 2026, 10:18:26) [GCC 13.3.0] |
| zlib | 1.3 |
| What changed | RGB of the 16 marker pixels only. Dimensions, path, UV layout, and alpha plane are unchanged. |
| Output file SHA-256 | `703f30d2e5b048dae204b8c129a88fbe4369d3aed4fdabeea8a4be3963d11cc4` |
| Output decoded RGBA SHA-256 | `dc8c1788cbb5b8655f52a066c5828e0c7982ad54379d94e29cc59363e9f70d4a` |
| Output alpha SHA-256 | `6b1968870d415a30d46ae6128dbdc2076674f356b615826aafc336fbe71cabcf` (same as the import) |

Check the committed file with `python3 art/validate_human_placeholder.py`. Exit 0 means the file is a 64×64 RGBA PNG, the alpha plane matches the import, the only decoded differences are those 16 coordinates, and the file SHA-256 matches the output hash above.

### How to replace it

1. Keep this path, the texture ID, 64×64, 8-bit RGBA, and binary alpha unless Core changes the renderer on purpose.
2. The mesh this client actually builds is `HumanoidModel.createMesh`, not a player-skin layout. Left arm and left leg in that mesh mirror `texOffs(40, 16)` and `texOffs(0, 16)`. Opaque pixels at y≥48 are in the file and were not part of this edit; this mesh does not sample them. A replacement drawn only in the 64×64 player left-limb slots will not show on those limbs until the model changes.
3. Swap the PNG. Update this manifest: author, tool, date, license, both file hashes, both decoded hashes, and status. Set status to ROBERT-FINAL only after Robert approves that appearance. Until then, keep a status of placeholder or proposal.
4. Keep the marked file available for rollback until the replacement is integrated.
5. A new hierarchy, pivot, overlay part, texture size, or UV island is not a drop-in. Core has to change the client model. This manifest does not authorize that edit.
6. No in-game render was run for the marker. After a real client launch, look at the front of the shirt. That check is still open.

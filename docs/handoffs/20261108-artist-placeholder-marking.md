# Handoff — Artist — Human placeholder mark — 2026-11-08

## Task assignment

Design Lead authorized a Phase 1 Step 1.1 fix: make the existing human texture visibly and unmistakably a placeholder, with a minimal pixel change, and record the asset.

Requested branch name: `art/1.1-placeholder`. This environment only accepts branches named `cursor/<description>-2265`, so the work is on `cursor/art-1.1-placeholder-2265`. That branch is not `main`. Nothing was pushed to `main`, merged, or published, and `LICENSE` was not edited.

This is not an approval of final appearance, a frozen rig, or a new model. Robert Clapp still has to okay a merge. Artist work here is a marked placeholder until he approves the art.

Task ID: none is established in the repository. The assignment did not issue one.

Requirement IDs: none are established in the repository. A search of the tracked tree found no `LC-SPC-001`, `LC-SPC-009`, `LC-ENG-011`, `LC-ENG-005`, or `LC-ENG-010`, and no `REQUIREMENTS.md`. The assignment said the relevant IDs were likely `LC-SPC-001`, `LC-SPC-009`, and `LC-ENG-011`. Planning notes supplied with the assignment also name `LC-ENG-005` and `LC-ENG-010`. Those notes are not repository requirements, and they are not treated here as an approved rig. No new requirement IDs were invented.

## What changed

The runtime texture already used by the client was recolored in 16 pixels. The face, hair, shirt placket, shirt buttons, limbs, dimensions, alpha, path, and UV layout were left as they were.

Marker: 4×4 magenta/black checker at x=24..27, y=24..27. That rectangle sits inside the body cube's north (front) face. Those pixels were a flat teal `(42, 105, 116, 255)` before the edit. Even `x+y` is `(255, 0, 255, 255)`. Odd `x+y` is `(0, 0, 0, 255)`. Alpha stayed 255.

Why that spot: `HumanEntityModel.createLayer` builds `HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F)` at 64×64. A decompiled copy of Minecraft 26.3 `HumanoidModel.createMesh` and `ModelPart.Cube`, Mojang names, was read on 2026-11-08 from the unofficial pages `https://mc-packet-reference.netlify.app/26.x/source/net/minecraft/client/model/HumanoidModel/` and `https://mc-packet-reference.netlify.app/26.x/source/net/minecraft/client/model/geom/ModelPart/`. That site says it is not affiliated with Mojang. This workspace has no Loom cache and no JDK 25, so those class bytes were not produced by compiling this project.

That decompilation shows:

- `head` `texOffs(0, 0)`, 8×8×8
- `hat` child of `head`, `texOffs(32, 0)`, 8×8×8, deformation +0.5
- `body` `texOffs(16, 16)`, 8×12×4, not mirrored
- `right_arm` `texOffs(40, 16)`; `left_arm` the same offset with `mirror()`
- `right_leg` `texOffs(0, 16)`; `left_leg` the same offset with `mirror()`

`ModelPart.Cube` maps `Direction.NORTH` to `[xTexOffs+depth, yTexOffs+depth, xTexOffs+depth+width, yTexOffs+depth+height)`. For the body that is x=20..27, y=20..31. The checker is inside it. The shirt pixels already drawn there match that rectangle, which agrees with the decompiled UV.

The face was not used. The hat's front face is opaque on y=8..9 and transparent on y=10..15, so the eyes and mouth show through. Painting the face would redesign it. Painting the transparent hat pixels would change alpha. The lower 64×64 limb slots (y≥48) are opaque in the file, but this mesh mirrors the right arm and right leg instead of sampling those slots, so a mark there would not show. The body front is sampled, opaque, and visible from the front, and the 4×4 that was edited contained no unique shirt detail.

No renderer, entity, command, gameplay, Gradle, dependency, or license file was edited.

## Files

| Path | Role |
|---|---|
| `src/main/resources/assets/livingcivilizations/textures/entity/human.png` | Runtime texture. 16 RGB values changed. |
| `art/ASSET_MANIFEST.md` | Manifest for this file only. Created. The repository did not have one. |
| `art/validate_human_placeholder.py` | Stdlib PNG check and re-apply tool. |
| `art/preview/human-before-nearest-x8.png` | Nearest-neighbor ×8 of the imported pixels. |
| `art/preview/human-after-nearest-x8.png` | Nearest-neighbor ×8 of the marked pixels. |
| `art/preview/human-before-after-nearest-x8.png` | Those two sheets side by side. |
| `art/preview/human-body-before-after-nearest-x16.png` | Body-island crop, nearest-neighbor ×16, before and after. |
| `docs/handoffs/20261108-artist-placeholder-marking.md` | This handoff. |

The four files under `docs/` that were already in the baseline were not edited. See the source assessment below.

Introducing commit: `d1f92bd5fbd4a52b415c463786e1fff6e0dc739a` on `cursor/art-1.1-placeholder-2265` (`Mark the human texture as a visible placeholder.`). This sentence was added in the following commit on the same branch. `git rev-parse HEAD` is the branch tip after that update.

## Dependencies and interfaces

No dependency was added. No Java interface changed. The texture ID, entity ID, model layer, and resource path are the same.

`HumanEntityRenderer` still loads `livingcivilizations:textures/entity/human.png`. A rebuilt client JAR is what a game would load. This environment did not build one.

## Checks and results

Executed on 2026-11-08 with Python 3.12.3 and zlib 1.3. Pillow is not installed and was not used. Command:

```sh
python3 art/validate_human_placeholder.py
```

Exit code: 0. Output:

```text
path: /workspace/src/main/resources/assets/livingcivilizations/textures/entity/human.png
dimensions: 64x64 RGBA
chunks: IHDR,IDAT,IEND
file_sha256: 703f30d2e5b048dae204b8c129a88fbe4369d3aed4fdabeea8a4be3963d11cc4
decoded_sha256: dc8c1788cbb5b8655f52a066c5828e0c7982ad54379d94e29cc59363e9f70d4a
restored_to_import_sha256: f7f0b6b916b158d546849a38c09eb08aa2613567c513fe5e163ef1620b5c8f88
alpha_sha256: 6b1968870d415a30d46ae6128dbdc2076674f356b615826aafc336fbe71cabcf
changed_pixels: 16
unchanged_pixels: 4080
  (24,24) (42, 105, 116, 255) -> (255, 0, 255, 255)
  (25,24) (42, 105, 116, 255) -> (0, 0, 0, 255)
  (26,24) (42, 105, 116, 255) -> (255, 0, 255, 255)
  (27,24) (42, 105, 116, 255) -> (0, 0, 0, 255)
  (24,25) (42, 105, 116, 255) -> (0, 0, 0, 255)
  (25,25) (42, 105, 116, 255) -> (255, 0, 255, 255)
  (26,25) (42, 105, 116, 255) -> (0, 0, 0, 255)
  (27,25) (42, 105, 116, 255) -> (255, 0, 255, 255)
  (24,26) (42, 105, 116, 255) -> (255, 0, 255, 255)
  (25,26) (42, 105, 116, 255) -> (0, 0, 0, 255)
  (26,26) (42, 105, 116, 255) -> (255, 0, 255, 255)
  (27,26) (42, 105, 116, 255) -> (0, 0, 0, 255)
  (24,27) (42, 105, 116, 255) -> (0, 0, 0, 255)
  (25,27) (42, 105, 116, 255) -> (255, 0, 255, 255)
  (26,27) (42, 105, 116, 255) -> (0, 0, 0, 255)
  (27,27) (42, 105, 116, 255) -> (255, 0, 255, 255)
```

`restored_to_import_sha256` is the decoded RGBA of the file after those 16 pixels are put back to `(42, 105, 116, 255)`. It matches the decoded SHA-256 of the imported baseline (`963137f19e8e604689fae86a0f3c66a3ab4de50651b04dd7fb07f72744c5b9ad` on disk in commit `81275e1`). `alpha_sha256` is unchanged from that baseline. `python3 -m py_compile art/validate_human_placeholder.py` also exited 0.

Two negative checks were run and are supposed to fail:

- A copy with pixel (0, 0) recolored, alpha kept, exited 1: `pixels outside the marker do not match the import`.
- `--apply-from` pointed at the already marked file exited 1 because its SHA-256 is not the imported baseline.

Not executed:

- `./gradlew test`, `./gradlew build`, and `./gradlew runClient`. The project asks for Java 25. The only JDK on this machine is OpenJDK 21.0.12.1. No Gradle run was started.
- No Minecraft client, dedicated server, or Windows client. The previews are nearest-neighbor upscales of the PNG. They are not in-game screenshots. A Linux run would not be Windows gameplay verification anyway.
- No test that the decompiled 26.3 UV bytes match the jar this project's Loom would download.

## Open provenance and runtime limits

- Original author, original tool, and original asset license of `human.png` are unknown.
- Repository CC0 does not establish that authorship or asset license.
- `f8db1b9` is named in the baseline commit message and is not a commit in this clone.
- The marker makes the shirt read as unfinished. It is still not an approved design.
- Whether the unofficial 26.3 decompilation matches the exact jar Fabric Loom would use for `minecraft_version=26.3` was not confirmed by a local decompile.
- In-game placement of the checker (rest, turn, walk, hat, seams, handedness) was not observed.
- Pixels at y≥48 remain as imported. This humanoid mesh does not sample them. That was true before this edit.

## Integration

1. Core/QA review this branch. Merge stays blocked on Robert's later okay.
2. Re-run `python3 art/validate_human_placeholder.py` from the repository root. Expect exit 0 and the report above.
3. On a machine with JDK 25, `./gradlew build` packages the PNG. `processResources` does not rewrite PNG bytes. `verifyModJar` checks that the entry exists, not that the pixels match this manifest.
4. After a real client launch, run `/livingciv spawn human` and look at the front of the shirt for the magenta/black checker. Record that observation separately. Do not treat this handoff as that observation.
5. Replacement steps are in `art/ASSET_MANIFEST.md`.

## Source assessment — S1, S2, S3

Read only, on 2026-11-08. No edits. The repository's `docs/` directory contains exactly these four files. None of them contains the labels `S1`, `S2`, or `S3`, and none defines what those labels mean. This assessment does not assign those labels from similarity.

### `docs/Living_Civilizations_Codex_Development_Plan.md`

- Title: Living Civilizations — Codex Implementation Plan
- Explicit markers: Revision 2026-11-07. Project `livingcivilizations`. Reported stack Minecraft 26.3, Java 25, Fabric, with an instruction to inspect the installed versions. Implementation agent named as Codex. Creative authorship assigned to "user". Scope note says the phase numbers are a roadmap, that the document incorporates the original and updated Living Civilizations design documents, and that previous Forge/1.20.1 targets are obsolete.
- Explicit companion filenames: `Living_Civilizations_Complete_Mod_Design_and_Codex_Brief_Updated.md` and `Minecraft_Living_Civilizations_Mod_Plan.md`. Both are present in `docs/`.
- Scope: operating contract and a step roadmap from phase 0 through phase 10, including step 1.1 (entity registration and a placeholder renderer), asset handoff conventions, and a decision log. It does not label any source as S1, S2, or S3.

### `docs/Living_Civilizations_Complete_Mod_Design_and_Codex_Brief_Updated.md`

- Title: Living Civilizations — Complete Mod Design & Codex Development Brief
- Explicit markers: Status "Design specification; not yet implemented." Provisional target Minecraft Java 1.20.1, Forge, "confirm before setup." It tells Codex to treat this document as the design source of truth. A suggested prompt inside the file names `Living_Civilizations_Complete_Mod_Design_and_Codex_Brief.md` (no `_Updated`). That filename is not in `docs/`. An appended section is titled "Historical Architecture Reference Guide — November 7, 2026".
- Scope: vision, tribes, species, economy, construction, simulation layers, a three-phase roadmap aimed at 1.20.1 Forge, a first-milestone acceptance test, open decisions, and the November 7 architecture-tier notes (humans, primitive humanoids, underground reptilians) plus a short entity-art note (vanilla style, 64×64, Blockbench, GeckoLib only if necessary).
- The missing cited filename is not given an S1/S2/S3 identity in this file. The present `_Updated` file is not declared to be that missing filename.

### `docs/Minecraft_Living_Civilizations_Mod_Plan.md`

- Title: Living Civilizations — Minecraft Mod Design & Development Plan
- Explicit markers: Target version Minecraft Java 1.20.1, "provisional, based on earlier discussion." Mod loader "to decide before implementation." No revision date, no document ID, no S-label, no named source file.
- Scope: design vision, architecture layers, an MVP definition of done, phases 0–11, technical recommendations, a first development session (including one custom entity with a basic model/placeholder appearance), and open decisions.

### `docs/Windows-Installation.md`

- Title: Living Civilization on Windows
- Explicit markers: Windows 10 and 11 (64-bit), Minecraft Java Edition 26.3, Fabric Loader 0.20.1, Fabric API `0.165.2+26.3`, JAR name `living-civilization-0.1.0.jar`, Java 25. No revision date, no author line, no S-label, no source-document ID. It says Windows gameplay has not been verified.
- Scope: player install, a step 1.1 spawn check (`/livingciv spawn human`), and Windows build commands.
- The Codex plan's step 10.2 describes writing a Windows installation guide as later work. It does not name `docs/Windows-Installation.md`. This assessment does not identify the existing guide as that step's deliverable.

### Are S1–S3 present?

No file in `docs/` identifies itself, or any other file, as source document S1, S2, or S3. The Codex plan's two named companions are present under those filenames. The updated brief cites one filename that is absent: `Living_Civilizations_Complete_Mod_Design_and_Codex_Brief.md`. That absence is not an identification of S1, S2, or S3. Which documents those three labels refer to, and whether any of them is missing, cannot be decided from these four files.

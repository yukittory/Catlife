CATLIFE UPDATE — Minecraft Forge 1.20.1 / Forge 47.2.0 / Java 17

Files:
- src/main/java/com/example/catlife/Catlife.java (replace existing file)
- src/main/java/com/example/catlife/ToyItem.java (new file)
- src/main/resources/assets/catlife/models/item/*.json (replace the 3 existing models)

What this adds:
- Right-click with a toy to throw one copy forward.
- The thrown toy is a dropped item entity that cats can find and chase.
- Cats approach nearby toys, look at them, make small pounces and nudge toys.
- Item models use cube elements for a stepped 3D voxel appearance.

Important animation note:
This uses the vanilla cat renderer and its built-in movement animation. It does NOT add separately rigged paw/head/tail animations. Those require a custom cat renderer/model or an animation library such as GeckoLib, plus a separate model/animation setup.

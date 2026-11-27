CATLIFE — GeckoLib cat model starter (Forge 1.20.1)

Add the contents of this archive into the ROOT of your GitHub repository, preserving folders.
These are NEW files. Do NOT replace Catlife.java or ToyItem.java.

Files add:
- client Java classes: visual replacement renderer for vanilla cats
- assets/catlife/geo/cat.geo.json: articulated 3D cat model
- assets/catlife/animations/cat.animation.json: idle + walk animations
- assets/catlife/textures/entity/cat.png: starter texture

The model uses GeckoLib's DefaultedEntityGeoModel path convention:
geo/cat.geo.json
animations/cat.animation.json
textures/entity/cat.png

After adding, run GitHub Actions. If it builds but the game crashes or cats render as missing texture,
send the exact error text from latest.log or crash report.
This first pass adds idle/walk only. Sleeping, sitting, pouncing and pawing need separate state-aware
animation triggers in a later pass; the existing ToyPlayGoal remains untouched.

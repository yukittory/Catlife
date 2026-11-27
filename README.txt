Catlife - GeckoLib cat model files

Copy these folders into:
src/main/resources/assets/catlife/

Files:
geo/entity/cat.geo.json
animations/entity/cat.animation.json

Expected texture:
textures/entity/cat.png (64x64)

The animation names intentionally match GeckoLib DefaultAnimations.genericWalkIdleController:
- move.walk
- misc.idle

Important:
This is a first-pass blocky model and UV layout for a 64x64 texture. Since the PNG's exact pixel-art UV atlas layout is not documented, the UV placement may need visual adjustment in-game. It does not modify Java files.

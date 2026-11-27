CATLIFE - 3D TOY ITEM MODELS

Upload these files into:
src/main/resources/assets/catlife/models/item/

Replace:
toy_ball.json
toy_mouse.json
yarn_ball.json

These models use existing texture identifiers:
catlife:item/toy_ball
catlife:item/toy_mouse
catlife:item/yarn_ball

They use vanilla item-model cuboids (elements), so no GeckoLib is required for the static 3D shape.
The item JSON files only change the visual model; they do not change throwing or cat AI behavior.

If an item appears invisible, check that the corresponding PNG exists under:
src/main/resources/assets/catlife/textures/item/

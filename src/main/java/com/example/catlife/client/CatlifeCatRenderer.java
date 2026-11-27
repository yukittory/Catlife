package com.example.catlife.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.animal.Cat;
import software.bernie.geckolib.renderer.GeoReplacedEntityRenderer;

public class CatlifeCatRenderer extends GeoReplacedEntityRenderer<Cat, CatlifeCatAnimatable> {
    public CatlifeCatRenderer(EntityRendererProvider.Context context) {
        super(context, new CatlifeCatModel(), new CatlifeCatAnimatable());
    }
}

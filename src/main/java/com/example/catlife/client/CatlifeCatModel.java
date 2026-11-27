package com.example.catlife.client;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class CatlifeCatModel extends DefaultedEntityGeoModel<CatlifeCatAnimatable> {
    public CatlifeCatModel() {
        super(new ResourceLocation("catlife", "cat"));
    }
}

package com.example.catlife;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraft.world.entity.ai.goal.Goal;
import java.util.EnumSet;

@Mod(Catlife.MODID)
public class Catlife {
    public static final String MODID = "catlife";
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> YARN = ITEMS.register("yarn_ball", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> MOUSE = ITEMS.register("toy_mouse", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> BALL = ITEMS.register("toy_ball", () -> new Item(new Item.Properties().stacksTo(16)));

    public Catlife() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::creativeTab);
    }
    private void creativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(YARN); event.accept(MOUSE); event.accept(BALL);
        }
    }

    @Mod.EventBusSubscriber(modid=MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
    public static class Events {
        @SubscribeEvent
        public static void onLevelTick(TickEvent.LevelTickEvent event) {
            if (event.phase != TickEvent.Phase.END || event.level.isClientSide || event.level.getGameTime() % 30 != 0) return;
            Level level = event.level;
            for (Cat cat : level.getEntitiesOfClass(Cat.class, new AABB(-30000000, -64, -30000000, 30000000, 320, 30000000))) {
                if (!cat.isAlive() || cat.isOrderedToSit() || cat.getTarget() != null) continue;
                // Occasionally nudge nearby cats toward one another; vanilla movement handles pathfinding.
                if (level.random.nextInt(5) != 0) continue;
                Cat partner = level.getEntitiesOfClass(Cat.class, cat.getBoundingBox().inflate(7), other -> other != cat && other.isAlive() && !other.isOrderedToSit()).stream().findFirst().orElse(null);
                if (partner != null && cat.distanceToSqr(partner) > 2.5) {
                    cat.getNavigation().moveTo(partner, 0.9);
                } else if (partner != null) {
                    cat.getNavigation().stop();
                    partner.getNavigation().stop();
                    // Brief playful movement; no forced teleporting or damage.
                    Vec3 away = cat.position().subtract(partner.position()).normalize().scale(0.15);
                    cat.setDeltaMovement(cat.getDeltaMovement().add(away.x, 0.04, away.z));
                    cat.hurtMarked = true;
                }
            }
        }
        @SubscribeEvent
        public static void toyTick(TickEvent.LevelTickEvent event) {
            if (event.phase != TickEvent.Phase.END || event.level.isClientSide || event.level.getGameTime() % 20 != 0) return;
            Level level = event.level;
            for (ItemEntity toy : level.getEntitiesOfClass(ItemEntity.class, new AABB(-30000000, -64, -30000000, 30000000, 320, 30000000))) {
                Item item = toy.getItem().getItem();
                if (item != YARN.get() && item != MOUSE.get() && item != BALL.get()) continue;
                Cat cat = level.getEntitiesOfClass(Cat.class, toy.getBoundingBox().inflate(5), c -> c.isAlive() && !c.isOrderedToSit()).stream().findFirst().orElse(null);
                if (cat == null) continue;
                if (cat.distanceToSqr(toy) > 1.4) cat.getNavigation().moveTo(toy, 1.05);
                else {
                    Vec3 toss = new Vec3(level.random.nextDouble() - .5, .15, level.random.nextDouble() - .5).scale(.22);
                    toy.setDeltaMovement(toy.getDeltaMovement().add(toss));
                    toy.hasImpulse = true;
                }
            }
        }
    }
}

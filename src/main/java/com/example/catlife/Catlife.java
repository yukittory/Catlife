package com.example.catlife;

import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

import java.util.EnumSet;

@Mod(Catlife.MODID)
public class Catlife {
    public static final String MODID = "catlife";

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> YARN =
            ITEMS.register("yarn_ball", () -> new ToyItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> MOUSE =
            ITEMS.register("toy_mouse", () -> new ToyItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> BALL =
            ITEMS.register("toy_ball", () -> new ToyItem(new Item.Properties().stacksTo(16)));

    public Catlife() {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modBus);
        modBus.addListener(this::creativeTab);
    }

    private void creativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(YARN);
            event.accept(MOUSE);
            event.accept(BALL);
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class Events {
        @SubscribeEvent
        public static void onCatJoin(EntityJoinLevelEvent event) {
            if (event.getLevel().isClientSide || !(event.getEntity() instanceof Cat cat)) return;
            cat.goalSelector.addGoal(7, new ToyPlayGoal(cat));
        }
    }

    /**
     * Cats notice nearby Catlife toys, approach them, look at them and occasionally
     * bat them away. Movement and jumping use the vanilla cat renderer animations.
     */
    static class ToyPlayGoal extends Goal {
        private final Cat cat;
        private ItemEntity toy;
        private int playTicks;
        private int nextBatTick;

        ToyPlayGoal(Cat cat) {
            this.cat = cat;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        private boolean isToy(ItemEntity entity) {
            ItemStack stack = entity.getItem();
            return stack.is(YARN.get()) || stack.is(MOUSE.get()) || stack.is(BALL.get());
        }

        @Override
        public boolean canUse() {
            if (cat.isOrderedToSit() || cat.isSleeping() || cat.getRandom().nextInt(3) != 0) {
                return false;
            }

            toy = cat.level().getEntitiesOfClass(
                    ItemEntity.class,
                    cat.getBoundingBox().inflate(12.0),
                    e -> e.isAlive() && !e.getItem().isEmpty() && isToy(e)
            ).stream()
             .min((a, b) -> Double.compare(cat.distanceToSqr(a), cat.distanceToSqr(b)))
             .orElse(null);

            return toy != null;
        }

        @Override
        public boolean canContinueToUse() {
            return toy != null
                    && toy.isAlive()
                    && !cat.isOrderedToSit()
                    && !cat.isSleeping()
                    && playTicks < 220
                    && cat.distanceToSqr(toy) < 196.0;
        }

        @Override
        public void start() {
            playTicks = 0;
            nextBatTick = 15 + cat.getRandom().nextInt(20);
        }

        @Override
        public void tick() {
            if (toy == null || !toy.isAlive()) return;

            playTicks++;
            cat.getLookControl().setLookAt(toy, 30.0F, 30.0F);

            double distance = cat.distanceToSqr(toy);
            if (distance > 3.0) {
                cat.getNavigation().moveTo(toy, 1.15);
                return;
            }

            cat.getNavigation().stop();

            if (--nextBatTick <= 0) {
                Vec3 away = toy.position().subtract(cat.position());
                Vec3 flat = new Vec3(away.x, 0.0, away.z);
                if (flat.lengthSqr() < 0.001) {
                    flat = new Vec3(1.0, 0.0, 0.0);
                }
                flat = flat.normalize();

                // A small hop gives the cat a playful pounce.
                if (cat.onGround()) {
                    cat.setDeltaMovement(
                            cat.getDeltaMovement().add(-flat.x * 0.08, 0.30, -flat.z * 0.08)
                    );
                    cat.hasImpulse = true;
                }

                // Nudge the dropped toy so it rolls/skitters away from the paw.
                toy.setDeltaMovement(
                        toy.getDeltaMovement().add(flat.x * 0.20, 0.10, flat.z * 0.20)
                );
                toy.hasImpulse = true;
                nextBatTick = 25 + cat.getRandom().nextInt(30);
            }
        }

        @Override
        public void stop() {
            toy = null;
            cat.getNavigation().stop();
        }
    }
}

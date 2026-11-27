package com.example.catlife;

import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTabs;
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
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> YARN = ITEMS.register("yarn_ball", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> MOUSE = ITEMS.register("toy_mouse", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> BALL = ITEMS.register("toy_ball", () -> new Item(new Item.Properties().stacksTo(16)));

    public Catlife() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::creativeTab);
    }
    private void creativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(YARN); event.accept(MOUSE); event.accept(BALL);
        }
    }

    @Mod.EventBusSubscriber(modid=MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
    public static class Events {
        @SubscribeEvent
        public static void onCatJoin(EntityJoinLevelEvent event) {
            if (event.getLevel().isClientSide || !(event.getEntity() instanceof Cat cat)) return;
            cat.goalSelector.addGoal(7, new ToyPlayGoal(cat));
        }
    }

    /** Cats investigate nearby toys, path toward them, and occasionally paw/toss toys with small hops. */
    static class ToyPlayGoal extends Goal {
        private final Cat cat;
        private ItemEntity toy;
        private int playTicks;
        ToyPlayGoal(Cat cat) { this.cat = cat; setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }
        @Override public boolean canUse() {
            if (cat.isOrderedToSit() || cat.getRandom().nextInt(3) != 0) return false;
            toy = cat.level().getEntitiesOfClass(ItemEntity.class, cat.getBoundingBox().inflate(9),
                e -> !e.isRemoved() && (e.getItem().is(YARN.get()) || e.getItem().is(MOUSE.get()) || e.getItem().is(BALL.get())))
                .stream().min((a,b) -> Double.compare(cat.distanceToSqr(a), cat.distanceToSqr(b))).orElse(null);
            return toy != null;
        }
        @Override public boolean canContinueToUse() {
            return toy != null && toy.isAlive() && !cat.isOrderedToSit() && playTicks < 100 && cat.distanceToSqr(toy) < 100;
        }
        @Override public void start() { playTicks = 0; }
        @Override public void tick() {
            if (toy == null) return;
            playTicks++;
            cat.getLookControl().setLookAt(toy);
            if (cat.distanceToSqr(toy) > 2.0) cat.getNavigation().moveTo(toy, 1.12);
            else {
                cat.getNavigation().stop();
                if (cat.getRandom().nextInt(12) == 0) {
                    var v = toy.position().subtract(cat.position()).normalize();
                    cat.setDeltaMovement(cat.getDeltaMovement().add(v.x * 0.18, 0.24, v.z * 0.18));
                    cat.hasImpulse = true;
                    toy.setDeltaMovement(toy.getDeltaMovement().add(-v.x * 0.13, 0.08, -v.z * 0.13));
                    toy.hasImpulse = true;
                }
            }
        }
        @Override public void stop() { toy = null; cat.getNavigation().stop(); }
    }
}

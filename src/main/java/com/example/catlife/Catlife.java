
package com.example.catlife;

import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
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
            ITEMS.register("yarn_ball",
                    () -> new Item(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> MOUSE =
            ITEMS.register("toy_mouse",
                    () -> new Item(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> BALL =
            ITEMS.register("toy_ball",
                    () -> new Item(new Item.Properties().stacksTo(16)));

    public Catlife() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();

        ITEMS.register(bus);
        bus.addListener(this::creativeTab);
    }

    private void creativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(YARN);
            event.accept(MOUSE);
            event.accept(BALL);
        }
    }

    @Mod.EventBusSubscriber(
            modid = MODID,
            bus = Mod.EventBusSubscriber.Bus.FORGE
    )
    public static class Events {

        @SubscribeEvent
        public static void onCatJoin(EntityJoinLevelEvent event) {

            if (event.getLevel().isClientSide) return;

            if (!(event.getEntity() instanceof Cat cat)) return;

            cat.goalSelector.addGoal(7, new ToyPlayGoal(cat));
        }
    }

    static class ToyPlayGoal extends Goal {

        private final Cat cat;
        private ItemEntity toy;

        private int playTicks;
        private int cooldown;

        ToyPlayGoal(Cat cat) {
            this.cat = cat;

            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {

            if (cat.isOrderedToSit()) return false;

            if (cooldown > 0) {
                cooldown--;
                return false;
            }

            if (cat.getRandom().nextInt(4) != 0) {
                return false;
            }

            toy = cat.level()
                    .getEntitiesOfClass(
                            ItemEntity.class,
                            cat.getBoundingBox().inflate(9),
                            e -> !e.isRemoved()
                                    && e.isAlive()
                                    && (
                                    e.getItem().is(YARN.get())
                                    || e.getItem().is(MOUSE.get())
                                    || e.getItem().is(BALL.get())
                            )
                    )
                    .stream()
                    .min((a, b) ->
                            Double.compare(
                                    cat.distanceToSqr(a),
                                    cat.distanceToSqr(b)
                            )
                    )
                    .orElse(null);

            return toy != null;
        }

        @Override
        public boolean canContinueToUse() {

            return toy != null
                    && toy.isAlive()
                    && !toy.isRemoved()
                    && !cat.isOrderedToSit()
                    && playTicks < 160
                    && cat.distanceToSqr(toy) < 144;
        }

        @Override
        public void start() {
            playTicks = 0;
        }

        @Override
        public void tick() {

            if (toy == null || !toy.isAlive()) return;

            playTicks++;

            cat.getLookControl().setLookAt(toy, 30, 30);

            double distance = cat.distanceToSqr(toy);

            // Подходит к игрушке
            if (distance > 2.2) {
                cat.getNavigation().moveTo(toy, 1.15);
                return;
            }

            // Останавливается возле игрушки
            cat.getNavigation().stop();

            // Играет лапами и двигает игрушку
            if (cat.getRandom().nextInt(8) == 0) {

                Vec3 direction = toy.position()
                        .subtract(cat.position())
                        .normalize();

                double strength = 0.12 + cat.getRandom().nextDouble() * 0.08;

                toy.setDeltaMovement(
                        toy.getDeltaMovement().add(
                                direction.x * strength,
                                0.06,
                                direction.z * strength
                        )
                );

                toy.hasImpulse = true;
            }

            // Иногда делает игровой прыжок
            if (cat.getRandom().nextInt(35) == 0
                    && cat.onGround()) {

                cat.setDeltaMovement(
                        cat.getDeltaMovement().add(
                                0,
                                0.32,
                                0
                        )
                );

                cat.hasImpulse = true;
            }
        }

        @Override
        public void stop() {

            toy = null;

            cat.getNavigation().stop();

            cooldown = 200 + cat.getRandom().nextInt(300);
        }
    }
}

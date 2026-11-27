package com.example.catlife;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Right-click to toss one toy forward. The dropped item entity is what cats chase.
 */
public class ToyItem extends Item {
    public ToyItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);

        if (!level.isClientSide) {
            ItemStack thrownStack = held.copy();
            thrownStack.setCount(1);

            Vec3 look = player.getLookAngle();
            ItemEntity thrown = new ItemEntity(
                    level,
                    player.getX() + look.x * 0.45,
                    player.getEyeY() - 0.15,
                    player.getZ() + look.z * 0.45,
                    thrownStack
            );

            thrown.setPickUpDelay(30);
            thrown.setDeltaMovement(look.scale(0.85).add(0.0, 0.18, 0.0));
            thrown.hasImpulse = true;
            level.addFreshEntity(thrown);

            if (!player.getAbilities().instabuild) {
                held.shrink(1);
            }
        }

        return InteractionResultHolder.sidedSuccess(held, level.isClientSide);
    }
}

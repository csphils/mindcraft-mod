package com.mindcraftmod.item;

import com.mindcraftmod.entity.ModEntities;
import com.mindcraftmod.entity.SignalFlareProjectile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * Signal Flare — throwable item with three variants that trigger world events on landing.
 *
 * - RED:   Spawns Gas Cloud blocks in a 9x9 area (triggers Gas Attack)
 * - GREEN: Drops a Supply Crate from the sky at impact location
 * - GRAY:  Places Smoke Screen blocks in a 5x5 area at impact
 *
 * RED and GREEN broadcast a server-wide chat message.
 * Full world-event system (Phase 6) will hook into these events via server tick handlers.
 *
 * Crafting: Gunpowder + Dye (Red/Green/Gray) + Iron Ingot → 2x Signal Flare
 */
public class SignalFlareItem extends Item {

    /** The three variants of Signal Flare. */
    public enum Type {
        RED, GREEN, GRAY;
    }

    private final Type type;

    public SignalFlareItem(Type type, Settings settings) {
        super(settings);
        this.type = type;
    }

    public Type getFlareType() {
        return type;
    }

    @Override
    public ItemStack use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (!world.isClient) {
            // Create projectile carrying our flare type
            SignalFlareProjectile flare =
                    new SignalFlareProjectile(ModEntities.SIGNAL_FLARE_PROJECTILE, player, world, type);
            flare.setVelocity(player, player.getPitch(), player.getYaw(), -20f, 1.2f, 0.5f);
            world.spawnEntity(flare);

            // Firing sound
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.PLAYERS, 1.0f, 1.0f);

            // Broadcast for RED and GREEN variants
            if (type == Type.RED || type == Type.GREEN) {
                String eventName = type == Type.RED ? "Gas attack incoming" : "Supply drop inbound";
                Text msg = Text.literal("[War Event] " + eventName + " at "
                        + (int) player.getX() + ", " + (int) player.getZ() + "!");
                if (world.getServer() != null) {
                    world.getServer().getPlayerManager().broadcast(msg, false);
                }
            }

            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
            }
        }

        return stack;
    }
}

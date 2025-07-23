package me.contaria.anglesnapserver;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

public class ArrowTracker {

    /**
     * Stores arrow NBT data per player UUID when they disconnect.
     * Restores arrows on player reconnect.
     */
    private static final Map<UUID, List<NbtCompound>> savedArrowsByPlayer = new HashMap<>();

    /**
     * Registers connection event listeners to save and restore player arrows.
     */
    public void register() {
        // On player disconnect, save arrows owned by the player and remove them from the world
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayerEntity player = handler.player;
            UUID playerUuid = player.getUuid();
            ServerWorld world = player.getWorld();

            List<NbtCompound> arrowsToSave = new ArrayList<>();
            // Find all PersistentProjectileEntity within 128 blocks owned by this player
            List<PersistentProjectileEntity> arrows = world.getEntitiesByClass(
                    PersistentProjectileEntity.class,
                    player.getBoundingBox().expand(128),
                    entity -> {
                        Entity owner = entity.getOwner();
                        return owner != null && owner.getUuid().equals(playerUuid);
                    }
            );

            for (PersistentProjectileEntity arrow : arrows) {
                NbtCompound nbt = new NbtCompound();
                if (arrow.saveSelfNbt(nbt)) {
                    arrowsToSave.add(nbt);
                    arrow.discard();
                }
            }

            if (!arrowsToSave.isEmpty()) {
                savedArrowsByPlayer.put(playerUuid, arrowsToSave);
            }
        });

        // On player join, restore saved arrows back into the world and reassign ownership
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;
            UUID playerUuid = player.getUuid();
            ServerWorld world = player.getWorld();

            List<NbtCompound> savedArrows = savedArrowsByPlayer.remove(playerUuid);
            if (savedArrows != null) {
                for (NbtCompound nbt : savedArrows) {
                    Entity entity = Entity.loadEntityWithPassengers(nbt, world);
                    if (entity instanceof PersistentProjectileEntity arrow) {
                        arrow.setOwner(player);
                        world.spawnEntity(arrow);
                    }
                }
            }
        });
    }
}

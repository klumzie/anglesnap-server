package me.contaria.anglesnapserver;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

public class ArrowTracker {

    private final Map<UUID, List<NbtCompound>> arrowData = new HashMap<>();

    public void register() {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayerEntity player = handler.player;
            UUID uuid = player.getUuid();
            ServerWorld world = player.getWorld();

            List<NbtCompound> saved = new ArrayList<>();

            for (PersistentProjectileEntity arrow : world.getEntitiesByClass(
                    PersistentProjectileEntity.class,
                    player.getBoundingBox().expand(64),
                    entity -> uuid.equals(entity.getOwnerUuid())
            )) {
                NbtCompound tag = new NbtCompound();
                arrow.writeNbt(tag);
                saved.add(tag);
                arrow.discard();
            }

            if (!saved.isEmpty()) {
                arrowData.put(uuid, saved);
            }
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;
            UUID uuid = player.getUuid();
            ServerWorld world = player.getWorld();

            List<NbtCompound> saved = arrowData.remove(uuid);
            if (saved == null) return;

            for (NbtCompound tag : saved) {
                Entity e = EntityType.loadEntityFromNbt(tag, world);
                if (e instanceof PersistentProjectileEntity p) {
                    p.setOwner(player);
                }
                if (e != null) {
                    world.spawnEntity(e);
                }
            }
        });
    }
}

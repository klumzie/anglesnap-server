package me.contaria.anglesnapserver;

import me.contaria.anglesnapserver.mixin.EntityNbtAccess;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.entity.EntityType;

import java.util.*;

public class ArrowTracker {
    private static final Map<UUID, List<NbtCompound>> playerArrowData = new HashMap<>();

    public void register() {
        // Save arrows on disconnect
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayerEntity player = handler.player;
            UUID uuid = player.getUuid();
            ServerWorld world = player.getWorld();

            List<NbtCompound> arrowsToSave = new ArrayList<>();

            List<PersistentProjectileEntity> projectiles = world.getEntitiesByClass(
                PersistentProjectileEntity.class,
                player.getBoundingBox().expand(128),
                entity -> entity.getOwner() != null && entity.getOwner().getUuid().equals(uuid)
            );

            for (PersistentProjectileEntity arrow : projectiles) {
                NbtCompound nbt = ((EntityNbtAccess) arrow).anglesnap$toNbt();
                // Add the entity type ID so we can load it later
                nbt.putString("id", Registries.ENTITY_TYPE.getId(arrow.getType()).toString());
                arrowsToSave.add(nbt);
                arrow.discard();
            }

            if (!arrowsToSave.isEmpty()) {
                playerArrowData.put(uuid, arrowsToSave);
            }
        });

        // Restore arrows on join
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;
            UUID uuid = player.getUuid();
            ServerWorld world = player.getWorld();

            List<NbtCompound> arrows = playerArrowData.remove(uuid);
            if (arrows != null) {
                for (NbtCompound nbt : arrows) {
                    String id = nbt.getString("id");
                    EntityType<?> type = Registries.ENTITY_TYPE.get(new Identifier(id));
                    if (type != null) {
                        Entity entity = type.create(world);
                        if (entity instanceof PersistentProjectileEntity arrow) {
                            ((EntityNbtAccess) arrow).anglesnap$fromNbt(nbt);
                            arrow.setOwner(player);
                            world.spawnEntity(arrow);
                        }
                    }
                }
            }
        });
    }
}

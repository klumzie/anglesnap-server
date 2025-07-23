package me.contaria.anglesnapserver;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ArrowTracker {

    // The map now stores a list of our simple SavedArrow objects.
    private static final Map<UUID, List<SavedArrow>> playerArrowData = new HashMap<>();

    public void register() {
        // --- On Logout: Extract minimal data from arrows ---
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayerEntity player = handler.player;
            UUID uuid = player.getUuid();
            ServerWorld world = player.getWorld();

            List<SavedArrow> arrowsToSave = new ArrayList<>();
            List<PersistentProjectileEntity> projectiles = world.getEntitiesByClass(
                PersistentProjectileEntity.class,
                player.getBoundingBox().expand(128),
                entity -> entity.getOwner() != null && entity.getOwner().getUuid().equals(uuid)
            );

            for (PersistentProjectileEntity projectile : projectiles) {
                // Get the unique ID string for the entity type (e.g., "minecraft:arrow")
                Identifier typeId = EntityType.getId(projectile.getType());

                // Create a new SavedArrow object with only the data we need.
                SavedArrow savedArrow = new SavedArrow(
                    typeId.toString(),
                    projectile.getPos(),
                    projectile.getVelocity(),
                    projectile.getDamage()
                );
                arrowsToSave.add(savedArrow);

                // Discard the original entity.
                projectile.discard();
            }

            if (!arrowsToSave.isEmpty()) {
                playerArrowData.put(uuid, arrowsToSave);
            }
        });

        // --- On Login: Rebuild arrows from minimal data ---
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;
            UUID uuid = player.getUuid();
            ServerWorld world = player.getWorld();

            List<SavedArrow> arrowsToRestore = playerArrowData.remove(uuid);

            if (arrowsToRestore != null) {
                for (SavedArrow saved : arrowsToRestore) {
                    // Find the entity type from its saved ID string.
                    EntityType.get(saved.typeId).ifPresent(entityType -> {
                        // Create a new entity instance. We assume it's a PersistentProjectileEntity.
                        // Note: A more complex setup would be needed for Tridents vs Arrows.
                        // For arrows, this is fine.
                        PersistentProjectileEntity newArrow = new ArrowEntity(world, player);

                        // Manually set all the saved properties.
                        newArrow.setOwner(player);
                        newArrow.setPosition(saved.position);
                        newArrow.setVelocity(saved.velocity);
                        newArrow.setDamage(saved.damage);

                        // Spawn the newly created arrow.
                        world.spawnEntity(newArrow);
                    });
                }
            }
        });
    }

    /**
     * A simple data class to hold only the essential arrow properties.
     * This is more stable across game updates than saving the full NBT.
     */
    private static class SavedArrow {
        final String typeId;
        final Vec3d position;
        final Vec3d velocity;
        final double damage;

        SavedArrow(String typeId, Vec3d position, Vec3d velocity, double damage) {
            this.typeId = typeId;
            this.position = position;
            this.velocity = velocity;
            this.damage = damage;
        }
    }
}
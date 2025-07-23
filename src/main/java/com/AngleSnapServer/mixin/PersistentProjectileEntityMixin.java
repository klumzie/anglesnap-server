package me.contaria.anglesnapserver.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin {

    // Helper method to get the current entity instance.
    private PersistentProjectileEntity asEntity() {
        return (PersistentProjectileEntity) (Object) this;
    }

    /**
     * Injects into the writeNbt method, which is the modern equivalent for saving data.
     * This avoids the need for custom WriteView classes.
     */
    @Inject(method = "writeNbt", at = @At("HEAD"))
    private void writeOwnerToNbt(NbtCompound nbt, CallbackInfo ci) {
        Entity owner = this.asEntity().getOwner();
        if (owner != null) {
            nbt.putUuid("CustomOwnerUUID", owner.getUuid());
        }
    }

    /**
     * Injects into the readNbt method, the modern equivalent for loading data.
     * This directly provides the NbtCompound to read from.
     */
    @Inject(method = "readNbt", at = @At("HEAD"))
    private void readOwnerFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.containsUuid("CustomOwnerUUID")) {
            var uuid = nbt.getUuid("CustomOwnerUUID");
            
            // The owner can only be resolved if the entity is in a server world.
            if (this.asEntity().getWorld() instanceof ServerWorld serverWorld) {
                var owner = serverWorld.getServer().getPlayerManager().getPlayer(uuid);
                if (owner != null) {
                    this.asEntity().setOwner(owner);
                }
            }
        }
    }
}
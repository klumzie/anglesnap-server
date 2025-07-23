package me.contaria.anglesnapserver.mixin;

import me.contaria.anglesnapserver.storage.NbtWriteView;
import me.contaria.anglesnapserver.storage.NbtReadView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin extends Entity {

    public PersistentProjectileEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    private void writeOwner(WriteView view, CallbackInfo ci) {
        Entity owner = ((PersistentProjectileEntity)(Object)this).getOwner();
        if (owner != null) {
            view.putUuid("OwnerUUID", owner.getUuid());
        }
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void readOwner(ReadView view, CallbackInfo ci) {
        if (view.getNbt().containsUuid("OwnerUUID")) {
            var uuid = view.getNbt().getUuid("OwnerUUID");
            if (this.getWorld() instanceof ServerWorld serverWorld) {
                ServerPlayerEntity player = serverWorld.getServer().getPlayerManager().getPlayer(uuid);
                if (player != null) {
                    ((PersistentProjectileEntity)(Object)this).setOwner(player);
                }
            }
        }
    }
}

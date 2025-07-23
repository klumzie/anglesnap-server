package me.contaria.anglesnapserver.mixin;

import me.contaria.anglesnapserver.storage.NbtReadView;
import me.contaria.anglesnapserver.storage.NbtWriteView;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityNbtAccess {
    @Shadow protected abstract void writeCustomData(WriteView view);
    @Shadow protected abstract void readCustomData(ReadView view);

    @Override
    public NbtCompound anglesnap$toNbt() {
        NbtCompound nbt = new NbtCompound();
        writeCustomData(new NbtWriteView(nbt));
        return nbt;
    }

    @Override
    public void anglesnap$fromNbt(NbtCompound nbt) {
        readCustomData(new NbtReadView(nbt));
    }
}

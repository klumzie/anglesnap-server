package me.contaria.anglesnapserver.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.storage.FakeWriteView;
import net.minecraft.storage.FakeReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.storage.ReadView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityNbtAccess {

    @Shadow protected abstract void writeCustomData(WriteView view);
    @Shadow protected abstract void readCustomData(ReadView view);

    @Override
    public NbtCompound anglesnap$toNbt() {
        NbtCompound nbt = new NbtCompound();
        writeCustomData(new FakeWriteView(nbt));
        return nbt;
    }

    @Override
    public void anglesnap$fromNbt(NbtCompound nbt) {
        readCustomData(new FakeReadView(nbt));
    }
}

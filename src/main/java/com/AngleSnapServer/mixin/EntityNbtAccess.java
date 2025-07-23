package me.contaria.anglesnapserver.mixin;

import net.minecraft.nbt.NbtCompound;

public interface EntityNbtAccess {
    NbtCompound anglesnap$toNbt();
    void anglesnap$fromNbt(NbtCompound nbt);
}

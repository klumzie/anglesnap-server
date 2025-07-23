package me.contaria.anglesnapserver.storage;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.storage.ReadView;
import net.minecraft.registry.RegistryWrapper;

import java.util.Optional;

public class NbtReadView implements ReadView {
    private final NbtCompound nbt;

    public NbtReadView(NbtCompound nbt) {
        this.nbt = nbt;
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        if (type == NbtCompound.class && nbt.contains(key)) {
            return type.cast(nbt.getCompound(key));
        }
        return null;
    }

    @Override
    public boolean contains(String key) {
        return nbt.contains(key);
    }

    @Override

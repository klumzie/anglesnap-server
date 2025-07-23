package me.contaria.anglesnapserver.storage;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.storage.WriteView;
import net.minecraft.registry.entry.RegistryEntryOwner;

public class NbtWriteView implements WriteView {
    private final NbtCompound nbt;

    public NbtWriteView(NbtCompound nbt) {
        this.nbt = nbt;
    }

    @Override
    public void put(String key, Object value) {
        if (value instanceof NbtCompound compound) {
            nbt.put(key, compound);
        }
    }

    @Override
    public boolean isEmpty() {
        return nbt.isEmpty();
    }

    @Override
    public <O> RegistryEntryOwner<O> getOwner(Class<O> type) {
        throw new UnsupportedOperationException("Registry access not supported");
    }
}

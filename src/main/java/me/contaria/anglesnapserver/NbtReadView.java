package me.contaria.anglesnapserver.storage;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.storage.ReadView;

public class NbtReadView implements ReadView {
    private final NbtCompound nbt;

    public NbtReadView(NbtCompound nbt) {
        this.nbt = nbt;
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        if (type == NbtCompound.class) {
            return type.cast(nbt.getCompound(key));
        }
        return null;
    }

    public NbtCompound getNbt() {
        return nbt;
    }
}

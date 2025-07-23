package me.contaria.anglesnapserver.storage;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.storage.WriteView;

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
        // You can add more types here if needed
    }

    public NbtCompound getNbt() {
        return nbt;
    }
}

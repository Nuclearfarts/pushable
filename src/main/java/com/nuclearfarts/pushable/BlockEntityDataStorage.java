package com.nuclearfarts.pushable;

import net.minecraft.nbt.CompoundTag;

public interface BlockEntityDataStorage {
	public CompoundTag pushable_getBlockEntityData();
	public void pushable_setBlockEntityData(CompoundTag e);
}

package com.nuclearfarts.pushable.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.nuclearfarts.pushable.BlockStateCacher;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin implements BlockStateCacher {

	@Shadow
	private BlockState cachedState;
	
	@Override
	public void pushable_setCachedState(BlockState state) {
		this.cachedState = state;
	}

}

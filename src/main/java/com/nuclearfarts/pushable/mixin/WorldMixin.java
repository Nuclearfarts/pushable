package com.nuclearfarts.pushable.mixin;

import java.util.HashMap;

import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.nuclearfarts.pushable.BlockStateCacher;
import com.nuclearfarts.pushable.Pushable;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

@Mixin(World.class)
public abstract class WorldMixin implements ExtendedBlockView, IWorld, AutoCloseable {
	
	@Inject(at = @At("TAIL"), method = "tickBlockEntities()V")
	private void neighborUpdateWorkaround(CallbackInfo info) {
		for(HashMap.Entry<BlockPos, CompoundTag> e : Pushable.BLOCK_ENTITIES_TO_READ.entrySet()) {
			BlockEntity be = getBlockEntity(e.getKey());
			if(be != null) {
				System.out.println(getBlockState(e.getKey()));
				((BlockStateCacher)be).pushable_setCachedState(getBlockState(e.getKey()));
				be.fromTag(e.getValue());
				be.setPos(e.getKey());
				be.markDirty();
			} else {
				Pushable.LOGGER.log(Level.WARN, "Attempted to load BlockEntity " + e.getValue() + " at " + e.getKey() + " but there was no BE to load into.");
			}
		}
		Pushable.BLOCK_ENTITIES_TO_READ.clear();
	}
}

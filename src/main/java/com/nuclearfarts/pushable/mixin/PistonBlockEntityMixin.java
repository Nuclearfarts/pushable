package com.nuclearfarts.pushable.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.nuclearfarts.pushable.BlockEntityDataStorage;
import com.nuclearfarts.pushable.Pushable;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;

@Mixin(PistonBlockEntity.class)
public abstract class PistonBlockEntityMixin extends BlockEntity implements Tickable, BlockEntityDataStorage {

	private PistonBlockEntityMixin(BlockEntityType<?> blockEntityType_1) {
		super(blockEntityType_1);
	}
	
	@Unique
	private CompoundTag pushable_blockEntity;
	
	@Override
	public CompoundTag pushable_getBlockEntityData() {
		return pushable_blockEntity;
	}
	
	@Override
	public void pushable_setBlockEntityData(CompoundTag tag) {
		pushable_blockEntity = tag;
	}
	
	@Inject(at = @At("TAIL"), method = "toTag(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/nbt/CompoundTag;")
	private void addBlockEntityDataToTag(CompoundTag tag, CallbackInfoReturnable<CompoundTag> info) {
		tag.put("pushedblockentity", this.pushable_blockEntity);
	}
	
	@Inject(at = @At("TAIL"), method = "fromTag(Lnet/minecraft/nbt/CompoundTag;)V")
	private void loadBlockEntityDataFromTag(CompoundTag tag, CallbackInfo info) {
		if(tag.containsKey("pushedblockentity")) {
			this.pushable_blockEntity = tag.getCompound("pushedblockentity");
		}
	}
	
	@Inject(at = @At(value = "INVOKE_ASSIGN", target = "net/minecraft/world/World.setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z", ordinal = 1), method = "tick()V")
	private void finishTick(CallbackInfo info) {
		finishMovingBlockEntity();
	}
	
	@Inject(at = @At(value = "INVOKE_ASSIGN", target = "net/minecraft/world/World.setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z", ordinal = 0), method = "finish()V")
	private void finish(CallbackInfo info) {
		finishMovingBlockEntity();
	}
	
	@Unique
	private void finishMovingBlockEntity() {
		if(this.pushable_blockEntity != null) {
			Pushable.BLOCK_ENTITIES_TO_READ.put(pos, pushable_blockEntity);
		}
	}
}

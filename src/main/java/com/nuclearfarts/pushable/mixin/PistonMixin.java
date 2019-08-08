package com.nuclearfarts.pushable.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonExtensionBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.nuclearfarts.pushable.BlockEntityDataStorage;
import com.nuclearfarts.pushable.Pushable;

@Mixin(PistonBlock.class)
public abstract class PistonMixin {
	
	@Unique
	private ThreadLocal<CompoundTag> blockEntityData = new ThreadLocal<CompoundTag>();
	
	//Make pistons able to move whitelisted BEs
	@Inject(at = @At(value = "RETURN", ordinal = 9), cancellable = true, method = "isMovable(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;ZLnet/minecraft/util/math/Direction;)Z") //ord 9
	private static void allowMove(BlockState state, World world, BlockPos pos, Direction d1, boolean b, Direction d2, CallbackInfoReturnable<Boolean> info) {
		info.setReturnValue(Pushable.WHITELIST.contains(state.getBlock()));
	}
	
	
	@Inject(locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "net/minecraft/util/math/BlockPos.offset(Lnet/minecraft/util/math/Direction;)Lnet/minecraft/util/math/BlockPos;", ordinal = 1), method = "move(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Z)Z")
	private void storeBlockEntities(World world_1, BlockPos blockPos_1, Direction direction_1, boolean boolean_1, CallbackInfoReturnable<Boolean> cir, BlockPos blockPos_2, PistonHandler pistonHandler_1, List<BlockPos> list_1, List<BlockState> list_2, List<BlockPos> list_3, int int_2, BlockState blockStates_1[], Direction direction_2, Set<BlockPos> set_1, int int_4, BlockPos prepushPos, BlockState prepushState) {
		if(prepushState.getBlock().hasBlockEntity()) {
			System.out.println(prepushState);
			BlockEntity blockEntity = world_1.getBlockEntity(prepushPos);
			world_1.removeBlockEntity(prepushPos);
			CompoundTag tag = blockEntity.toTag(new CompoundTag());
			System.out.println(tag);
			blockEntityData.set(tag); //save the BE data
		} else {
			blockEntityData.set(null);
		}
	}
	
	@Redirect(at = @At(value = "INVOKE", target = "net/minecraft/block/PistonExtensionBlock.createBlockEntityPiston(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;ZZ)Lnet/minecraft/block/entity/BlockEntity;", ordinal = 0), method = "move(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Z)Z")
	private BlockEntity createPistonBlockEntity(BlockState state, Direction direction, boolean b1, boolean b2) {
		BlockEntity blockEntity = PistonExtensionBlock.createBlockEntityPiston(state, direction, b1, b2);
		((BlockEntityDataStorage)blockEntity).pushable_setBlockEntityData(blockEntityData.get());
		return blockEntity;
	}
}

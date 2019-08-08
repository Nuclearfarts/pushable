package com.nuclearfarts.pushable.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.nuclearfarts.pushable.BlockEntityDataStorage;
import com.nuclearfarts.pushable.BlockStateCacher;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.PistonBlockEntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.math.BlockPos;

@Mixin(PistonBlockEntityRenderer.class)
public abstract class PistonRendererMixin extends BlockEntityRenderer<PistonBlockEntity> {
	@Shadow
	private BlockRenderManager manager;
	
	
	//@Inject(locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", ordinal = 3, target = "method_3575(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/client/render/BufferBuilder;Lnet/minecraft/world/World;Z)Z"), method = "method_3576(Lnet/minecraft/block/entity/PistonBlockEntity;DDDFI)V")
	@Inject(locals = LocalCapture.CAPTURE_FAILHARD, at = @At("TAIL"), method = "method_3576(Lnet/minecraft/block/entity/PistonBlockEntity;DDDFI)V")
	private void renderBlockEntityPush(PistonBlockEntity blockEntity, double x, double y, double z, float float_1, int i, CallbackInfo info, BlockPos blockPos_1, BlockState blockState_1, Tessellator tessellator_1, BufferBuilder bufferBuilder_1) {
		if(blockState_1.getBlock().hasBlockEntity()) {
			BlockEntity renBlockEntity = BlockEntity.createFromTag(((BlockEntityDataStorage)blockEntity).pushable_getBlockEntityData());
			renBlockEntity.setWorld(blockEntity.getWorld());
			((BlockStateCacher)renBlockEntity).pushable_setCachedState(blockEntity.getPushedBlock());
			BlockEntityRenderDispatcher.INSTANCE.renderEntity(renBlockEntity, x + blockEntity.getRenderOffsetX(float_1), y + blockEntity.getRenderOffsetY(float_1), z + blockEntity.getRenderOffsetZ(float_1), 0, -1, true);
			bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		}
	}
}

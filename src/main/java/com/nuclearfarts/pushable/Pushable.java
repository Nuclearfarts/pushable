package com.nuclearfarts.pushable;

import java.util.HashMap;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.math.BlockPos;

public class Pushable implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger("Pushable");
	public static final HashSet<Block> WHITELIST = new HashSet<Block>();
	public static final HashMap<BlockPos, CompoundTag> BLOCK_ENTITIES_TO_READ = new HashMap<BlockPos, CompoundTag>();

	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new WhitelistDataListener());
	}
	
	public static void yeet() {
		System.out.println("yeet");
	}
}

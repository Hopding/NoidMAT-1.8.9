package com.hopding.noidmat;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.BlockPos;

public class Utilities {
	public static final String[] LAYER_BLOCKS = {
			"minecraft:snow_layer",
			"minecraft:tallgrass"
	};

	public static String getBlockName(Block block) {
		return Block.blockRegistry.getNameForObject(block).toString();
	}
	
	public static Block getBlock(BlockPos blockPos) {
		IBlockState ibs = Minecraft.getMinecraft().theWorld.getBlockState(blockPos);
		WorldClient mcWorld = Minecraft.getMinecraft().theWorld;
		return ibs.getBlock();
	}

	public static boolean isLayerBlock(Block block) {
		String blockName = getBlockName(block);
		for(String layerBlock : LAYER_BLOCKS)
			if(blockName.equals(layerBlock))
				return true;
		return false;
	}
}

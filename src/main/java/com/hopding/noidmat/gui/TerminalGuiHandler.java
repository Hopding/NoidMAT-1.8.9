package com.hopding.noidmat.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * {@code TerminalGuiHandler} is the {@code IGuiHandler} implementation for {@code Terminal}.
 */
@SideOnly(Side.CLIENT)
public class TerminalGuiHandler implements IGuiHandler {
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == Terminal.GUI_ID)
			return new Terminal();
		return null;
	}
	
}

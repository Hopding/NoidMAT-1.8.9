package com.hopding.noidmat;

import com.hopding.noidmat.gui.TerminalGuiHandler;
import com.hopding.noidmat.input.KeyboardLocker;
import com.hopding.noidmat.input.MouseLocker;
import com.hopding.noidmat.task.TaskCompiler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod(modid = NoidMAT.MODID, name = NoidMAT.MODNAME, version = NoidMAT.MODVER)
public class NoidMAT {
	// Set the ID of the mod (Should be lower case).
	public static final String MODID = "noidmat";
	// Set the "Name" of the mod.
	public static final String MODNAME = "NoidMAT";
	// Set the version of the mod.
	public static final String MODVER = "0.0.0";
	
	@Instance(value = NoidMAT.MODID) // Tell Forge what instance to use.
	public static NoidMAT instance;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
	MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
	MinecraftForge.EVENT_BUS.register(Player.getPlayer());
	MinecraftForge.EVENT_BUS.register(KeyboardLocker.getKeyboardLocker());
	MinecraftForge.EVENT_BUS.register(MouseLocker.getMouseLocker());
	NetworkRegistry.INSTANCE.registerGuiHandler(instance, new TerminalGuiHandler());
	KeyBindings.init();
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		Thread taskCompiler = new TaskCompiler();
		taskCompiler.setDaemon(true);
		taskCompiler.setName("Task-Compiler-Thread");
		taskCompiler.start();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}
}
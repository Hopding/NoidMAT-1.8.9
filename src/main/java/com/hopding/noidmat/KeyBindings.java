package com.hopding.noidmat;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

/**
 * {@code KeyBindings} is the class responsible for initializing and registering the keybindings for NoidMAT.
 */
@SideOnly(Side.CLIENT)
public class KeyBindings {
	public static KeyBinding	holdForward;
	public static KeyBinding	holdBack;
	public static KeyBinding	holdRight;
	public static KeyBinding	holdLeft;
	public static KeyBinding	holdJump;
	public static KeyBinding	leftAlt;
	public static KeyBinding	testKeyX;
	public static KeyBinding	testKeyZ;
	public static KeyBinding	testKeyB;
								
	public static void init() {
		holdForward = new KeyBinding("key.holdForward", Keyboard.KEY_UP, "key.categories.noidmat");
		holdBack = new KeyBinding("key.holdBack", Keyboard.KEY_DOWN, "key.categories.noidmat");
		holdRight = new KeyBinding("key.holdRight", Keyboard.KEY_RIGHT, "key.categories.noidmat");
		holdLeft = new KeyBinding("key.holdLeft", Keyboard.KEY_LEFT, "key.categories.noidmat");
		holdJump = new KeyBinding("key.holdJump", Keyboard.KEY_RCONTROL, "key.categories.noidmat");
		leftAlt = new KeyBinding("key.leftAlt", Keyboard.KEY_LMENU, "key.categories.noidmat");
		testKeyX = new KeyBinding("key.testKeyX", Keyboard.KEY_X, "key.categories.noidmat");
		testKeyZ = new KeyBinding("key.testKeyZ", Keyboard.KEY_Z, "key.categories.noidmat");
		testKeyB = new KeyBinding("key.testKeyB", Keyboard.KEY_B, "key.categories.noidmat");
		
		ClientRegistry.registerKeyBinding(holdForward);
		ClientRegistry.registerKeyBinding(holdBack);
		ClientRegistry.registerKeyBinding(holdRight);
		ClientRegistry.registerKeyBinding(holdLeft);
		ClientRegistry.registerKeyBinding(holdJump);
		ClientRegistry.registerKeyBinding(leftAlt);
		ClientRegistry.registerKeyBinding(testKeyX);
		ClientRegistry.registerKeyBinding(testKeyZ);
		ClientRegistry.registerKeyBinding(testKeyB);
	}
}

package com.hopding.noidmat;

import com.hopding.noidmat.gui.Terminal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Handles input from the keyboard.
 * If input is being blocked by KeyboardLocker, no key events will be detected by {@code KeyInputHandler}
 */
@SideOnly(Side.CLIENT)
public class KeyInputHandler {
	
	private GameSettings	gameSettings;
	private KeyBinding		forwardKey;
	private KeyBinding		backwardKey;
	private KeyBinding		rightKey;
	private KeyBinding		leftKey;
	private KeyBinding		jumpKey;
							
	public KeyInputHandler() {
		this.gameSettings = Minecraft.getMinecraft().gameSettings;
		forwardKey = gameSettings.keyBindForward;
		backwardKey = gameSettings.keyBindBack;
		rightKey = gameSettings.keyBindRight;
		leftKey = gameSettings.keyBindLeft;
		jumpKey = gameSettings.keyBindJump;
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if (KeyBindings.testKeyX.isPressed()) {
			Terminal.open();
		}
		if (KeyBindings.testKeyZ.isPressed()) {
		}
		if (KeyBindings.testKeyB.isPressed()) {
		}

			if (KeyBindings.holdForward.isPressed()) {
				if (forwardKey.isKeyDown())
					forwardKey.setKeyBindState(forwardKey.getKeyCode(), false);
				else
					forwardKey.setKeyBindState(forwardKey.getKeyCode(), true);
			}
			if (KeyBindings.holdJump.isPressed()) {
				if (jumpKey.isKeyDown())
					jumpKey.setKeyBindState(jumpKey.getKeyCode(), false);
				else
					jumpKey.setKeyBindState(jumpKey.getKeyCode(), true);
			}
			if (KeyBindings.holdBack.isPressed()) {
				if (backwardKey.isKeyDown())
					backwardKey.setKeyBindState(backwardKey.getKeyCode(), false);
				else
					backwardKey.setKeyBindState(backwardKey.getKeyCode(), true);
			}
			if (KeyBindings.holdRight.isPressed()) {
				if (rightKey.isKeyDown())
					rightKey.setKeyBindState(rightKey.getKeyCode(), false);
				else
					rightKey.setKeyBindState(rightKey.getKeyCode(), true);
			}
			if (KeyBindings.holdLeft.isPressed()) {
				if (leftKey.isKeyDown())
					leftKey.setKeyBindState(leftKey.getKeyCode(), false);
				else
					leftKey.setKeyBindState(leftKey.getKeyCode(), true);
			}
	}
}

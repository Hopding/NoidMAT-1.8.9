package com.hopding.noidmat.gui;

import com.hopding.noidmat.CommandHandler;
import com.hopding.noidmat.NoidMAT;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

/**
 * {@code Terminal} is the gui terminal used to input commands to NoidMAT.
 */
@SideOnly(Side.CLIENT)
public class Terminal extends GuiScreen {
	private GuiTextField	textField;
	public static final int	GUI_ID	= 21;
									
	public void initGui() {
		textField = new GuiTextField(GUI_ID, this.fontRendererObj, this.width / 2 - 68, this.height / 2 - 46, 137, 20);
		textField.setFocused(true);
	}
	
	public void updateScreen() {
		super.updateScreen();
		this.textField.updateCursorCounter();
	}
	
	public void drawScreen(int par1, int par2, float par3) {
		this.drawDefaultBackground();
		this.textField.drawTextBox();
		super.drawScreen(par1, par2, par3);
	}
	
	protected void keyTyped(char par1, int par2) {
		try {
			super.keyTyped(par1, par2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.textField.textboxKeyTyped(par1, par2);
		if (par2 == Keyboard.KEY_RETURN) {
			Minecraft.getMinecraft().displayGuiScreen(null);
			CommandHandler.process(textField.getText());
		}
	}
	
	protected void mouseClicked(int x, int y, int btn) {
		try {
			super.mouseClicked(x, y, btn);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.textField.mouseClicked(x, y, btn);
	}

	public static void open() {
		Minecraft.getMinecraft().thePlayer.openGui(NoidMAT.instance, Terminal.GUI_ID,
				Minecraft.getMinecraft().theWorld, 0, 0, 0);
	}
}

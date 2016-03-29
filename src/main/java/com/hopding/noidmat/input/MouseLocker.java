package com.hopding.noidmat.input;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

/**
 * {@code MouseLocker} is responsible for locking and unlocking the mouse input (motion and buttons) to Minecraft.
 * <p>
 * {@code MouseLocker} complies with the Singleton design pattern.
 * An instance of {@code MouseLocker} can be retrieved with the following code:
 * <pre>{@code
 * MouseLocker mouseLocker = MouseLocker.getMouseLocker();
 * }</pre>
 */
@SideOnly(Side.CLIENT)
public class MouseLocker {
    private static MouseLocker mouseLocker;
    private boolean isMousePosLocked;
    private boolean isMouseBtnsLocked;

    private MouseLocker() {
    }

    public static MouseLocker getMouseLocker() {
        if(mouseLocker == null)
            mouseLocker = new MouseLocker();
        return mouseLocker;
    }

    public boolean isMousePosLocked() {
        return isMousePosLocked;
    }

    public void lockMousePos() {
        isMousePosLocked = true;
    }

    public void unlockMousePos() {
        isMousePosLocked = false;
    }

    public boolean areMouseBtnsLocked() {
        return isMouseBtnsLocked;
    }

    public void lockMouseButtons() {
        isMouseBtnsLocked = true;
    }

    public void unlockMouseButtons() {
        isMouseBtnsLocked = false;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if(isMousePosLocked && event.phase == TickEvent.Phase.START) {
            Mouse.getDX();
            Mouse.getDY();
            Minecraft.getMinecraft().mouseHelper.deltaX = 0;
            Minecraft.getMinecraft().mouseHelper.deltaY = 0;
        }
        if(isMouseBtnsLocked && event.phase == TickEvent.Phase.START) {
            while(Mouse.next()) {}
        }
    }
}

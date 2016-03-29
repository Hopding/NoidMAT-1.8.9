package com.hopding.noidmat;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * {@code ActionMonitor}s are used by {@code Player} to monitor the progress of specific actions.
 */
@SideOnly(Side.CLIENT)
public abstract class ActionMonitor {
	private boolean actionComplete;
	
	public void setActionCompleted() {
		actionComplete = true;
	}
	
	public boolean actionIsComplete() {
		return actionComplete;
	}
	
	public abstract void update();
	
}

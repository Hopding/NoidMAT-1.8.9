package com.hopding.noidmat.task;

import com.hopding.noidmat.Player;
import com.hopding.noidmat.exceptions.FailedToCompleteTaskException;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Used to run and terminate tasks
 */
@SideOnly(Side.CLIENT)
public class TaskRunner {
	private static Thread currentTaskThread;
	
	public static void runTask(final Task task, final String... args) {
		currentTaskThread = new Thread() {
			public void run() {
				try {
					task.run(Player.getPlayer(), args);
				} catch (FailedToCompleteTaskException e) {
					e.printStackTrace();
				}
			}
		};
		currentTaskThread.start();
	}
	
	public static void stopCurrentTask() {
		if (currentTaskThread != null) {
			currentTaskThread.interrupt();
			Player.getPlayer().cancelAction();
			currentTaskThread = null;
		}
	}
}

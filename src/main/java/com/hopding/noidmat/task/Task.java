package com.hopding.noidmat.task;

import com.hopding.noidmat.Player;
import com.hopding.noidmat.exceptions.FailedToCompleteTaskException;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * See {@link com.hopding.noidmat.Player Player} for an explanation of how to use this abstract class.
 */
@SideOnly(Side.CLIENT)
public abstract class Task {
	public abstract void run(Player player, String... args) throws FailedToCompleteTaskException;
	
	public static Task loadTask(String className)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
	ClassLoader parentLoader = Task.class.getClassLoader();
	URLClassLoader childLoader = new URLClassLoader(new URL[] { TaskCompiler.TASK_CLASS_DIR.toURI().toURL() }, parentLoader);
	Class cls = childLoader.loadClass(className);
	return (Task) cls.newInstance();
	}
}

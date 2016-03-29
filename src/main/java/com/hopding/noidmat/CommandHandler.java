package com.hopding.noidmat;

import com.hopding.noidmat.task.Task;
import com.hopding.noidmat.task.TaskRunner;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;

/**
 * {@code CommandHandler} is responsible for processing the commands fed into the NoidMAT {@code Terminal}.
 */
@SideOnly(Side.CLIENT)
public class CommandHandler {
	public static void process(String command) {
		try {
			if(command.contains(" ")) {
				String[] cmdArray = command.split(" ");
				command = cmdArray[0];
				System.out.println(command);
				String[] args = new String[cmdArray.length - 1];
				for(int i = 0; i < args.length; i++) {
					args[i] = cmdArray[i + 1];
				}
				System.out.println(Arrays.toString(args));
				TaskRunner.runTask(Task.loadTask(cmdArray[0]), args);
			}
			else
				TaskRunner.runTask(Task.loadTask(command));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

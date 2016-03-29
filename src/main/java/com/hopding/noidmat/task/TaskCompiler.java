package com.hopding.noidmat.task;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.compiler.batch.BatchCompiler;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * {@code TaskCompiler} is meant to be run as a daemon thread and periodically check the task source file directory for
 * changes to the task sources, and recompile them.
 */
@SideOnly(Side.CLIENT)
public class TaskCompiler extends Thread {
	public static final File PATH_TO_MDK = new File("C:/Users/Andrew/NoidMAT-1.8.9");
	public static final File TASK_HOME_DIR = new File(System.getProperty("user.home") + "/NoidMAT-Tasks");
	public static final File TASK_SRC_DIR = new File(TASK_HOME_DIR + "/src");
	public static final File TASK_CLASS_DIR = new File(TASK_HOME_DIR + "/bin");
	public static final File TASK_COMPILER_OUT_DIR = new File(TASK_HOME_DIR + "/ecj-out");
	private static File NoidMAT_JAR;
	private static PrintWriter logPw;
	private static String logPwFilePath = TASK_HOME_DIR + "/TaskCompiler-daemon-log.txt";
	private static boolean firstLogPwInit = true;
	private static Map<String, Integer> timesSrcBeenCompiled = new HashMap<String, Integer>();
	public static final String[] DEFAULT_TASK_SOURCES = {
			"BuildWall.java", "BreakDownWall.java", "TaskTemplate.java",
			"Mine.java"
	};

	//args[0] - absolute path to NoidMAT jar file
	//args[1] - absolute path to dump directory for compiled class files
	//args[2] - absolute path to directory put the jdt compiler output files
	//args[3] - absolute path to java source file
	public static void main(String[] args) {
		try {
			BatchCompiler.compile(
					"-classpath \"" + args[0] + "\" "
							+ "-d " + args[1] + " -source 1.6 "
							+ "-log " + args[2] + " -progress "
							+ args[3],
					new PrintWriter(System.out),
					new PrintWriter(System.err), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void compile(File noidmatJar, File classFileDumpDir, File ecjOutputDir, File javaSourceFile) {
		String srcFileName = javaSourceFile.getAbsolutePath().substring(
				(TASK_SRC_DIR + File.separator).length(),
				javaSourceFile.getAbsolutePath().indexOf("."));
		if(timesSrcBeenCompiled.get(srcFileName) == null) {
			timesSrcBeenCompiled.put(srcFileName, 0);
		} else {
			int timesCompiled = timesSrcBeenCompiled.get(srcFileName);
			timesSrcBeenCompiled.put(srcFileName, ++timesCompiled);
		}
		String logFileName = srcFileName + timesSrcBeenCompiled.get(srcFileName) + ".txt";
		File logFile = new File(TASK_COMPILER_OUT_DIR + File.separator + logFileName);
		try {
			logFile.createNewFile();
			ProcessBuilder processBuilder = new ProcessBuilder(
					"java", "-cp", noidmatJar.getAbsolutePath(), TaskCompiler.class.getName(),
					noidmatJar.getAbsolutePath(),
					classFileDumpDir.getAbsolutePath(),
					logFile.getAbsolutePath(),
					javaSourceFile.getAbsolutePath()
			);
			logMsg("");
			logMsg("=================================================================================================");
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			logMsg(dateFormat.format(new Date()));
			logMsg("TaskCompiler.compile() was invoked:");
			logMsg(processBuilder.command().toString());
			closeLogFile();
			processBuilder.redirectOutput(ProcessBuilder.Redirect.appendTo(new File(logPwFilePath)));
			processBuilder.redirectErrorStream(true);
			Process process = processBuilder.start();
			process.waitFor();
			logMsg("=================================================================================================");
		} catch (IOException e) {
			e.printStackTrace(getLogPw());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

		@Override
		public void run() {
			final HashMap<File, Long> taskFilesModif = new HashMap<File, Long>();
			TaskCompiler.TASK_HOME_DIR.mkdir();
			TaskCompiler.TASK_SRC_DIR.mkdir();
			TaskCompiler.TASK_CLASS_DIR.mkdir();
			TaskCompiler.TASK_COMPILER_OUT_DIR.mkdir();
			try {
				FileUtils.cleanDirectory(TASK_CLASS_DIR);
				FileUtils.cleanDirectory(TASK_COMPILER_OUT_DIR);
			} catch (IOException e) {
				e.printStackTrace();
			}

			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			logMsg("Date Format: yyyy/MM/dd HH:mm:ss");
			logMsg(dateFormat.format(new Date()));
			logMsg("==========Starting initialization to prepare for compilation of Task sources.==========");

			try {
				logMsg("Getting location of current class (TaskCompiler) as URL:");
				URL noidMatJarURL = TaskCompiler.class.getProtectionDomain().getCodeSource().getLocation().toURI().toURL();
				logMsg(noidMatJarURL.toString());
				logMsg("Converting class location URL to NoidMAT jar File:");
				if(inDevEnv()) {
					logMsg("inDevEnv() returned true, must be in development environment. Will use hard coded jar location instead:");
					NoidMAT_JAR = new File(PATH_TO_MDK + "/build/libs/NoidMAT-0.0.0.jar");
				}
				else {
				/*
				Because noidMatJarURL.toString will return the path to the current class inside the jar file,
				we need to trim it so that it points just to the jar (not the packages inside of it. We also
				need to trim off the "jar:file:" that is added onto the front of the path in order to create
				a valid file path from the URL, so we use substring() to get this, for example:
					"/C:/Users/Andrew/AppData/Roaming/.minecraft/mods/NoidMAT-0.0.0.jar"
				from this:
					"jar:file:/C:/Users/Andrew/AppData/Roaming/.minecraft/mods/NoidMAT-0.0.0.jar!/com/hopding/noidmat/task/TaskCompiler.class"
				 */
					NoidMAT_JAR = new File(noidMatJarURL.toString().substring(
							"jar:file:".length(),
							noidMatJarURL.toString().lastIndexOf("!")
					).replace("%20", " "));
				}
				logMsg(NoidMAT_JAR.getAbsolutePath());

				logMsg("Ensuring all default tasks are in TASK_SRC_DIR...");
				if(inDevEnv()) {
					File defaultTaskFolder = new File(PATH_TO_MDK + "/src/main/resources/default-tasks");
					for(String defaultTask : DEFAULT_TASK_SOURCES) {
						File defaultTaskFile = new File(TASK_SRC_DIR + File.separator + defaultTask);
						FileUtils.deleteQuietly(defaultTaskFile);
						FileUtils.copyFile(new File(defaultTaskFolder + File.separator + defaultTask), defaultTaskFile);
					}
				}
				else {
					for(String defaultTask : DEFAULT_TASK_SOURCES) {
						File defaultTaskFile = new File(TASK_SRC_DIR + File.separator + defaultTask);
						FileUtils.deleteQuietly(defaultTaskFile);
						BufferedReader buffReader = new BufferedReader(new InputStreamReader(
								getClass().getResourceAsStream("/default-tasks/" + defaultTask)));
						PrintWriter writer = new PrintWriter(defaultTaskFile);
						String line;
						while((line = buffReader.readLine()) != null) {
							writer.println(line);
						}
						writer.close();
						buffReader.close();
					}
				}
				logMsg("Done");
				logMsg("==========Completed initialization==========");
				closeLogFile();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			Runtime.getRuntime().addShutdownHook(new Thread() {
						@Override
						public void run() {
							try {
								FileUtils.cleanDirectory(TASK_CLASS_DIR);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});

			while (true) {
				File[] taskFiles = TASK_SRC_DIR.listFiles();
				for (File taskFile : taskFiles) {
					String taskFileName = taskFile.getName();
					if(!taskFileName.substring(taskFileName.lastIndexOf("."), taskFileName.length()).equals(".java"))
						continue;
					if (taskFilesModif.get(taskFile) == null || taskFilesModif.get(taskFile) != taskFile.lastModified()) {
						TaskCompiler.compile(NoidMAT_JAR, TASK_CLASS_DIR, TASK_COMPILER_OUT_DIR, taskFile);
						taskFilesModif.put(taskFile, taskFile.lastModified());
					}
				}
				Thread.yield();
			}
		}

	private static boolean inDevEnv() {
		//Assume if the mod jar file is included somewhere in the current class' URL that we are running from the normal
		//mod jar file in the /mods directory of the minecraft folder. Otherwise, we'll assume we are running from a
		//Forge development environment.
		URL noidMatJarURL = null;
		try {
			noidMatJarURL = TaskCompiler.class.getProtectionDomain().getCodeSource().getLocation().toURI().toURL();
			if(noidMatJarURL.toString().contains(".jar"))
				return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private static void logMsg(String msg) {
		if(logPw == null)
			try {
				if(firstLogPwInit) {
					FileUtils.deleteQuietly(new File(logPwFilePath));
					logPw = new PrintWriter(new FileWriter(logPwFilePath, true), true);
					firstLogPwInit = false;
				}
				else
					logPw = new PrintWriter(new FileWriter(logPwFilePath, true), true);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		logPw.append(msg + "\n");
	}

	private static PrintWriter getLogPw() {
		if(logPw == null)
			try {
				logPw = new PrintWriter(new FileWriter(logPwFilePath, true), true);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return logPw;
	}

	private static void closeLogFile() {
		if(logPw != null) {
			logPw.flush();
			logPw.close();
			logPw = null;
		}
	}
}

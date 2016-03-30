# NoidMAT-1.8.9
A Minecraft Forge Mod For Automating Minecraft

NoidMAT (Noid Minecraft Automation Tool) is an extensible Minecraft Forge mod designed to automate as much as possible of Minecraft's survival mode. NoidMAT operates on both singleplayer and multiplayer, even on completely vanilla multiplayer servers. Instructions on its installation and use can be found in the README for the current [release](https://github.com/Hopding/NoidMAT-1.8.9/releases/).

By default, NoidMAT is currently able to:
 * Build walls
 * Break down walls
 * Mine out areas
 * Walk, swim, and jump for long periods of time without requiring the user to hold the W, A, S, D keys or the spacebar.

Using NoidMAT is as simple as pressing the "X" key on your keyboard and entering a command, e.g.
```
BuildWall 5:5
```

Users can make their own tasks to be automated by NoidMAT by utilizing the NoidMAT API and writing some very simple java code. Details can be found in the README for the current [release](https://github.com/Hopding/NoidMAT-1.8.9/releases/).

Additional information can be found on NoidMAT-1.8.9's [wiki](https://github.com/Hopding/NoidMAT-1.8.9/wiki).

#Clone and Setup this Repo (for Developers)
First, you must clone this repo to a local repo on your machine:
```
git clone https://github.com/Hopding/NoidMAT-1.8.9
```
Then you will need to download the Minecraft Forge [mdk](http://files.minecraftforge.net/) for 1.8.9. Download and unzip the mdk to a suitable location on your machine. Delete the /src subdirectory and then copy all the rest of the files and folders from the mdk dir into the repo you cloned previously for this project.
Open up a terminal in the cloned repo dir and enter the following command:
```
For Windows Command Prompt:
	gradlew setupDecompWorkspace --refresh-dependencies 

For Linux/Mac OS and Windows PowerShell: 
	./gradlew setupDecompWorkspace --refresh-dependencies 
If this doesn't work, type chmod +x gradlew and then retry the above command.
```
Next you'll need to set up the project for your IDE, if you choose to use one. Open the project in your IDE of choice (for Intellij, do File>Open, find and expand your repo clone, select the build.gradle file, click OK, click OK again).
Now return to your terminal and enter the follow commands:
```
For Eclipse on Windows:
	gradlew eclipse 

For Eclipse on Linux/Mac OS: 
	./gradlew eclipse 

For IntelliJ IDEA on Windows: 
	gradlew genIntellijRuns

For IntellijIDEA on Linux/Mac OS: 
	./gradlew genIntellijruns 
```
Once the command completes, return to your IDE and reload the project (IntelliJ will likely prompt you to do so automatically). You need to set up the run configurations for the project to launch. In Intellij, go to Run>Edit Configurations>Application>Minecraft Client and press OK.
Now you'll need to navigate to the following class:
```
/src/main/java/com/hopding/noidmat/task/TaskCompiler.java
```
and change this line of code, towards the top of the class (line 24 as of this writing):
```
	public static final File PATH_TO_MDK = new File("C:/Users/Andrew/NoidMAT-1.8.9");
```
to reflect the path to your clone of this repo.
Now return to your terminal and enter the following command:
```
gradle build
```
You should now be ready to run the project! Click the Run button in your IDE. The Minecraft client should launch. To ensure everything is working properly, login to a world and test the various tasks (BuildWall, Mine, etc...).

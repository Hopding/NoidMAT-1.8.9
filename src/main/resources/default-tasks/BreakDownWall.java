import com.hopding.noidmat.exceptions.FailedToCompleteTaskException;
import com.hopding.noidmat.task.Task;
import com.hopding.noidmat.Player;

/*
Breaks down a wall using the item in the second hot bar slot

Example command:
	BreakDownWall 5:5
...will break down a wall 5 blocks long and 5 blocks high in the direction the
player is facing at the time the command is run.
 */
public class BreakDownWall extends Task {  
	@Override
	public void run(Player player, String... args) throws FailedToCompleteTaskException {
		try {
			int WALL_HEIGHT = 0;
			int WALL_LENGTH = 0;
			if(args == null)
				throw new FailedToCompleteTaskException("Length and height arguments are empty, cannot proceed.");
			else if(args[0].contains(":")) {
				String[] parsedArgs = args[0].split(":");
				WALL_LENGTH = Integer.parseInt(parsedArgs[0]);
				WALL_HEIGHT = Integer.parseInt(parsedArgs[1]);
			}
			else {
				int index = 0;
				for(String arg : args) {
					if(arg.equals("-l") || arg.equals("--length"))
						WALL_LENGTH = Integer.parseInt(args[index + 1]);
					if(arg.equals("-h") || arg.equals("--height"))
						WALL_HEIGHT = Integer.parseInt(args[index + 1]);
					index++;
				}
			}
			
			player.setKeyboardLock(true);
			player.setMousePosLock(true);
			player.setMouseButtonsLock(true);
			float originalYaw = player.getYaw();
			player.selectHotbarSlot(2);
			for (int h = 0; h < WALL_HEIGHT; h++) {
				player.setPitch(Player.VIEW_BELOW);
				player.breakBlock();
				for (int l = 0; l < WALL_LENGTH - 1; l++) {
					player.lookAtFootLevelBlock();
					player.breakBlock();
					player.walkForward(1);
				}
				player.aboutFace();
			}
			player.setYaw(originalYaw);
			player.setKeyboardLock(false);
			player.setMousePosLock(false);
			player.setMouseButtonsLock(false);
		} catch (Exception e) {
			throw new FailedToCompleteTaskException(e.getMessage());
		}
	}	
}

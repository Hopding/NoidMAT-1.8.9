import com.hopding.noidmat.exceptions.FailedToCompleteTaskException;
import com.hopding.noidmat.task.Task;
import com.hopding.noidmat.Player;

/*
Builds a wall using the blocks in the player's first hot bar slot.
If the blocks in the first hotbar slot are used up entirely, then BuildWall
will find another stack of the same type of block in the hot bar (favor is
given to stacks farther to the left).

Example command:
	BuildWall 5:5
...will build a wall 5 blocks long and 5 blocks high in the direction the
player is facing at the time the command is run.
 */
public class BuildWall extends Task {
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
			player.straightenYaw();
			player.aboutFace();
			player.walkBackward(1);
			player.selectHotbarSlot(1);
			for (int h = 0; h < WALL_HEIGHT; h++) {
				for (int l = 0; l < WALL_LENGTH - 2; l++) {
					if(!player.lookAtFootLevelBlock()) {
						player.pause(250);
						player.lookAtGroundLevelBlock();
					}
					player.placeBlock(true);
					player.walkBackward(1);
				}
				if(!player.lookAtFootLevelBlock()) {
					player.pause(250);
					player.lookAtGroundLevelBlock();
				}
				player.placeBlock(true);
				player.placeBlockBelow(true);
				player.aboutFace();
				player.walkBackward(1);
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

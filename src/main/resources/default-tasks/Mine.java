import com.hopding.noidmat.exceptions.FailedToCompleteTaskException;
import com.hopding.noidmat.task.Task;
import com.hopding.noidmat.Player;

/*
Mines out an area of the specified length and width, with a height of 2.
Uses the tool in the first hot bar slot.
Uses the second hot bar slot for torches.
Torches, if they are available, are placed every fourth block, every third row.

Example command:
	Mine 5:5
...will mine an area 5 blocks long, 5 blocks wide, and 2 blocks high in the direction the
player is facing at the time the command is run.
 */
public class Mine extends Task {
    @Override
    public void run(Player player, String... args) throws FailedToCompleteTaskException {
        try {
            int MINE_WIDTH = 0;
            int MINE_LENGTH = 0;
            if(args == null)
                throw new FailedToCompleteTaskException("Width and length arguments are empty, cannot proceed.");
            else if(args[0].contains(":")) {
                String[] parsedArgs = args[0].split(":");
                MINE_LENGTH = Integer.parseInt(parsedArgs[0]);
                MINE_WIDTH = Integer.parseInt(parsedArgs[1]);
            }
            else {
                int index = 0;
                for(String arg : args) {
                    if(arg.equals("-l") || arg.equals("--length"))
                        MINE_LENGTH = Integer.parseInt(args[index + 1]);
                    if(arg.equals("-w") || arg.equals("--width"))
                        MINE_WIDTH = Integer.parseInt(args[index + 1]);
                    index++;
                }
            }

            player.setKeyboardLock(true);
            player.setMousePosLock(true);
            player.setMouseButtonsLock(true);
            float originalYaw = player.getYaw();
            player.straightenYaw();
            player.selectHotbarSlot(1);
            player.setPitch(Player.VIEW_CENTER);
            player.breakBlock();
            player.lookAtFootLevelBlock();
            player.breakBlock();
            player.walkForward(1);
            for (int w = 0; w < MINE_WIDTH; w++) {
                for (int l = 0; l < MINE_LENGTH-1; l++) {
                    if(w%3 == 0 && l%5 == 0) {
                        int hbs = player.getHotbarSlot();
                        player.selectHotbarSlot(2);
                        player.setPitch(Player.VIEW_BELOW);
                        player.placeBlock();
                        player.selectHotbarSlot(hbs);
                    }
                    player.setPitch(Player.VIEW_CENTER);
                    player.pause(100);
                    player.breakBlock();
                    player.lookAtFootLevelBlock();
                    player.breakBlock();
                    player.walkForward(1);
                }
                if(w%3 == 0) {
                    int hbs = player.getHotbarSlot();
                    player.selectHotbarSlot(2);
                    player.setPitch(Player.VIEW_BELOW);
                    player.placeBlock(true);
                    player.selectHotbarSlot(hbs);
                }
                if(w != MINE_WIDTH-1) {
                    if(w%2 == 0)
                        player.turnRight();
                    else
                        player.turnLeft();
                    player.setPitch(Player.VIEW_CENTER);
                    player.pause(100);
                    player.breakBlock();
                    player.lookAtFootLevelBlock();
                    player.breakBlock();
                    player.walkForward(1);
                    if(w%2 == 0)
                        player.turnRight();
                    else
                        player.turnLeft();
                }
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

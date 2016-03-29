import com.hopding.noidmat.exceptions.FailedToCompleteTaskException;
import com.hopding.noidmat.task.Task;
import com.hopding.noidmat.Player;

//CHANGE THE NAME OF THIS CLASS BASED ON WHAT IT DOES.
//IT IS IMPORTANT THAT THE FILE BE SAVED AS "NameOfTheClass.java".
//SO IN THIS CASE, WE SAVE THE FILE AS "TaskTemplate.java".

//THE USER TYPES IN THE NAME OF THE CLASS TO RUN THIS TASK.
//SO IN THIS CASE, THE USER MUST TYPE "TaskTemplate" INTO THE
//NoidMAT CONSOLE TO RUN THIS TASK.
public class TaskTemplate extends Task {
	@Override
	public void run(Player player, String... args) throws FailedToCompleteTaskException {
		try {
			
//======================================================================//
//PUT THE CODE YOU WANT TO RUN IN HERE. USE THE PASSED IN Player INSTANCE
//TO CALL METHODS AND CONTROL THE USER'S PLAYER.
//FOR EXAMPLE:
		player.walkForward(1);
//WILL CAUSE THE PLAYER TO WALK FORWARD ONE BLOCK WHEN THE USER TYPES THE 
//NAME OF THIS CLASS (TaskTemplate, in this case) INTO THE NoidMAT CONSOLE.
//======================================================================//

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

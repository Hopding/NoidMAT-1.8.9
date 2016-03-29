package com.hopding.noidmat;

import com.hopding.noidmat.exceptions.OutOfRangeException;
import com.hopding.noidmat.input.KeyboardLocker;
import com.hopding.noidmat.input.MouseLocker;
import com.hopding.noidmat.task.Task;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * The {@code Player} class represents a user's Minecraft player.
 * It complies with the Singleton design pattern, meaning there is only one instance
 * per jvm, and therefore per running Minecraft instance.
 * <p>
 * A reference to the single {@code Player} instance can be obtained with the following code:
 * <pre>{@code
 * Player player = Player.getPlayer();
 * }</pre>
 * The user's Minecraft player can be controlled with the {@code Player} class by calling
 * the various methods it contains.
 * It is important that these methods not be called within the Minecraft game threads.
 * They should be called inside a separate thread.
 * The recommended way of doing this is to extend the abstract class
 * {@link com.hopding.noidmat.task.Task Task} and implement its {@code run(Player, String...)}
 * method by calling methods on the passed in {@code Player} instance.
 * A reference to the {@code Task} subclass can then be passed into the
 * {@link com.hopding.noidmat.task.TaskRunner#runTask(Task, String...) runTask(Task, String...)} method to be run.
 * The user's Minecraft player will then respond to the methods called in the {@code Task} subclass's
 * {@code run(Player, String...)} method.
 *
 * @author Andrew Dillon
 */
@SideOnly(Side.CLIENT)
public class Player {

	/**Pitch value to look straight down*/
	public static final float			VIEW_BELOW	= 90;
	/**Pitch value to look straight ahead*/
	public static final float			VIEW_CENTER	= 0;
	/**Pitch value to look straight up*/
	public static final float			VIEW_ABOVE	= -90;

	private Minecraft			minecraft;
	private GameSettings		gameSettings;
	private EntityPlayerSP		player;
	private PlayerControllerMP	playerController;
	private KeyBinding					forwardKey;
	private KeyBinding					reverseKey;
	private KeyBinding					rightKey;
	private KeyBinding					leftKey;
	private KeyBinding					sneakKey;
	private KeyBinding					attackKey;
	private KeyBinding					useItemKey;
	private KeyBinding					jumpKey;
	private ActionMonitor		actionMonitor;
	private static Player		singletonPlayerInstance;

	private Player() {
		this.minecraft = Minecraft.getMinecraft();
		this.gameSettings = minecraft.gameSettings;
		this.player = minecraft.thePlayer;
		this.playerController = minecraft.playerController;
		forwardKey = gameSettings.keyBindForward;
		reverseKey = gameSettings.keyBindBack;
		rightKey = gameSettings.keyBindRight;
		leftKey = gameSettings.keyBindLeft;
		sneakKey = gameSettings.keyBindSneak;
		attackKey = gameSettings.keyBindAttack;
		useItemKey = gameSettings.keyBindUseItem;
		jumpKey = gameSettings.keyBindJump;
	}

	/**
	 * Gets the single instance of {@link com.hopding.noidmat.Player Player} for this jvm.
	 *
	 * @return the {@code Player} instance
     */
	public static synchronized Player getPlayer() {
		if (singletonPlayerInstance == null)
			singletonPlayerInstance = new Player();
		return singletonPlayerInstance;
	}

	/**
	 * Used by Forge, should NOT be called otherwise.
	 * Called on every tick of the client.
	 * Should <b>never</b> ever be called except by Forge.
     */
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onClientTick(TickEvent.ClientTickEvent event) {
		synchronized (this) {
			this.notifyAll();
		}
		if (player == null)
			reloadPlayer();
		if (actionMonitor != null)
			if (!actionMonitor.actionIsComplete())
				actionMonitor.update();
	}

	/**
	 * Sets the keyboard lock.
	 * When the keyboard lock is set to true, the user cannot use the keyboard to interact with the game.
	 * The only key the user can press is ESC, which will cause all mouse and keyboard locks to be set to false.
	 * It will also end whatever Task the player is currently running, if any.
	 * It is advised to lock the keyboard at the beginning of a Task, to prevent the user from disrupting the Task.
	 *
	 * @param lock a boolean specifying whether to lock or unlock keyboard input to the game
     */
	public void setKeyboardLock(boolean lock) {
		if(lock)
			KeyboardLocker.getKeyboardLocker().lockKeyboard();
		else
			KeyboardLocker.getKeyboardLocker().unlockKeyboard();
	}

	/**
	 * Returns the state of the keyboard lock, true if keyboard input is locked, false if it is not.
	 *
	 * @return a boolean indicating the state of the keyboard lock
     */
	public boolean isKeyboardLocked() {
		return KeyboardLocker.getKeyboardLocker().isKeyboardLocked();
	}

	/**
	 * Sets the mouse position lock.
	 * When the mouse position lock is set to true, the user cannot move the mouse to interact with the game.
	 * It is advised to lock the mouse position at the beginning of a Task, to prevent the user from disrupting the Task.
	 *
	 * @param lock a boolean specifying whether to lock or unlock mouse position input to the game
	 * @custom.bug The mouse lock can be temporarily subverted if the mouse is moved soon after the {@code Player's}
	 * pitch or yaw is changed.
     */
	public void setMousePosLock(boolean lock) {
		if(lock)
			MouseLocker.getMouseLocker().lockMousePos();
		else
			MouseLocker.getMouseLocker().unlockMousePos();
	}

	/**
	 * Returns the state of the mouse position lock, true if the mouse position is locked, false if it is not.
	 *
	 * @return a boolean indicating the state of the mouse position lock
     */
	public boolean isMousePosLocked() {
		return MouseLocker.getMouseLocker().isMousePosLocked();
	}

	/**
	 * Sets the mouse button lock.
	 * When the mouse button lock is set to true, the user cannot use the mouse buttons to interact with the game.
	 * It is advised to lock the mouse position at the beginning of a Task, to prevent the user from disrupting the Task.
	 *
	 * @param lock a boolean specifying whether to lock or unlock mouse button input to the game
     */
	public void setMouseButtonsLock(boolean lock) {
		if(lock)
			MouseLocker.getMouseLocker().lockMouseButtons();
		else
			MouseLocker.getMouseLocker().unlockMouseButtons();
	}

	/**
	 * Returns the state of the mouse button lock, true if the mouse buttons are locked, false if they are not.
	 *
	 * @return a boolean indicating the state of the mouse button lock.
     */
	public boolean areMouseButtonsLocked() {
		return MouseLocker.getMouseLocker().areMouseBtnsLocked();
	}

	/**
	 * Cancels the action currently being executed by the {@code Player} and sets all keys to not pressed.
	 * An action corresponds to a single method in the {@code Player} class.
	 * <p>
	 * If a Task is still being executed, calling {@code cancelAction()} will NOT stop it.
	 * Calling {@code cancelAction()} only stops a single action within the larger Task.
	 * To stop a Task, invoke {@link com.hopding.noidmat.task.TaskRunner#stopCurrentTask() TaskRunner.stopCurrentTask()}.
	 */
	public void cancelAction() {
		actionMonitor = null;
		KeyBinding.setKeyBindState(forwardKey.getKeyCode(), false);
		KeyBinding.setKeyBindState(reverseKey.getKeyCode(), false);
		KeyBinding.setKeyBindState(rightKey.getKeyCode(), false);
		KeyBinding.setKeyBindState(leftKey.getKeyCode(), false);
		KeyBinding.setKeyBindState(attackKey.getKeyCode(), false);
		KeyBinding.setKeyBindState(useItemKey.getKeyCode(), false);
	}

	/**
	 * Causes the player to look at the block at foot level just in front of the player, if one exists.
	 * If there is no block at foot level in front of the player, the method returns false and the
	 * player doesn't move.
	 *
	 * @return true if the action was successful, false if no such block exists
	 * @throws InterruptedException
     */
	public synchronized boolean lookAtFootLevelBlock() throws InterruptedException {
		straightenYaw();
		final BlockPos footLvlBlkPos;
		if (getYaw() == 0) {
			footLvlBlkPos = new BlockPos(player.posX, player.posY, player.posZ + 1);
		} else if (getYaw() == 90) {
			footLvlBlkPos = new BlockPos(player.posX - 1, player.posY, player.posZ);
		} else if (getYaw() == 180) {
			footLvlBlkPos = new BlockPos(player.posX, player.posY, player.posZ - 1);
		} else { // This gets executed only if getYaw() == 270
			footLvlBlkPos = new BlockPos(player.posX + 1, player.posY, player.posZ);
		}
		if(Utilities.getBlockName(Utilities.getBlock(footLvlBlkPos)).equals("minecraft:air"))
			return false;
		actionMonitor = new ActionMonitor() {
			public void update() {
				if(getFacingBlockPos().equals(footLvlBlkPos))
					setActionCompleted();
				else if(getFacingBlockPos().getY() > footLvlBlkPos.getY() && !Utilities.isLayerBlock(getFacingBlock())
						|| getYaw() == 0 && getFacingBlockPos().getZ() > footLvlBlkPos.getZ()
						|| getYaw() == 90 && getFacingBlockPos().getX() < footLvlBlkPos.getX()
						|| getYaw() == 180 && getFacingBlockPos().getZ() < footLvlBlkPos.getZ()
						|| getYaw() == 270 && getFacingBlockPos().getX() > footLvlBlkPos.getX())
					player.rotationPitch = getPitch() + 5;
				else
					player.rotationPitch = getPitch() - 5;
			}
		};
		synchronized (this) {
			while (!actionMonitor.actionIsComplete())
				this.wait();
		}
		return true;
	}

	/**
	 * Causes the player to look at the block at ground level just in front of the player, if one exists.
	 * If there is no block at ground level in front of the player, the method returns false and the
	 * player doesn't move.
	 *
	 * @return true if the action was successful, false if no such block exists
	 * @throws InterruptedException
	 * @custom.note The method is unlikely to return if it is called while a block exists that is at foot level directly
	 * in front of the player.
	 * It is advised to call {@link com.hopding.noidmat.Player#lookAtFootLevelBlock()} before calling
	 * {@code lookAtGroundLevelBlock()} to avoid this issue, e.g.:
	 * <pre>{@code
	 * //This code will break any blocks at foot level and
	 * //then look at the ground level block, if one exists
	 * if(player.lookAtFootLevelBlock())
	 * 	player.breakBlock();
	 * player.lookAtGroundLevelBlock();
	 * }</pre>
	 * or
	 * <pre>{@code
	 * //This code will cause the player to place a block at foot level
	 * //on top of the ground level block in front of the player, even
	 * //if there is snow or tallgrass in the way
	 * if(!player.lookAtFootLevelBlock())
	 * 	player.lookAtGroundLevelBlock();
	 * player.placeBlock();
	 * }</pre>
	 */
	public synchronized boolean lookAtGroundLevelBlock() throws InterruptedException {
		straightenYaw();
		final BlockPos groundLvlBlkPos;
		if (getYaw() == 0) {
			groundLvlBlkPos = new BlockPos(player.posX, player.posY - 1, player.posZ + 1);
		} else if (getYaw() == 90) {
			groundLvlBlkPos = new BlockPos(player.posX - 1, player.posY - 1, player.posZ);
		} else if (getYaw() == 180) {
			groundLvlBlkPos = new BlockPos(player.posX, player.posY - 1, player.posZ - 1);
		} else { // This gets executed only if getYaw() == 270
			groundLvlBlkPos = new BlockPos(player.posX + 1, player.posY - 1, player.posZ);
		}
		if(Utilities.getBlockName(Utilities.getBlock(groundLvlBlkPos)).equals("minecraft:air"))
			return false;

		actionMonitor = new ActionMonitor() {
			public void update() {
				if(getFacingBlockPos().equals(groundLvlBlkPos))
					setActionCompleted();
				else if(getFacingBlockPos().getY() > groundLvlBlkPos.getY() && !Utilities.isLayerBlock(getFacingBlock())
						|| getYaw() == 0 && getFacingBlockPos().getZ() > groundLvlBlkPos.getZ()
						|| getYaw() == 90 && getFacingBlockPos().getX() < groundLvlBlkPos.getX()
						|| getYaw() == 180 && getFacingBlockPos().getZ() < groundLvlBlkPos.getZ()
						|| getYaw() == 270 && getFacingBlockPos().getX() > groundLvlBlkPos.getX())
					player.rotationPitch = getPitch() + 5;
				else
					player.rotationPitch = getPitch() - 5;
			}
		};
		synchronized (this) {
			while (!actionMonitor.actionIsComplete())
				this.wait();
		}
		return true;
	}

	/**
	 * Causes the player to begin breaking whatever block is currently being looked at.
	 * Once the block is broken, the method will return.
	 * The player will use whatever tool is currently selected in the hot bar to break the
	 * block, regardless of whether or not that is the ideal tool for breaking the block (e.g. iron pick for a diamond
	 * block, a shovel for dirt, etc...).
	 *
	 * @throws InterruptedException
     */
	public synchronized void breakBlock() throws InterruptedException {
		final Block originalBlock = getFacingBlock();
		final BlockPos originalBlockPos = getFacingBlockPos();
		KeyBinding.setKeyBindState(attackKey.getKeyCode(), true);
		actionMonitor = new ActionMonitor() {
			public void update() {
				if (Utilities.getBlockName(Utilities.getBlock(originalBlockPos)).equals("minecraft:air")) {
					KeyBinding.setKeyBindState(attackKey.getKeyCode(), false);
					setActionCompleted();
				}
			}
		};
		synchronized (this) {
			while (!actionMonitor.actionIsComplete())
				this.wait();
		}
		pause(200);
	}

	/**
	 * Causes the player to attempt placing whatever (block, item, etc...) is currently selected in the hot bar.
	 * Once the player has attempted to place the item/block, the method will return.
	 * <p>
	 * The {@code autoScroll} param indicates whether or not to autoscroll.
	 * If autoScroll is set to true, and {@code placeBlock()} is going to place the last item in the
	 * currently selected stack, then after placing that item, the currently selected hotbar slot will search the hot bar
	 * from left to right for another stack of the same item type.
	 * If no stacks of the same type are found, then the selected hot bar slot will not change.
	 * Otherwise, the player's selected hotbar slot will change to the newly found item stack.
	 *
	 * @param autoScroll a boolean indicating whether this invocation of {@code placeBlock()} should autoscroll
	 * @throws InterruptedException
     */
	public synchronized void placeBlock(boolean autoScroll) throws InterruptedException {
		if(getNumItemsLeft() == 0)
			return; //Player is holding empty slot, so just return
		String curItemName = player.inventory.getCurrentItem().getItem().getRegistryName();
		KeyBinding.setKeyBindState(useItemKey.getKeyCode(), true);
		pause(100);
		KeyBinding.setKeyBindState(useItemKey.getKeyCode(), false);
		if(autoScroll && getNumItemsLeft() == 0) {
			for(int i = 36; i < 45; i++) {
				ItemStack itemStack = player.inventoryContainer.getInventory().get(i);
					try {
						if(itemStack.getItem().getRegistryName().equals(curItemName)) {
							selectHotbarSlot(i - 35);
							return;
						}
					} catch (OutOfRangeException e) {
						e.printStackTrace();
					} catch (NullPointerException e1) {
						continue;
					}
			}
		}
	}

	/**
	 * Causes the player to attempt placing whatever (block, item, etc...) is currently selected in the hot bar.
	 * Once the player has attempted to place the item/block, the method will return.
	 */
	public synchronized void placeBlock() throws InterruptedException {
		placeBlock(false);
	}

	/**
	 * Causes the player to jump and attempt placing the item currently selected in the hot bar directly below.
	 * When {@code placeBlockBelow()} returns, the player will still be looking at the same place that
	 * was being viewed immediately prior to calling {@code placeBlockBelow()}.
	 * <p>
	 * The {@code autoScroll} param indicates whether or not to autoscroll.
	 * If autoScroll is set to true, and {@code placeBlockBelow()} is going to place the last item in the
	 * currently selected stack, then after placing that item, the currently selected hotbar slot will search the hot bar
	 * from left to right for another stack of the same item type.
	 * If no stacks of the same type are found, then the selected hot bar slot will not change.
	 * Otherwise, the player's selected hotbar slot will change to the newly found item stack.
	 *
	 * @param autoScroll a boolean indicating whether this invocation of {@code placeBlockBelow()} should autoscroll
	 * @throws InterruptedException
	 */
	public synchronized void placeBlockBelow(boolean autoScroll) throws InterruptedException {
		if(getNumItemsLeft() == 0)
			return; //Player is holding empty slot, so just return
		String curItemName = player.inventory.getCurrentItem().getItem().getRegistryName();
		final float originalPitch = getPitch();
		setPitch(VIEW_BELOW);
		KeyBinding.setKeyBindState(useItemKey.getKeyCode(), true);
		KeyBinding.setKeyBindState(jumpKey.getKeyCode(), true);
		pause(300);
		KeyBinding.setKeyBindState(jumpKey.getKeyCode(), false);
		KeyBinding.setKeyBindState(useItemKey.getKeyCode(), false);
		setPitch(originalPitch);
		if(autoScroll && getNumItemsLeft() == 0) {
			for(int i = 36; i < 45; i++) {
				ItemStack itemStack = player.inventoryContainer.getInventory().get(i);
				try {
					if(itemStack.getItem().getRegistryName().equals(curItemName)) {
						selectHotbarSlot(i - 35);
						return;
					}
				} catch (OutOfRangeException e) {
					e.printStackTrace();
				} catch (NullPointerException e1) {
					continue;
				}
			}
		}
	}

	/**
	 * Causes the player to jump and attempt placing the item currently selected in the hot bar directly below.
	 * When {@code placeBlockBelow()} returns, the player will still be looking at the same place that
	 * was being viewed immediately prior to calling {@code placeBlockBelow()}.
	 */
	public synchronized void placeBlockBelow() throws InterruptedException {
		placeBlockBelow(false);
	}

	/**
	 * Changes the selected hot bar slot (1-9).
	 * The {@code slotNum} arg specifies the slot to be selected.
	 * 1 will select the slot farthest to the left.
	 * Successively larger values will select slots farther to the right, 9 being the far right slot.
	 *
	 * @param slotNum an int specifying the slot to be selected
	 * @throws OutOfRangeException if the {@code slotNum} arg value is out of the range 1-9
     */
	public void selectHotbarSlot(int slotNum) throws OutOfRangeException {
		if (slotNum > 9 || slotNum < 1)
			throw new OutOfRangeException("The slotNum arg: " + slotNum + " is not in the range of 1-9.");
		player.inventory.currentItem = slotNum - 1;
	}

	/**
	 * Gets the index of the hotbar slot that's currently selected.
	 * A return value of 1 indicates the slot farthest to the left is selected.
	 * Successively larger values indicate slots farther to the right, 9 being the far right slot.
	 *
	 * @return an int indicating the index of the currently selected hotbar slot.
	 */
	public int getHotbarSlot() {
		return player.inventory.currentItem + 1;
	}

	/**
	 * Gets the size of the item stack currently held by the player.
	 *
	 * @return an int indicating the number of items left in the stack held by the player.
     */
	public int getNumItemsLeft() {
		try {
			return player.inventory.getCurrentItem().stackSize;
		} catch (NullPointerException e) {
			return 0;
		}
	}

	/**
	 * Causes the player to swing the item currently selected in the hot bar one time.
	 * Calling {@code swing()} multiple times will not break items or blocks, it is usually
	 * only used to attack entities (mobs or players).
	 */
	public void swing() {
		player.swingItem();
	}

	/**
	 * Causes the player to walk forward the specified number of blocks.
	 * Before the player begins to move, the player's yaw will be adjusted so that the player
	 * is looking straight ahead (0, 90, 180, or 270 degrees).
	 * {@code walkForward(int)} does not compensate for potential obstacles in the player's path.
	 * If the player is knocked off course (e.g. by a skeleton's arrow), the method may fail to properly
	 * execute and/or return.
	 *
	 * @param walkDistance an int specifying the number of blocks the player should walk forward
	 * @throws InterruptedException
     */
	public synchronized void walkForward(int walkDistance) throws InterruptedException {
		straightenYaw();
		final int stopPosX;
		final int stopPosZ;
		final double stopPosXDeci;
		final double stopPosZDeci;
		if (getYaw() == 0) {
			stopPosZ = (int) player.posZ + walkDistance;
			if (stopPosZ > 0)
				stopPosZDeci = 0.3;
			else
				stopPosZDeci = -0.7;
			stopPosX = (int) player.posX;
			stopPosXDeci = 0;
		} else if (getYaw() == 90) {
			stopPosX = (int) player.posX - walkDistance;
			if (stopPosX > 0)
				stopPosXDeci = 0.7;
			else
				stopPosXDeci = -0.3;
			stopPosZ = (int) player.posZ;
			stopPosZDeci = 0;
		} else if (getYaw() == 180) {
			stopPosZ = (int) player.posZ - walkDistance;
			if (stopPosZ > 0)
				stopPosZDeci = 0.7;
			else
				stopPosZDeci = -0.3;
			stopPosX = (int) player.posX;
			stopPosXDeci = 0;
		} else { // This gets executed only if getYaw() == 270
			stopPosX = (int) player.posX + walkDistance;
			if (stopPosX > 0)
				stopPosXDeci = 0.3;
			else
				stopPosXDeci = -0.7;
			stopPosZ = (int) player.posZ;
			stopPosZDeci = 0;
		}
		KeyBinding.setKeyBindState(forwardKey.getKeyCode(), true);
		actionMonitor = new ActionMonitor() {
			public void update() {
				if (getYaw() == 90 && player.posX < stopPosX + stopPosXDeci
						|| getYaw() == 270 && player.posX > stopPosX + stopPosXDeci
						|| getYaw() == 0 && player.posZ > stopPosZ + stopPosZDeci
						|| getYaw() == 180 && player.posZ < stopPosZ + stopPosZDeci) {
					KeyBinding.setKeyBindState(forwardKey.getKeyCode(), false);
					setActionCompleted();
				}
			}
		};
		synchronized (this) {
			while (!actionMonitor.actionIsComplete())
				this.wait();
		}
	}

	/**
	 * Causes the player to walk backwards the specified number of blocks.
	 * Before the player begins to move, the player's yaw will be adjusted so that the player
	 * is looking straight ahead (0, 90, 180, or 270 degrees).
	 * {@code walkBackward(int)} does not compensate for potential obstacles in the player's path.
	 * If the player is knocked off course (e.g. by a skeleton's arrow), the method may fail to properly
	 * execute and/or return.
	 *
	 * @param walkDistance an int specifying the number of blocks the player should walk backward
	 * @throws InterruptedException
     */
	public synchronized void walkBackward(int walkDistance) throws InterruptedException {
		straightenYaw();
		final int stopPosX;
		final int stopPosZ;
		final double stopPosXDeci;
		final double stopPosZDeci;
		if (getYaw() == 0) {
			stopPosZ = (int) player.posZ - walkDistance;
			if (stopPosZ > 0)
				stopPosZDeci = 0.7;
			else
				stopPosZDeci = -0.3;
			stopPosX = (int) player.posX;
			stopPosXDeci = 0;
		} else if (getYaw() == 90) {
			stopPosX = (int) player.posX + walkDistance;
			if (stopPosX > 0)
				stopPosXDeci = 0.3;
			else
				stopPosXDeci = -0.7;
			stopPosZ = (int) player.posZ;
			stopPosZDeci = 0;
		} else if (getYaw() == 180) {
			stopPosZ = (int) player.posZ + walkDistance;
			if (stopPosZ > 0)
				stopPosZDeci = 0.3;
			else
				stopPosZDeci = -0.7;
			stopPosX = (int) player.posX;
			stopPosXDeci = 0;
		} else { // This gets executed only if getYaw() == 270
			stopPosX = (int) player.posX - walkDistance;
			if (stopPosX > 0)
				stopPosXDeci = 0.7;
			else
				stopPosXDeci = -0.3;
			stopPosZ = (int) player.posZ;
			stopPosZDeci = 0;
		}
		KeyBinding.setKeyBindState(reverseKey.getKeyCode(), true);
		actionMonitor = new ActionMonitor() {
			public void update() {
				if (getYaw() == 90 && player.posX > stopPosX + stopPosXDeci
						|| getYaw() == 270 && player.posX < stopPosX + stopPosXDeci
						|| getYaw() == 0 && player.posZ < stopPosZ + stopPosZDeci
						|| getYaw() == 180 && player.posZ > stopPosZ + stopPosZDeci) {
					KeyBinding.setKeyBindState(reverseKey.getKeyCode(), false);
					setActionCompleted();
				}
			}
		};
		synchronized (this) {
			while (!actionMonitor.actionIsComplete())
				this.wait();
		}
	}

	private synchronized void centerXOnCurrentBlock() throws InterruptedException {
		// player.posX = (int) player.posX + 0.5;
		final float originalYaw = getYaw();
		setYaw(0);
		double startXPos = player.posX;
		double startXPosFrac = Math.abs(startXPos - ((int) startXPos)); //Extract the fraction part of the current posX
		double endXPos = 0.5 - startXPosFrac;
		KeyBinding.setKeyBindState(sneakKey.getKeyCode(), true);
		if (endXPos > 0)
			KeyBinding.setKeyBindState(rightKey.getKeyCode(), true);
		else if (endXPos < 0)
			KeyBinding.setKeyBindState(leftKey.getKeyCode(), true);
		actionMonitor = new ActionMonitor() {
			public void update() {
				double curXPosFrac = Math.abs(player.posX - (int) player.posX);
				if (curXPosFrac > 0.5 && curXPosFrac < 0.575) {
					KeyBinding.setKeyBindState(rightKey.getKeyCode(), false);
					KeyBinding.setKeyBindState(leftKey.getKeyCode(), false);
					KeyBinding.setKeyBindState(sneakKey.getKeyCode(), false);
					player.rotationYaw = originalYaw;
					setActionCompleted();
				}
			}
		};
		synchronized (this) {
			while (!actionMonitor.actionIsComplete())
				this.wait();
		}
	}

	private synchronized void centerZOnCurrentBlock() throws InterruptedException {
		// player.posZ = (int) player.posZ + 0.5;
		final float originalYaw = getYaw();
		setYaw(0);
		double startZPos = player.posZ;
		double startZPosFrac = Math.abs(startZPos - ((int) startZPos)); //Extract the fraction part of the current posZ
		double endZPos = 0.5 - startZPosFrac;
		KeyBinding.setKeyBindState(sneakKey.getKeyCode(), true);
		if (endZPos > 0)
			KeyBinding.setKeyBindState(forwardKey.getKeyCode(), true);
		else if (endZPos < 0)
			KeyBinding.setKeyBindState(reverseKey.getKeyCode(), true);
		actionMonitor = new ActionMonitor() {
			public void update() {
				double curZPosFrac = Math.abs(player.posZ - (int) player.posZ);
				if (curZPosFrac > 0.5 && curZPosFrac < 0.575) {
					KeyBinding.setKeyBindState(forwardKey.getKeyCode(), false);
					KeyBinding.setKeyBindState(reverseKey.getKeyCode(), false);
					KeyBinding.setKeyBindState(sneakKey.getKeyCode(), false);
					player.rotationYaw = originalYaw;
					setActionCompleted();
				}
			}
		};
		synchronized (this) {
			while (!actionMonitor.actionIsComplete())
				this.wait();
		}
	}

	/**
	 * Sets the player's yaw - the direction the player is facing left and right.
	 *
	 * @param degree a float specifying the desired yaw
     */
	public void setYaw(float degree) {
		player.rotationYaw = (float)degree;
	}

	/**
	 * Gets the current yaw of the player - the direction the player is facing left and right.
	 * The float value returned by {@code getYaw()} will always be within the range 0-360.
	 *
	 * @return the player's current yaw as a float
     */
	public float getYaw() {
		// The player's yaw does not reset to 0 if it reaches 361, nor does it
		// reset to 360 if it falls below 0.
		// To compensate for this, we'll add/subtract 360 from the yaw until it
		// is within the range of 0 to 360.
		float yaw = player.rotationYaw;
		if (yaw < 0)
			while (yaw < 0) {
				yaw += 360;
			}
		else if (yaw > 360)
			while (yaw > 360) {
				yaw -= 360;
			}
		return yaw;
	}

	/**
	 * Sets the player's pitch - the angle the player is facing up and down.
	 * <blockquote>
	 *     A {@code degree} arg value of 90 will cause the player to look straight down.<br>
	 * 	   A {@code degree} arg value of -90 will cause the player to look straight up.<br>
	 *     A {@code degree} arg value of 0 will cause the player to look straight ahead.<br>
	 * </blockquote>
	 * Any value is valid for the {@code degree} arg, but if the value if not in the range
	 * -90 to 90, then it will be clipped such that it is within that range.
	 * Values < -90 will be regarded as -90.
	 * Values > 90 will be regarded as 90.
	 *
	 * @param degree a float value specifying the desired pitch
	 * @throws InterruptedException
     */
	public synchronized void setPitch(float degree) throws InterruptedException {
		if(degree > 90)
			degree = 90;
		if(degree < -90)
			degree = -90;
		player.rotationPitch = (float)degree;
		pause(100);
	}

	/**
	 * Gets the player's current pitch - the angle the player is facing up and down.
	 * The pitch value will always be within the range -90 to 90.
	 *
	 * @return the player's current pitch as a float
     */
	public float getPitch() {
		return player.rotationPitch;
	}

	/**
	 * Straightens out the player's yaw.
	 * Upon returning, the player's yaw will be one of four different values, depending
	 * on what the player's yaw was prior to calling {@code straightenYaw()}:
	 * <blockquote>
	 *     0, if the player's yaw was in the range 315-360.<br>
	 *     90, if the player's yaw was in the range 45-135.<br>
	 *     180, if the player's yaw was in the range 135-225.<br>
	 *     270, if the player's yaw was in the range 225-315.
	 * </blockquote>
	 *
	 * @throws InterruptedException
     */
	public void straightenYaw() throws InterruptedException {
		if (getYaw() >= 315 && getYaw() <= 360 || getYaw() >= 0 && getYaw() < 45)
			setYaw(0);
		else if (getYaw() >= 45 && getYaw() < 135)
			setYaw(90);
		else if (getYaw() >= 135 && getYaw() < 225)
			setYaw(180);
		else if (getYaw() >= 225 && getYaw() < 315)
			setYaw(270);
	}

	/**
	 * Causes the player to turn around, increasing the player's yaw by 180.
	 */
	public void aboutFace() {
		setYaw(getYaw() + 180);
	}

	/**
	 * Causes the player to turn right, increasing the player's yaw by 90.
	 */
	public void turnRight() {
		setYaw(getYaw() + 90);
	}

	/**
	 * Causes the player to turn left, decreasing the player's yaw by 90.
	 */
	public void turnLeft() {
		setYaw(getYaw() - 90);
	}

	/**
	 * Detects whether or there is a block at ground level directly in front of the player.
	 * If there is, then {@code isInFrontOfPit()} returns false.
	 * If there isn't, then {@code isInFrontOfPit()} returns true.
	 *
	 * @return boolean indicating the presence of a block at ground level in front of the player
	 * @throws InterruptedException
     */
	public boolean isInFrontOfPit() throws InterruptedException {
		straightenYaw();
		final BlockPos groundLvlBlkPos;
		if (getYaw() == 0) {
			groundLvlBlkPos = new BlockPos(player.posX, player.posY - 1, player.posZ + 1);
		} else if (getYaw() == 90) {
			groundLvlBlkPos = new BlockPos(player.posX - 1, player.posY - 1, player.posZ);
		} else if (getYaw() == 180) {
			groundLvlBlkPos = new BlockPos(player.posX, player.posY - 1, player.posZ - 1);
		} else { // This gets executed only if getYaw() == 270
			groundLvlBlkPos = new BlockPos(player.posX + 1, player.posY - 1, player.posZ);
		}
		if(Utilities.getBlockName(Utilities.getBlock(groundLvlBlkPos)).equals("minecraft:air"))
			return true;
		else
			return false;
	}

	/**
	 * Causes the player to pause for the specified number of milliseconds.
	 *
	 * @param millis an int specifying the number of milliseconds to pause for
	 * @throws InterruptedException
	 */
	public void pause(int millis) throws InterruptedException {
		long startTime = System.currentTimeMillis();
		synchronized (this) {
			while (System.currentTimeMillis() - startTime < millis)
				this.wait();
		}
	}

	private void reloadPlayer() {
		player = minecraft.thePlayer;
	}

	private Block getFacingBlock() {
		IBlockState ibs = minecraft.theWorld.getBlockState(minecraft.objectMouseOver.getBlockPos());
		return ibs.getBlock();
	}

	private BlockPos getFacingBlockPos() {
		return minecraft.objectMouseOver.getBlockPos();
	}
}

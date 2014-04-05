package arcanelegacy.tileentity;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import arcanelegacy.blocks.ALBlocks;
import arcanelegacy.blocks.BlockArcaneInfuser;
import arcanelegacy.inventory.ContainerArcaneInfuser;
import arcanelegacy.item.ALItems;
import arcanelegacy.item.ItemDust;
import arcanelegacy.item.crafting.ArcaneInfuserRecipes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityArcaneInfuser extends TileEntityInventory implements ISidedInventory
{
	private static final int[] slots_top = new int[] {0};
	private static final int[] slots_bottom = new int[] {2, 1};
	private static final int[] slots_sides = new int[] {1};

	/** The number of ticks that the infuser will keep burning */
	public int infuserBurnTime;

	/** The number of ticks that a fresh copy of the currently-burning item would keep the infuser burning for */
	public int currentItemBurnTime;

	/** The number of ticks that the current item has been cooking for */
	public int infuserCookTime;

	public TileEntityArcaneInfuser() {
		inventory = new ItemStack[4];
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public String getInvName() {
		return "Arcane Infuser";
	}

	@Override
	public boolean isInvNameLocalized() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return player.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return slot == ContainerArcaneInfuser.OUTPUT ? false : (slot == ContainerArcaneInfuser.FUEL ? isItemFuel(stack) : true);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return side == 0 ? slots_bottom : (side == 1 ? slots_top : slots_sides);
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side) {
		return isItemValidForSlot(slot, itemstack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side) {
		return side != 0 || slot != ContainerArcaneInfuser.FUEL;
	}

	/**
	 * Returns an integer between 0 and the passed value representing how close the current item is to being completely
	 * cooked
	 */
	@SideOnly(Side.CLIENT)
	public int getCookProgressScaled(int par1) {
		return infuserCookTime * par1 / 200;
	}

	/**
	 * Returns an integer between 0 and the passed value representing how much burn time is left on the current fuel
	 * item, where 0 means that the item is exhausted and the passed value means that the item is fresh
	 */
	@SideOnly(Side.CLIENT)
	public int getBurnTimeRemainingScaled(int par1) {
		if (currentItemBurnTime == 0) {
			currentItemBurnTime = 200;
		}
		return infuserBurnTime * par1 / currentItemBurnTime;
	}

	/**
	 * Returns true if the infuser is currently burning.
	 */
	public boolean isBurning() {
		return this.infuserBurnTime > 0;
	}

	@Override
	public void updateEntity() {
		boolean flag = infuserBurnTime > 0;
		boolean flag1 = false;

		if (infuserBurnTime > 0) {
			--infuserBurnTime;
		}

		if (!worldObj.isRemote) {
			if (infuserBurnTime == 0 && canInfuse()) {
				currentItemBurnTime = infuserBurnTime = getItemBurnTime(inventory[ContainerArcaneInfuser.FUEL]);
				
				if (infuserBurnTime > 0) {
					flag1 = true;
					
					if (inventory[ContainerArcaneInfuser.FUEL] != null) {
						--inventory[ContainerArcaneInfuser.FUEL].stackSize;
						if (inventory[ContainerArcaneInfuser.FUEL].stackSize == 0) {
							inventory[ContainerArcaneInfuser.FUEL] = inventory[ContainerArcaneInfuser.FUEL].getItem().getContainerItemStack(inventory[ContainerArcaneInfuser.FUEL]);
						}
					}
				}
			}

			if (isBurning() && canInfuse()) {
				++infuserCookTime;
				if (infuserCookTime == 200) {
					infuserCookTime = 0;
					infuseItem();
					flag1 = true;
				}
			} else {
				infuserCookTime = 0;
			}

			if (flag != infuserBurnTime > 0) {
				flag1 = true;
				BlockArcaneInfuser.updateInfuserBlockState(infuserBurnTime > 0, worldObj, xCoord, yCoord, zCoord);
			}
		}

		if (flag1) {
			onInventoryChanged();
		}
	}

	/**
	 * Returns true if the infuser can charge an item, i.e. has a source item, destination stack isn't full, etc.
	 */
	private boolean canInfuse() {
		if (inventory[ContainerArcaneInfuser.INPUT_1] == null || inventory[ContainerArcaneInfuser.INPUT_2] == null) {
			return false;
		} else {
			ItemStack stack = ArcaneInfuserRecipes.infusing().getInfusingResult(inventory[ContainerArcaneInfuser.INPUT_1], inventory[ContainerArcaneInfuser.INPUT_2]);
			if (stack == null) {
				return false;
			} else if (inventory[ContainerArcaneInfuser.OUTPUT] == null) {
				return true;
			} else if (!inventory[ContainerArcaneInfuser.OUTPUT].isItemEqual(stack)) {
				return false;
			}
			int result = inventory[ContainerArcaneInfuser.OUTPUT].stackSize + stack.stackSize;
			return (result <= getInventoryStackLimit() && result <= stack.getMaxStackSize());
		}
	}

	/**
	 * Turn a rune from the infuser source stack into a charged rune in the infuser result stack.
	 */
	public void infuseItem() {
		if (canInfuse()) {
			ItemStack stack = ArcaneInfuserRecipes.infusing().getInfusingResult(inventory[ContainerArcaneInfuser.INPUT_1], inventory[ContainerArcaneInfuser.INPUT_2]);
			if (inventory[ContainerArcaneInfuser.OUTPUT] == null) {
				inventory[ContainerArcaneInfuser.OUTPUT] = stack.copy();
			} else if (inventory[ContainerArcaneInfuser.OUTPUT].isItemEqual(stack)) {
				inventory[ContainerArcaneInfuser.OUTPUT].stackSize += stack.stackSize;
			}

			--inventory[ContainerArcaneInfuser.INPUT_1].stackSize;
			--inventory[ContainerArcaneInfuser.INPUT_2].stackSize;

			if (inventory[ContainerArcaneInfuser.INPUT_1].stackSize <= 0) {
				inventory[ContainerArcaneInfuser.INPUT_1] = null;
			}
			if (inventory[ContainerArcaneInfuser.INPUT_2].stackSize <= 0) {
				inventory[ContainerArcaneInfuser.INPUT_2] = null;
			}
		}
	}

	/**
	 * Returns the number of ticks that the supplied fuel item will keep the infuser burning, or 0 if the item isn't
	 * fuel
	 */
	public static int getItemBurnTime(ItemStack stack)
	{
		if (stack == null) {
			return 0;
		} else {
			if (stack.getItem() instanceof ItemBlock && Block.blocksList[stack.itemID] != null) {
				if (Block.blocksList[stack.itemID] == ALBlocks.blockSpirit) {
					return 1600;	// value of coal
				}
			}

			if (stack.getItem() == ALItems.dust && stack.getItemDamage() == ItemDust.DUST_SPIRIT) {
				return 100;
			} else if (stack.getItem() == ALItems.spiritShard) {
				return 400;
			} else {
				return 0;
			}
		}
	}

	/**
	 * Return true if item is a fuel source (getItemBurnTime() > 0).
	 */
	public static boolean isItemFuel(ItemStack stack) {
		return getItemBurnTime(stack) > 0;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		infuserBurnTime = compound.getShort("BurnTime");
		infuserCookTime = compound.getShort("CookTime");
		currentItemBurnTime = getItemBurnTime(inventory[1]);
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setShort("BurnTime", (short) infuserBurnTime);
		compound.setShort("CookTime", (short) infuserCookTime);
	}
}
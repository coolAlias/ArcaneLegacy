package arcanelegacy.tileentity;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import arcanelegacy.blocks.BlockMortarPestle;
import arcanelegacy.item.ALItems;
import arcanelegacy.item.ItemDust;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityMortarPestle extends TileEntityInventory implements ISidedInventory
{
	private static final int[] slots_top = new int[] {0};
	private static final int[] slots_bottom = new int[] {2, 1};
	private static final int[] slots_sides = new int[] {1};

	/** Inventory slot constants */
	public static final int INV_SIZE = 2, GRIND_BASE = 0, GRIND_RESULT = 1;

	/** Number of ticks mortar & pestle will remain active */
	public int grinderBurnTime;

	/** Number of ticks required to grind an item (aka number of ticks fresh source will burn) */
	public static final int GRIND_TIME = 100;

	/** Number of ticks for which the current item has been grinding. */
	public int grinderCookTime;

	public TileEntityMortarPestle() {
		inventory = new ItemStack[INV_SIZE];
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public String getInvName() {
		return "Mortar and Pestle";
	}

	@Override
	public boolean isInvNameLocalized() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	/**
	 * Returns an integer between 0 and the passed value representing how 
	 * close the current item is to being completely cooked
	 */
	@SideOnly(Side.CLIENT)
	public int getGrindProgressScaled(int par1) {
		return grinderCookTime * par1 / GRIND_TIME;
	}

	/**
	 * Returns true if the mortar and pestle is currently in use
	 */
	public boolean isGrinding() {
		return grinderBurnTime > 0;
	}

	@Override
	public void updateEntity() {
		boolean updateBlock = isGrinding();
		boolean flag1 = false;

		if (!worldObj.isRemote) {
			if (grinderBurnTime == 0 && canGrind()) {
				grinderBurnTime = GRIND_TIME;
			}

			if (isGrinding() && canGrind()) {
				++grinderCookTime;

				if (grinderCookTime == GRIND_TIME) {
					grinderCookTime = 0;
					grindItem();
					flag1 = true;
				}
			} else {
				grinderBurnTime = 0;
				grinderCookTime = 0;
			}

			if (updateBlock != isGrinding()) {
				flag1 = true;
				BlockMortarPestle.updateMortarPestleBlockState(isGrinding(), worldObj, xCoord, yCoord, zCoord);
			}
		}

		if (flag1) {
			onInventoryChanged();
		}
	}

	/**
	 * Returns true if the mortar & pestle can grind an item, i.e. has a source item, destination stack isn't full, etc.
	 */
	private boolean canGrind()
	{
		if (inventory[GRIND_BASE] == null) {
			return false;
		} else {
			ItemStack itemstack = getGrindingResult(inventory[GRIND_BASE]);
			if (itemstack == null) {
				return false;
			} else if (inventory[GRIND_RESULT] == null) {
				return true;
			} else if (!inventory[GRIND_RESULT].isItemEqual(itemstack)) {
				return false;
			}
			int result = inventory[GRIND_RESULT].stackSize + itemstack.stackSize;
			return (result <= getInventoryStackLimit() && result <= itemstack.getMaxStackSize());
		}
	}

	/**
	 * Turn one item from the source stack into the appropriate ground item in the result stack
	 */
	public void grindItem() {
		if (canGrind()) {
			ItemStack result = getGrindingResult(inventory[GRIND_BASE]);

			if (inventory[GRIND_RESULT] == null) {
				inventory[GRIND_RESULT] = result.copy();
			} else if (inventory[GRIND_RESULT].isItemEqual(result)) {
				inventory[GRIND_RESULT].stackSize += result.stackSize;
			}

			--inventory[GRIND_BASE].stackSize;

			if (inventory[GRIND_BASE].stackSize <= 0) {
				inventory[GRIND_BASE] = null;
			}
		}
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return player.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public boolean isItemValidForSlot(int par1, ItemStack stack) {
		return par1 != GRIND_RESULT && isItemGrindable(stack);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return side == 0 ? slots_bottom : (side == 1 ? slots_top : slots_sides);
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		return isItemValidForSlot(slot, stack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		return side != 0 || slot != GRIND_BASE;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		grinderBurnTime = compound.getShort("BurnTime");
		grinderCookTime = compound.getShort("CookTime");
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setShort("BurnTime", (short) grinderBurnTime);
		compound.setShort("CookTime", (short) grinderCookTime);
	}

	/**
	 * Returns true if the ItemStack toGrind has a valid grinding result.
	 */
	public static boolean isItemGrindable(ItemStack toGrind) {
		return getGrindingResult(toGrind) != null;
	}

	/**
	 * Returns the ItemStack result of grinding the ItemStack 'toGrind'
	 */
	public static ItemStack getGrindingResult(ItemStack toGrind)
	{
		if (toGrind.itemID == ALItems.spiritShard.itemID) {
			return new ItemStack(ALItems.dust, 4, ItemDust.DUST_SPIRIT);
		} else if (toGrind.itemID == Item.diamond.itemID) {
			return new ItemStack(ALItems.dust, 4, ItemDust.DUST_DIAMOND);
		} else if (toGrind.itemID == Item.emerald.itemID) {
			return new ItemStack(ALItems.dust, 4, ItemDust.DUST_EMERALD);
		} else if (toGrind.itemID == Item.ingotGold.itemID) {
			return new ItemStack(ALItems.dust, 4, ItemDust.DUST_GOLD);
		} else if (toGrind.itemID == Block.netherrack.blockID) {
			return new ItemStack(ALItems.dust, 4, ItemDust.DUST_NETHERRACK);
		} else if (toGrind.itemID == Block.blockNetherQuartz.blockID) {
			return new ItemStack(ALItems.dust, 16, ItemDust.DUST_QUARTZ);
		} else if (toGrind.itemID == Item.netherQuartz.itemID) {
			return new ItemStack(ALItems.dust, 4, ItemDust.DUST_QUARTZ);
		} else if (toGrind.itemID == Block.oreNetherQuartz.blockID) {
			return new ItemStack(ALItems.dust, 4, ItemDust.DUST_QUARTZ);
		}

		return null;
	}
}
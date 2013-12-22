package coolalias.arcanelegacy.tileentity;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import coolalias.arcanelegacy.blocks.ALBlocks;
import coolalias.arcanelegacy.blocks.BlockArcaneInfuser;
import coolalias.arcanelegacy.inventory.ContainerArcaneInfuser;
import coolalias.arcanelegacy.item.ALItems;
import coolalias.arcanelegacy.item.ItemDust;
import coolalias.arcanelegacy.item.crafting.ArcaneInfuserRecipes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityArcaneInfuser extends TileEntity implements ISidedInventory
{
	private static final int[] slots_top = new int[] {0};
	private static final int[] slots_bottom = new int[] {2, 1};
	private static final int[] slots_sides = new int[] {1};

	/** Array bounds = number of slots in ContainerArcaneInfuser */
	private final ItemStack[] infuserInventory;

	/** The number of ticks that the infuser will keep burning */
	public int infuserBurnTime;

	/** The number of ticks that a fresh copy of the currently-burning item would keep the infuser burning for */
	public int currentItemBurnTime;

	/** The number of ticks that the current item has been cooking for */
	public int infuserCookTime;

	public TileEntityArcaneInfuser() {
		infuserInventory = new ItemStack[4];
	}

	@Override
	public int getSizeInventory() {
		return this.infuserInventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return this.infuserInventory[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount)
	{
		ItemStack stack = getStackInSlot(slot);

		if(stack != null)
		{
			if(stack.stackSize > amount)
			{
				stack = stack.splitStack(amount);
				this.onInventoryChanged();
			} else {
				setInventorySlotContents(slot, null);
			}
		}

		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		ItemStack stack = getStackInSlot(slot);
		setInventorySlotContents(slot, null);
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack)
	{
		infuserInventory[slot] = itemstack;

		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
		{
			itemstack.stackSize = getInventoryStackLimit();
		}

		onInventoryChanged();
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
	public void openChest() {}

	@Override
	public void closeChest() {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		return slot == ContainerArcaneInfuser.OUTPUT ? false : (slot == ContainerArcaneInfuser.FUEL ? isItemFuel(itemstack) : true);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return side == 0 ? slots_bottom : (side == 1 ? slots_top : slots_sides);
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side) {
		return this.isItemValidForSlot(slot, itemstack);
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
		return this.infuserCookTime * par1 / 200;
	}

	/**
	 * Returns an integer between 0 and the passed value representing how much burn time is left on the current fuel
	 * item, where 0 means that the item is exhausted and the passed value means that the item is fresh
	 */
	@SideOnly(Side.CLIENT)
	public int getBurnTimeRemainingScaled(int par1)
	{
		if (this.currentItemBurnTime == 0)
		{
			this.currentItemBurnTime = 200;
		}

		return this.infuserBurnTime * par1 / this.currentItemBurnTime;
	}

	/**
	 * Returns true if the infuser is currently burning.
	 */
	public boolean isBurning() {
		return this.infuserBurnTime > 0;
	}

	@Override
	public void updateEntity()
	{
		boolean flag = this.infuserBurnTime > 0;
		boolean flag1 = false;

		if (this.infuserBurnTime > 0)
		{
			--this.infuserBurnTime;
		}

		if (!this.worldObj.isRemote)
		{
			if (this.infuserBurnTime == 0 && this.canInfuse())
			{
				this.currentItemBurnTime = this.infuserBurnTime = getItemBurnTime(this.infuserInventory[ContainerArcaneInfuser.FUEL]);

				if (this.infuserBurnTime > 0)
				{
					flag1 = true;

					if (this.infuserInventory[ContainerArcaneInfuser.FUEL] != null)
					{
						--this.infuserInventory[ContainerArcaneInfuser.FUEL].stackSize;

						if (this.infuserInventory[ContainerArcaneInfuser.FUEL].stackSize == 0)
						{
							this.infuserInventory[ContainerArcaneInfuser.FUEL] = this.infuserInventory[ContainerArcaneInfuser.FUEL].getItem().getContainerItemStack(infuserInventory[ContainerArcaneInfuser.FUEL]);
						}
					}
				}
			}

			if (this.isBurning() && this.canInfuse())
			{
				++this.infuserCookTime;

				if (this.infuserCookTime == 200)
				{
					this.infuserCookTime = 0;
					this.infuseItem();
					flag1 = true;
				}
			}
			else
			{
				this.infuserCookTime = 0;
			}

			if (flag != this.infuserBurnTime > 0)
			{
				flag1 = true;
				BlockArcaneInfuser.updateInfuserBlockState(this.infuserBurnTime > 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
			}
		}

		if (flag1)
		{
			onInventoryChanged();
		}
	}

	/**
	 * Returns true if the infuser can charge an item, i.e. has a source item, destination stack isn't full, etc.
	 */
	private boolean canInfuse()
	{
		if (this.infuserInventory[ContainerArcaneInfuser.INPUT_1] == null || this.infuserInventory[ContainerArcaneInfuser.INPUT_2] == null)
		{
			return false;
		}
		else
		{
			ItemStack itemstack = ArcaneInfuserRecipes.infusing().getInfusingResult(this.infuserInventory[ContainerArcaneInfuser.INPUT_1], this.infuserInventory[ContainerArcaneInfuser.INPUT_2]);
			if (itemstack == null) return false;
			if (this.infuserInventory[ContainerArcaneInfuser.OUTPUT] == null) return true;
			if (!this.infuserInventory[ContainerArcaneInfuser.OUTPUT].isItemEqual(itemstack)) return false;
			int result = infuserInventory[ContainerArcaneInfuser.OUTPUT].stackSize + itemstack.stackSize;
			return (result <= getInventoryStackLimit() && result <= itemstack.getMaxStackSize());
		}
	}

	/**
	 * Turn a rune from the infuser source stack into a charged rune in the infuser result stack.
	 */
	public void infuseItem()
	{
		if (this.canInfuse())
		{
			ItemStack itemstack = ArcaneInfuserRecipes.infusing().getInfusingResult(this.infuserInventory[ContainerArcaneInfuser.INPUT_1], this.infuserInventory[ContainerArcaneInfuser.INPUT_2]);

			if (this.infuserInventory[ContainerArcaneInfuser.OUTPUT] == null)
			{
				this.infuserInventory[ContainerArcaneInfuser.OUTPUT] = itemstack.copy();
			}
			else if (this.infuserInventory[ContainerArcaneInfuser.OUTPUT].isItemEqual(itemstack))
			{
				infuserInventory[ContainerArcaneInfuser.OUTPUT].stackSize += itemstack.stackSize;
			}

			--this.infuserInventory[ContainerArcaneInfuser.INPUT_1].stackSize;
			--this.infuserInventory[ContainerArcaneInfuser.INPUT_2].stackSize;

			if (this.infuserInventory[ContainerArcaneInfuser.INPUT_1].stackSize <= 0)
			{
				this.infuserInventory[ContainerArcaneInfuser.INPUT_1] = null;
			}
			if (this.infuserInventory[ContainerArcaneInfuser.INPUT_2].stackSize <= 0)
			{
				this.infuserInventory[ContainerArcaneInfuser.INPUT_2] = null;
			}
		}
	}

	/**
	 * Returns the number of ticks that the supplied fuel item will keep the infuser burning, or 0 if the item isn't
	 * fuel
	 */
	public static int getItemBurnTime(ItemStack par0ItemStack)
	{
		if (par0ItemStack == null)
		{
			return 0;
		}
		else
		{
			int i = par0ItemStack.getItem().itemID;
			Item item = par0ItemStack.getItem();

			if (par0ItemStack.getItem() instanceof ItemBlock && Block.blocksList[i] != null)
			{
				Block block = Block.blocksList[i];

				if (block == ALBlocks.blockSpirit)
				{
					return 1600;	// value of coal
				}
			}

			if (i == ALItems.dust.itemID && par0ItemStack.getItemDamage() == ItemDust.DUST_SPIRIT) return 100;
			if (i == ALItems.spiritShard.itemID) return 400;
			return 0;
		}
	}

	/**
	 * Return true if item is a fuel source (getItemBurnTime() > 0).
	 */
	public static boolean isItemFuel(ItemStack itemstack) {
		return getItemBurnTime(itemstack) > 0;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		NBTTagList items = compound.getTagList("Items");

		for (int i = 0; i < items.tagCount(); ++i)
		{
			NBTTagCompound item = (NBTTagCompound) items.tagAt(i);
			byte slot = item.getByte("Slot");

			if (slot >= 0 && slot < getSizeInventory())
			{
				setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(item));
			}
		}

		this.infuserBurnTime = compound.getShort("BurnTime");
		this.infuserCookTime = compound.getShort("CookTime");
		this.currentItemBurnTime = getItemBurnTime(this.infuserInventory[1]);
	}

	@Override
	public void writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		
		compound.setShort("BurnTime", (short) this.infuserBurnTime);
		compound.setShort("CookTime", (short) this.infuserCookTime);
		
		NBTTagList items = new NBTTagList();

		for (int i = 0; i < getSizeInventory(); ++i)
		{
			if (getStackInSlot(i) != null)
			{
				NBTTagCompound item = new NBTTagCompound();
				item.setByte("Slot", (byte) i);
				getStackInSlot(i).writeToNBT(item);
				items.appendTag(item);
			}
		}

		compound.setTag("Items", items);
	}
}
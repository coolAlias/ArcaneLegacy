package coolalias.arcanelegacy.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import coolalias.arcanelegacy.blocks.BlockArcaneInscriber;
import coolalias.arcanelegacy.inventory.ContainerArcaneInscriber;
import coolalias.arcanelegacy.item.ALItems;
import coolalias.arcanelegacy.spells.SpellRecipes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityArcaneInscriber extends TileEntityEnchantmentTable implements ISidedInventory//TileEntity implements ISidedInventory
{
	private static final int[] slots_top = new int[] {0};
	private static final int[] slots_bottom = new int[] {2, 1};
	private static final int[] slots_sides = new int[] {1};

	/** Array bounds = number of slots in ContainerArcaneInscriber */
	private ItemStack[] inscriberInventory = new ItemStack[ContainerArcaneInscriber.INV_START];

	/** Time required to scribe a single scroll, amount of 'power' a charged rune provides */
	private static final int INSCRIBE_TIME = 100, RUNE_CHARGE_TIME = 400;

	/** The number of ticks that the inscriber will keep inscribing */
	public int currentInscribeTime;

	/** The number of ticks that the current scroll has been inscribing for */
	public int inscribeProgressTime;

	public TileEntityArcaneInscriber() {}

	@Override
	public int getSizeInventory() {
		return inscriberInventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inscriberInventory[slot];
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
	public void setInventorySlotContents(int slot, ItemStack stack)
	{
		inscriberInventory[slot] = stack;
		
		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}
		
		onInventoryChanged();
	}

	@Override
	public String getInvName() {
		return "Arcane Inscriber";
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
	public int getInscribeProgressScaled(int par1) {
		return inscribeProgressTime * par1 / INSCRIBE_TIME;
	}

	/**
	 * Returns an integer between 0 and the passed value representing how much burn time is left on the current fuel
	 * item, where 0 means that the item is exhausted and the passed value means that the item is fresh
	 */
	@SideOnly(Side.CLIENT)
	public int getInscribeTimeRemainingScaled(int par1) {
		return currentInscribeTime * par1 / INSCRIBE_TIME;
	}

	/**
	 * Returns true if a scroll could currently be inscribed
	 */
	public boolean isInscribing() {
		return currentInscribeTime > 0;
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		
		boolean flag = currentInscribeTime > 0;
		boolean flag1 = false;

		if (currentInscribeTime > 0)
		{
			--currentInscribeTime;

			// Container recipe doesn't match current non-null InscribingResult
			flag1 = (inscriberInventory[ContainerArcaneInscriber.RECIPE] != getCurrentRecipe() && getCurrentRecipe() != null);
			// Recipe changed - reset timer
			if (flag1) {
				inscriberInventory[ContainerArcaneInscriber.RECIPE] = getCurrentRecipe();
				onInventoryChanged();
				currentInscribeTime = 0;
			}
		}

		if (!worldObj.isRemote)
		{
			if (currentInscribeTime == 0)
			{
				flag1 = (inscriberInventory[ContainerArcaneInscriber.RECIPE] != getCurrentRecipe());
				inscriberInventory[ContainerArcaneInscriber.RECIPE] = getCurrentRecipe();
				// Recipe changed - update inventory
				if (flag1) { onInventoryChanged(); }

				if (canInscribe()) {
					currentInscribeTime = getInscriberChargeTime(inscriberInventory[0]);
				}

				if (currentInscribeTime > 0)
				{
					flag1 = true;

					// Decrement input slots, increment discharge slots
					for (int i = 0; i < ContainerArcaneInscriber.RUNE_SLOTS; ++i)
					{
						if (inscriberInventory[ContainerArcaneInscriber.INPUT[i]] != null)
						{
							--inscriberInventory[ContainerArcaneInscriber.INPUT[i]].stackSize;
							if (inscriberInventory[ContainerArcaneInscriber.DISCHARGE[i]] != null) {
								++inscriberInventory[ContainerArcaneInscriber.DISCHARGE[i]].stackSize;
							}
							else {
								ItemStack discharge = new ItemStack(ALItems.runeBasic,1,inscriberInventory[ContainerArcaneInscriber.INPUT[i]].getItemDamage());
								inscriberInventory[ContainerArcaneInscriber.DISCHARGE[i]] = discharge.copy();
							}

							if (inscriberInventory[ContainerArcaneInscriber.INPUT[i]].stackSize == 0)
							{
								inscriberInventory[ContainerArcaneInscriber.INPUT[i]] = inscriberInventory[ContainerArcaneInscriber.INPUT[i]].getItem().getContainerItemStack(inscriberInventory[ContainerArcaneInscriber.INPUT[i]]);
							}
						}
					}
				}
			}

			if (isInscribing() && canInscribe())
			{
				++inscribeProgressTime;

				if (inscribeProgressTime == INSCRIBE_TIME)
				{
					inscribeProgressTime = 0;
					inscribeScroll();
					flag1 = true;
				}
			}
			else
			{
				inscribeProgressTime = 0;
			}

			if (flag != currentInscribeTime > 0)
			{
				flag1 = true;
				BlockArcaneInscriber.updateInscriberBlockState(currentInscribeTime > 0, worldObj, xCoord, yCoord, zCoord);
			}
		}

		if (flag1)
		{
			onInventoryChanged();
		}
	}
	
	/**
	 * Returns true if the inscriber can inscribe a scroll;
	 * i.e. has a blank scroll, has a charged rune, destination stack isn't full, etc.
	 */
	private boolean canInscribe()
	{
		boolean canInscribe = true;

		// Still time remaining to inscribe current recipe, check if blank scrolls available
		if (isInscribing() && inscriberInventory[ContainerArcaneInscriber.RECIPE] != null)
		{
			canInscribe = (inscriberInventory[ContainerArcaneInscriber.BLANK_SCROLL] == null ? false : true);
		}
		// No charged rune in first input slot
		else if (inscriberInventory[ContainerArcaneInscriber.INPUT[0]] == null)
		{
			canInscribe = false;
		}
		// No blank scrolls to inscribe
		else if (inscriberInventory[ContainerArcaneInscriber.BLANK_SCROLL] == null)
		{
			canInscribe = false;
		}
		// Check if any of the discharge slots are full
		else
		{
			for (int i = 0; i < ContainerArcaneInscriber.RUNE_SLOTS && canInscribe; ++i)
			{
				if (inscriberInventory[ContainerArcaneInscriber.DISCHARGE[i]] != null)
				{
					// Check if input[i] and discharge[i] are mismatched
					if (inscriberInventory[ContainerArcaneInscriber.INPUT[i]] != null)
					{
						canInscribe = ((inscriberInventory[ContainerArcaneInscriber.INPUT[i]].getItemDamage()
								== inscriberInventory[ContainerArcaneInscriber.DISCHARGE[i]].getItemDamage())
								&& inscriberInventory[ContainerArcaneInscriber.DISCHARGE[i]].stackSize
								< inscriberInventory[ContainerArcaneInscriber.DISCHARGE[i]].getMaxStackSize());
					}
					else
					{
						canInscribe = inscriberInventory[ContainerArcaneInscriber.DISCHARGE[i]].stackSize < inscriberInventory[ContainerArcaneInscriber.DISCHARGE[i]].getMaxStackSize();
					}
				}
			}
		}

		if (canInscribe)
		{
			ItemStack itemstack = getCurrentRecipe();
			// No recipe, check if stored recipe
			if (itemstack == null) { itemstack = inscriberInventory[ContainerArcaneInscriber.RECIPE]; }
			// Invalid recipe
			if (itemstack == null) return false;
			// Recipe is different from the current recipe
			if (inscriberInventory[ContainerArcaneInscriber.RECIPE] != null && !inscriberInventory[ContainerArcaneInscriber.RECIPE].isItemEqual(itemstack)) return false;
			// Output slot is empty, inscribe away!
			if (inscriberInventory[ContainerArcaneInscriber.OUTPUT] == null) return true;
			// Current scroll in output slot is different than recipe output
			if (!inscriberInventory[ContainerArcaneInscriber.OUTPUT].isItemEqual(itemstack)) return false;
			// Inscribing may surpass stack size limit
			int result = inscriberInventory[ContainerArcaneInscriber.OUTPUT].stackSize + itemstack.stackSize;
			return (result <= getInventoryStackLimit() && result <= itemstack.getMaxStackSize());
		}
		else
		{
			return canInscribe;
		}
	}

	/**
	 * Returns the crafting result of runes currently in place
	 */
	public ItemStack getCurrentRecipe() {
		return SpellRecipes.spells().getInscribingResult(inscriberInventory);
	}

	/**
	 * Inscribe a blank scroll with the last current recipe
	 */
	public void inscribeScroll()
	{
		if (canInscribe())
		{
			ItemStack inscribeResult = inscriberInventory[ContainerArcaneInscriber.RECIPE];

			if (inscribeResult != null)
			{
				if (inscriberInventory[ContainerArcaneInscriber.OUTPUT] == null)
				{
					inscriberInventory[ContainerArcaneInscriber.OUTPUT] = inscribeResult.copy();
				}
				else if (inscriberInventory[ContainerArcaneInscriber.OUTPUT].isItemEqual(inscribeResult))
				{
					inscriberInventory[ContainerArcaneInscriber.OUTPUT].stackSize += inscribeResult.stackSize;
				}

				--inscriberInventory[ContainerArcaneInscriber.BLANK_SCROLL].stackSize;

				if (inscriberInventory[ContainerArcaneInscriber.BLANK_SCROLL].stackSize <= 0)
				{
					inscriberInventory[ContainerArcaneInscriber.BLANK_SCROLL] = null;
				}
			}
		}
	}

	/**
	 * Returns the number of ticks that the supplied rune will keep
	 * the inscriber running, or 0 if the rune isn't charged
	 */
	public static int getInscriberChargeTime(ItemStack rune)
	{
		if (rune != null && rune.itemID == ALItems.runeCharged.itemID) {
			return RUNE_CHARGE_TIME;
		} else { return 0; }
	}

	/**
	 * Return true if item is an energy source (i.e. a charged rune)
	 */
	public static boolean isSource(ItemStack itemstack) {
		return getInscriberChargeTime(itemstack) > 0;
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
	public boolean isItemValidForSlot(int slot, ItemStack itemstack)
	{
		boolean isValid = false;

		if (slot >= ContainerArcaneInscriber.INPUT[0] && slot <= ContainerArcaneInscriber.INPUT[ContainerArcaneInscriber.RUNE_SLOTS-1]) {
			isValid = itemstack.getItem().itemID == ALItems.runeCharged.itemID;
		} else if (slot == ContainerArcaneInscriber.BLANK_SCROLL) {
			isValid = itemstack.getItem().itemID == ALItems.scrollBlank.itemID;
		}
		return isValid;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int par1) {
		return par1 == 0 ? slots_bottom : (par1 == 1 ? slots_top : slots_sides);
	}

	@Override
	public boolean canInsertItem(int par1, ItemStack par2ItemStack, int par3) {
		return isItemValidForSlot(par1, par2ItemStack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side) {
		return (slot == ContainerArcaneInscriber.OUTPUT || (slot >= ContainerArcaneInscriber.DISCHARGE[0] && slot <= ContainerArcaneInscriber.DISCHARGE[ContainerArcaneInscriber.RUNE_SLOTS-1]));
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

			if (slot >= 0 && slot < getSizeInventory()) {
				setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(item));
			}
		}

		currentInscribeTime = compound.getShort("IncribeTime");
		inscribeProgressTime = compound.getShort("InscribeProgress");
	}

	@Override
	public void writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		
		compound.setShort("InscribeTime", (short) currentInscribeTime);
		compound.setShort("InscribeProgress", (short) inscribeProgressTime);
		
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
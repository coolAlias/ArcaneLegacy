package coolalias.arcanelegacy.tileentity;

import java.util.Random;

import coolalias.arcanelegacy.blocks.BlockArcaneInscriber;
import coolalias.arcanelegacy.inventory.ContainerArcaneInscriber;
import coolalias.arcanelegacy.item.ALItems;
import coolalias.arcanelegacy.spells.SpellRecipes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class TileEntityArcaneInscriber extends TileEntity implements ISidedInventory
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

	private String displayName = "Arcane Inscriber";

	// ENCHANTMENT BOOK //
	/** Used by the render to make the book 'bounce' */
	public int tickCount;

	/** Value used for determining how the page flip should look. */
	public float pageFlip;

	/** The last tick's pageFlip value. */
	public float pageFlipPrev;
	public float field_70373_d;
	public float field_70374_e;

	/** The amount that the book is open. */
	public float bookSpread;

	/** The amount that the book is open. */
	public float bookSpreadPrev;
	public float bookRotation2;
	public float bookRotationPrev;
	public float bookRotation;
	private static Random rand = new Random();
	private String field_94136_s;

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
		return isInvNameLocalized() ? displayName : "container.arcaneinscriber";
	}

	/**
	 * If this returns false, the inventory name will be used as an unlocalized name, and translated into the player's
	 * language. Otherwise it will be used directly.
	 */
	@Override
	public boolean isInvNameLocalized() {
		return displayName != null && displayName.length() > 0;
	}

	/**
	 * Sets the custom display name to use when opening a GUI linked to this tile entity.
	 */
	public void setGuiDisplayName(String par1Str) {
		displayName = par1Str;
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

	/**
	 * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
	 * ticks and creates a new spawn inside its implementation.
	 */
	public void updateEntity()
	{
		updateBook();
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

	private void updateBook()
	{
		bookSpreadPrev = bookSpread;
		bookRotationPrev = bookRotation2;
		EntityPlayer entityplayer = worldObj.getClosestPlayer((double)((float)xCoord + 0.5F), (double)((float)yCoord + 0.5F), (double)((float)zCoord + 0.5F), 3.0D);

		if (entityplayer != null)
		{
			double d0 = entityplayer.posX - (double)((float)xCoord + 0.5F);
			double d1 = entityplayer.posZ - (double)((float)zCoord + 0.5F);
			bookRotation = (float)Math.atan2(d1, d0);
			bookSpread += 0.1F;

			if (bookSpread < 0.5F || rand.nextInt(40) == 0)
			{
				float f = field_70373_d;

				do
				{
					field_70373_d += (float)(rand.nextInt(4) - rand.nextInt(4));
				}
				while (f == field_70373_d);
			}
		}
		else
		{
			bookRotation += 0.02F;
			bookSpread -= 0.1F;
		}

		while (bookRotation2 >= (float)Math.PI)
		{
			bookRotation2 -= ((float)Math.PI * 2F);
		}

		while (bookRotation2 < -(float)Math.PI)
		{
			bookRotation2 += ((float)Math.PI * 2F);
		}

		while (bookRotation >= (float)Math.PI)
		{
			bookRotation -= ((float)Math.PI * 2F);
		}

		while (bookRotation < -(float)Math.PI)
		{
			bookRotation += ((float)Math.PI * 2F);
		}

		float f1;

		for (f1 = bookRotation - bookRotation2; f1 >= (float)Math.PI; f1 -= ((float)Math.PI * 2F))
		{
			;
		}

		while (f1 < -(float)Math.PI)
		{
			f1 += ((float)Math.PI * 2F);
		}

		bookRotation2 += f1 * 0.4F;

		if (bookSpread < 0.0F)
		{
			bookSpread = 0.0F;
		}

		if (bookSpread > 1.0F)
		{
			bookSpread = 1.0F;
		}

		++tickCount;
		pageFlipPrev = pageFlip;
		float f2 = (field_70373_d - pageFlip) * 0.4F;
		float f3 = 0.2F;

		if (f2 < -f3)
		{
			f2 = -f3;
		}

		if (f2 > f3)
		{
			f2 = f3;
		}

		field_70374_e += (f2 - field_70374_e) * 0.9F;
		pageFlip += field_70374_e;
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

	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
	 */
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

	/**
	 * Returns an array containing the indices of the slots that can be accessed by automation on the given side of this
	 * block.
	 */
	@Override
	public int[] getAccessibleSlotsFromSide(int par1) {
		return par1 == 0 ? slots_bottom : (par1 == 1 ? slots_top : slots_sides);
	}

	/**
	 * Returns true if automation can insert the given item in the given slot from the given side. Args: Slot, item,
	 * side
	 */
	@Override
	public boolean canInsertItem(int par1, ItemStack par2ItemStack, int par3) {
		return isItemValidForSlot(par1, par2ItemStack);
	}

	/**
	 * Returns true if automation can extract the given item in the given slot from the given side.
	 * Args: Slot, item, side
	 */
	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side) {
		return (slot == ContainerArcaneInscriber.OUTPUT || (slot >= ContainerArcaneInscriber.DISCHARGE[0] && slot <= ContainerArcaneInscriber.DISCHARGE[ContainerArcaneInscriber.RUNE_SLOTS-1]));
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		NBTTagList items = compound.getTagList("Items");
		// inscriberInventory = new ItemStack[inscriberInventory.length];

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

		if (compound.hasKey("CustomName")) {
			displayName = compound.getString("CustomName");
		}
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

		if (isInvNameLocalized()) {
			compound.setString("CustomName", displayName);
		}
	}
}

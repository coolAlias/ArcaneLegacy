package arcanelegacy.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import arcanelegacy.item.ItemScroll;
import arcanelegacy.item.ItemWand;

public class InventoryWand implements IInventory
{
	private String name = "Wand Inventory";

	private final ItemStack wand;

	public static final int INV_SIZE = 4, ACTIVE_SLOT = INV_SIZE;

	private ItemStack[] inventory = new ItemStack[INV_SIZE];

	public InventoryWand(ItemStack stack) {
		//System.out.println("[WAND INV] Constructor called");
		//uniqueID = "";
		wand = stack;		
		//uniqueID = UUID.randomUUID().toString();
		if (!wand.hasTagCompound()) {
			wand.setTagCompound(new NBTTagCompound());
		}
		readFromNBT(wand.getTagCompound());
	}

	/**
	 * Returns index of slot that is 'active'
	 */
	public int getActiveSlot() {
		return wand.getTagCompound().getByte("WandActiveSlot");
	}

	/**
	 * Sets the active slot index to the next index within the inventory size
	 */
	public void nextActiveSlot() {
		byte activeSlot = (byte)(wand.getTagCompound().getByte("WandActiveSlot") + 1);
		if (activeSlot == ACTIVE_SLOT) {
			activeSlot = 0;
		}
		wand.getTagCompound().setByte("WandActiveSlot", (byte) activeSlot);
	}

	/**
	 * Sets the active slot index to the previous index within the inventory size
	 */
	public void prevActiveSlot() {
		byte activeSlot = (byte)(wand.getTagCompound().getByte("WandActiveSlot") - 1);
		if (activeSlot < 0) {
			activeSlot = ACTIVE_SLOT - 1;
		}
		wand.getTagCompound().setByte("WandActiveSlot", (byte) activeSlot);
	}

	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if (slot == ACTIVE_SLOT) {
			return inventory[wand.getTagCompound().getByte("WandActiveSlot")];
		} else {
			return inventory[slot];
		}
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			if (stack.stackSize > amount) {
				stack = stack.splitStack(amount);
				onInventoryChanged();
			} else {
				setInventorySlotContents(slot, null);
			}
		}

		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			setInventorySlotContents(slot, null);
		}
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory[slot] = stack;
		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}
		onInventoryChanged();
	}

	@Override
	public String getInvName() {
		return name;
	}

	@Override
	public boolean isInvNameLocalized() {
		return name.length() > 0;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void onInventoryChanged() {
		for (int i = 0; i < getSizeInventory(); ++i) {
			if (getStackInSlot(i) != null && getStackInSlot(i).stackSize == 0) {
				inventory[i] = null;
			}
		}
		writeToNBT(wand.getTagCompound());
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemWand;
	}

	@Override
	public void openChest() {}

	@Override
	public void closeChest() {}

	@Override
	public boolean isItemValidForSlot(int par1, ItemStack stack) {
		return (stack.getItem() instanceof ItemScroll ? true : false);
	}

	public void readFromNBT(NBTTagCompound compound)
	{
		//System.out.println("[WAND INV] Reading from NBT");
		/*
		if ("".equals(uniqueID))
		{	
			uniqueID = tagcompound.getString("uniqueID");
			System.out.println("[WAND INV] UUID from NBT = " + uniqueID);
			if ("".equals(uniqueID))
			{
				uniqueID = UUID.randomUUID().toString();
				System.out.println("[WAND INV] Randomly assigned UUID = " + uniqueID);
			}
		}
		 */

		NBTTagList items = compound.getTagList("WandInventory");
		for (int i = 0; i < items.tagCount(); ++i) {
			NBTTagCompound item = (NBTTagCompound) items.tagAt(i);
			byte slot = item.getByte("Slot");
			if (slot >= 0 && slot < getSizeInventory()) {
				setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(item));
			}
		}
	}

	public void writeToNBT(NBTTagCompound compound) {
		NBTTagList items = new NBTTagList();
		for (int i = 0; i < getSizeInventory(); ++i) {
			if (getStackInSlot(i) != null) {
				NBTTagCompound item = new NBTTagCompound();
				item.setByte("Slot", (byte)i);
				getStackInSlot(i).writeToNBT(item);
				items.appendTag(item);
			}
		}

		compound.setTag("WandInventory", items);
	}
}
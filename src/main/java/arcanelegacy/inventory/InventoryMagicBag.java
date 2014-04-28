package arcanelegacy.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import arcanelegacy.item.ItemMagicBag;

public class InventoryMagicBag implements IInventory
{
	private String name = "Bag of Holding";

	/** Defining your inventory size this way is handy */
	public static final int INV_SIZE = 10;

	/** Inventory's size must be same as number of slots you add to the Container class */
	private ItemStack[] inventory = new ItemStack[INV_SIZE];

	/** Provides NBT Tag Compound to reference */
	private final ItemStack invStack;

	public InventoryMagicBag(ItemStack stack) {
		invStack = stack;
		if (!invStack.hasTagCompound()) {
			invStack.setTagCompound(new NBTTagCompound());
		}
		readFromNBT(invStack.getTagCompound());
	}

	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			if(stack.stackSize > amount) {
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
		setInventorySlotContents(slot, null);
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
		return 64;
	}

	@Override
	public void onInventoryChanged() {
		for (int i = 0; i < getSizeInventory(); ++i) {
			if (getStackInSlot(i) != null && getStackInSlot(i).stackSize == 0) {
				inventory[i] = null;
			}
		}
		writeToNBT(invStack.getTagCompound());
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return player.getHeldItem() == invStack;
	}

	@Override
	public void openChest() {}

	@Override
	public void closeChest() {}

	/**
	 * This method doesn't seem to do what it claims to do, as
	 * inventory can still be left-clicked and placed in the inventory
	 * even when this returns false
	 */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return !(stack.getItem() instanceof ItemMagicBag);
	}

	public void readFromNBT(NBTTagCompound compound) {
		NBTTagList items = compound.getTagList("ItemInventory");
		for (int i = 0; i < items.tagCount(); ++i) {
			NBTTagCompound item = (NBTTagCompound) items.tagAt(i);
			byte slot = item.getByte("Slot");
			if (slot >= 0 && slot < getSizeInventory()) {
				inventory[slot] = ItemStack.loadItemStackFromNBT(item);
			}
		}
	}

	public void writeToNBT(NBTTagCompound compound) {
		NBTTagList items = new NBTTagList();
		for (int i = 0; i < getSizeInventory(); ++i) {
			if (getStackInSlot(i) != null) {
				NBTTagCompound item = new NBTTagCompound();
				item.setByte("Slot", (byte) i);
				getStackInSlot(i).writeToNBT(item);
				items.appendTag(item);
			}
		}
		compound.setTag("ItemInventory", items);
	}
}
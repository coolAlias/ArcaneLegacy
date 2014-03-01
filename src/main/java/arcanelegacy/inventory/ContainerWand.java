package arcanelegacy.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import arcanelegacy.item.ItemScroll;

public class ContainerWand extends Container
{
	public final InventoryWand inventory;

	private static final int ARMOR_START = InventoryWand.INV_SIZE, ARMOR_END = ARMOR_START+3, INV_START = ARMOR_END+1,
			INV_END = INV_START+26, HOTBAR_START = INV_END+1, HOTBAR_END = HOTBAR_START+8;

	public ContainerWand(EntityPlayer player, InventoryPlayer inv, InventoryWand wand)
	{
		int i = 0;
		this.inventory = wand;		

		// WAND INVENTORY
		for (i = 0; i < InventoryWand.INV_SIZE; ++i) {
			addSlotToContainer(new SlotWand(this.inventory, i, 80, 8 + (18*i)));
		}

		// ARMOR SLOTS
		for (i = 0; i < 4; ++i) {
			addSlotToContainer(new SlotArmor(player, inv, inv.getSizeInventory() - 1 - i, 8, 8 + i * 18, i));
		}

		// PLAYER INVENTORY
		for (i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		// PLAYER ACTION BAR
		for (i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(inv, i, 8 + i * 18, 142));
		}
	}

	/**
	 * Returns index of slot that is 'active'
	 */
	public final int getActiveSlot() {
		return ((InventoryWand) inventory).getActiveSlot();
	}

	/**
	 * Sets the active slot index to the next index within the inventory size
	 */
	public final void nextActiveSlot() {
		((InventoryWand) inventory).nextActiveSlot();
	}

	/**
	 * Sets the active slot index to the previous index within the inventory size
	 */
	public final void prevActiveSlot() {
		((InventoryWand) inventory).prevActiveSlot();
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return inventory.isUseableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int par2)
	{
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(par2);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			// If item is in scroll or armor slots
			if (par2 < INV_START)
			{
				// try to place in player action bar
				if (!this.mergeItemStack(itemstack1, HOTBAR_START, HOTBAR_END+1, false))
				{
					// couldn't place in action bar, try inventory
					if (!this.mergeItemStack(itemstack1, INV_START, INV_END+1, false)) {
						return null;
					}
				}

				slot.onSlotChange(itemstack1, itemstack);
			}
			// Item is in inventory / hotbar, try to place either in scroll or armor slots
			else
			{
				// if it is a scroll, place in the first open scroll slot
				if (itemstack1.getItem() instanceof ItemScroll)
				{
					if (!this.mergeItemStack(itemstack1, 0, InventoryWand.INV_SIZE, false))
					{
						return null;
					}
				}
				else if (itemstack1.getItem() instanceof ItemArmor)
				{
					int type = ((ItemArmor) itemstack1.getItem()).armorType;
					if (!this.mergeItemStack(itemstack1, ARMOR_START + type, ARMOR_START + type + 1, false))
					{
						return null;
					}
				}
				// item in player's inventory, but not in action bar
				else if (par2 >= INV_START && par2 < HOTBAR_START)
				{
					// place in action bar
					if (!this.mergeItemStack(itemstack1, HOTBAR_START, HOTBAR_END+1, false))
					{
						return null;
					}
				}
				// item in action bar - place in player inventory
				else if (par2 >= HOTBAR_START && par2 < HOTBAR_END+1)
				{
					if (!this.mergeItemStack(itemstack1, INV_START, INV_END+1, false))
					{
						return null;
					}
				}
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(player, itemstack1);
		}

		return itemstack;
	}

	/**
	 * merges provided ItemStack with the first available one in the container/player inventory
	 */
	@Override
	protected boolean mergeItemStack(ItemStack par1ItemStack, int par2, int par3, boolean par4)
	{
		boolean flag1 = false;
		int k = par2;
		Slot slot;
		ItemStack itemstack1;

		if (par4) { k = par3 - 1; }

		if (par1ItemStack.isStackable())
		{
			while (par1ItemStack.stackSize > 0 && (!par4 && k < par3 || par4 && k >= par2))
			{
				slot = (Slot) inventorySlots.get(k);
				itemstack1 = slot.getStack();

				if (itemstack1 != null && itemstack1.itemID == par1ItemStack.itemID && (!par1ItemStack.getHasSubtypes() || par1ItemStack.getItemDamage() == itemstack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(par1ItemStack, itemstack1))
				{
					int l = itemstack1.stackSize + par1ItemStack.stackSize;

					if (l <= par1ItemStack.getMaxStackSize() && l <= slot.getSlotStackLimit())
					{
						par1ItemStack.stackSize = 0;
						itemstack1.stackSize = l;
						inventory.onInventoryChanged();
						flag1 = true;
					}
					else if (itemstack1.stackSize < par1ItemStack.getMaxStackSize() && l < slot.getSlotStackLimit())
					{
						par1ItemStack.stackSize -= par1ItemStack.getMaxStackSize() - itemstack1.stackSize;
						itemstack1.stackSize = par1ItemStack.getMaxStackSize();
						inventory.onInventoryChanged();
						flag1 = true;
					}
				}

				if (par4) { --k; }

				else { ++k; }
			}
		}

		if (par1ItemStack.stackSize > 0)
		{
			if (par4) { k = par3 - 1; }

			else { k = par2; }

			while (!par4 && k < par3 || par4 && k >= par2)
			{
				slot = (Slot) inventorySlots.get(k);
				itemstack1 = slot.getStack();

				if (itemstack1 == null)
				{
					int l = par1ItemStack.stackSize;

					if (l <= slot.getSlotStackLimit())
					{
						slot.putStack(par1ItemStack.copy());
						par1ItemStack.stackSize = 0;
						inventory.onInventoryChanged();
						flag1 = true;
						break;
					}
					else
					{
						putStackInSlot(k, new ItemStack(par1ItemStack.getItem(), slot.getSlotStackLimit()));
						par1ItemStack.stackSize -= slot.getSlotStackLimit();
						inventory.onInventoryChanged();
						flag1 = true;
					}
				}

				if (par4) { --k; }

				else { ++k; }
			}
		}

		return flag1;
	}
}
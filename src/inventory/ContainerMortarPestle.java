package coolalias.arcanelegacy.inventory;

import coolalias.arcanelegacy.tileentity.TileEntityMortarPestle;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerMortarPestle extends Container
{
	private TileEntityMortarPestle mortarPestle;

	private int lastGrindTime;

	private static final int INV_START = TileEntityMortarPestle.INV_SIZE, INV_END = INV_START+26,
			HOTBAR_START = INV_END+1, HOTBAR_END= HOTBAR_START+8;

	public ContainerMortarPestle(InventoryPlayer inventoryPlayer, TileEntityMortarPestle tileEntity)
	{
		mortarPestle = tileEntity;
		addSlotToContainer(new SlotMortarPestle(tileEntity, TileEntityMortarPestle.GRIND_BASE, 56, 35, true));
		addSlotToContainer(new SlotMortarPestle(tileEntity, TileEntityMortarPestle.GRIND_RESULT, 116, 34, false));

		for (int invRow = 0; invRow < 3; ++invRow)
		{
			for (int invCol = 0; invCol < 9; ++invCol)
			{
				addSlotToContainer(new Slot(inventoryPlayer, invCol + invRow * 9 + 9, 8 + invCol * 18, 84 + invRow * 18));
			}
		}

		for (int actionBar = 0; actionBar < 9; ++actionBar)
		{
			addSlotToContainer(new Slot(inventoryPlayer, actionBar, 8 + actionBar * 18, 142));
		}
	}

	@Override
	public void addCraftingToCrafters(ICrafting par1ICrafting)
	{
		super.addCraftingToCrafters(par1ICrafting);
		par1ICrafting.sendProgressBarUpdate(this, 0, mortarPestle.grinderCookTime);
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();

		for (int i = 0; i < crafters.size(); ++i)
		{
			ICrafting icrafting = (ICrafting) crafters.get(i);

			if (lastGrindTime != mortarPestle.grinderCookTime)
			{
				icrafting.sendProgressBarUpdate(this, 0, mortarPestle.grinderCookTime);
			}
		}

		lastGrindTime = mortarPestle.grinderCookTime;
	}

	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int par1, int par2) {
		if (par1 == 0) { mortarPestle.grinderCookTime = par2; }
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return mortarPestle.isUseableByPlayer(player);
	}

	/** Called when a player shift-clicks on a slotIndex. You must override this or you will crash when someone does that. */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotIndex)
	{
		ItemStack itemstack = null;
		Slot slot = (Slot) inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			// In grinder inventory
			if (slotIndex < INV_START)
			{
				// merge with player inventory
				if (!mergeItemStack(itemstack1, INV_START, HOTBAR_END+1, true))
				{
					return null;
				}

				slot.onSlotChange(itemstack1, itemstack);
			}
			// not in mortar pestle inventory
			else
			{
				// merge with input slot IF itemStack is grindable
				if (mortarPestle.getGrindingResult(itemstack1) != null)
				{
					if (!mergeItemStack(itemstack1, 0, 1, false))
					{
						return null;
					}
				}
				// If slotIndex is in player inventory, not action bar or mortar pestle
				else if (slotIndex >= INV_START && slotIndex < HOTBAR_START)
				{
					if (!mergeItemStack(itemstack1, HOTBAR_START, HOTBAR_END+1, false))
					{
						return null;
					}
				}
				// If slot is in the action bar
				else if (slotIndex >= HOTBAR_START && slotIndex < HOTBAR_END+1 && !mergeItemStack(itemstack1, INV_START, HOTBAR_START, false))
				{
					return null;
				}
			}

			if (itemstack1.stackSize == 0)
			{
				slot.putStack((ItemStack) null);
			}
			else
			{
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize)
			{
				return null;
			}

			slot.onPickupFromSlot(entityPlayer, itemstack1);
		}

		return itemstack;
	}
}

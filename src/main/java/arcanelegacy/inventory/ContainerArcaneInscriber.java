package arcanelegacy.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import arcanelegacy.item.ALItems;
import arcanelegacy.tileentity.TileEntityArcaneInscriber;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerArcaneInscriber extends Container
{
	private TileEntityArcaneInscriber inscriber;
	private int lastProgressTime;
	//private int lastBurnTime;
	//private int lastItemBurnTime;
	public static final int INPUT[] = {0,1,2,3,4,5,6};
	public static final int DISCHARGE[] = {7,8,9,10,11,12,13};
	public static final int RUNE_SLOTS = INPUT.length, BLANK_SCROLL = RUNE_SLOTS*2, RECIPE = BLANK_SCROLL+1, OUTPUT = RECIPE+1,
			INV_START = OUTPUT+1, INV_END = INV_START+26, HOTBAR_START = INV_END+1, HOTBAR_END= HOTBAR_START+8;

	public ContainerArcaneInscriber(InventoryPlayer inventoryPlayer, TileEntityArcaneInscriber par2TileEntityArcaneInscriber)
	{
		int i = 0;
		this.inscriber = par2TileEntityArcaneInscriber;

		for (i = 0; i < RUNE_SLOTS; ++i) {
			addSlotToContainer(new Slot(par2TileEntityArcaneInscriber, INPUT[i], 43 + (18*i), 15));
		}
		for (i = 0; i < RUNE_SLOTS; ++i) {
			addSlotToContainer(new SlotArcaneInscriberDischarge(par2TileEntityArcaneInscriber, DISCHARGE[i], 44 + (18*i), 64));
		}

		addSlotToContainer(new Slot(par2TileEntityArcaneInscriber, BLANK_SCROLL, 63, 39));
		addSlotToContainer(new SlotArcaneInscriberRecipe(par2TileEntityArcaneInscriber, RECIPE, 17, 35));
		addSlotToContainer(new SlotArcaneInscriber(inventoryPlayer.player, par2TileEntityArcaneInscriber, OUTPUT, 119, 39));

		for (i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
		}
	}

	@Override
	public void addCraftingToCrafters(ICrafting iCrafting)
	{
		super.addCraftingToCrafters(iCrafting);
		iCrafting.sendProgressBarUpdate(this, 0, this.inscriber.inscribeProgressTime);
		// iCrafting.sendProgressBarUpdate(this, 1, this.inscriber.inscriberBurnTime);
		// iCrafting.sendProgressBarUpdate(this, 2, this.inscriber.currentItemBurnTime);
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();

		for (int i = 0; i < this.crafters.size(); ++i)
		{
			ICrafting icrafting = (ICrafting)this.crafters.get(i);

			if (this.lastProgressTime != this.inscriber.inscribeProgressTime)
			{
				icrafting.sendProgressBarUpdate(this, 0, this.inscriber.inscribeProgressTime);
			}

			/*
            if (this.lastBurnTime != this.inscriber.inscriberBurnTime)
            {
                icrafting.sendProgressBarUpdate(this, 1, this.inscriber.inscriberBurnTime);
            }

            if (this.lastItemBurnTime != this.inscriber.currentItemBurnTime)
            {
                icrafting.sendProgressBarUpdate(this, 2, this.inscriber.currentItemBurnTime);
            }
			 */
		}

		this.lastProgressTime = this.inscriber.inscribeProgressTime;
		// this.lastBurnTime = this.inscriber.inscriberBurnTime;
		// this.lastItemBurnTime = this.inscriber.currentItemBurnTime;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int par1, int par2)
	{
		if (par1 == 0)
		{
			this.inscriber.inscribeProgressTime = par2;
		}
		/*
        if (par1 == 1)
        {
            this.inscriber.inscriberBurnTime = par2;
        }

        if (par1 == 2)
        {
            this.inscriber.currentItemBurnTime = par2;
        }
		 */
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return this.inscriber.isUseableByPlayer(entityplayer);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
	{
		ItemStack itemstack = null;
		Slot slot = (Slot)this.inventorySlots.get(par2);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			// If item is in TileEntity inventory
			if (par2 < INV_START)
			{
				// try to place in player inventory / action bar
				if (!this.mergeItemStack(itemstack1, INV_START, HOTBAR_END+1, true))
				{
					return null;
				}

				slot.onSlotChange(itemstack1, itemstack);
			}
			// Item is in player inventory, try to place in inscriber
			else if (par2 > OUTPUT)
			{
				// if it is a charged rune, place in the first open input slot
				if (TileEntityArcaneInscriber.isSource(itemstack1))
				{
					if (!this.mergeItemStack(itemstack1, INPUT[0], INPUT[RUNE_SLOTS-1]+1, false))
					{
						return null;
					}
				}
				// if it's a blank scroll, place in the scroll slot
				else if (itemstack1.itemID == ALItems.scrollBlank.itemID)
				{
					if (!this.mergeItemStack(itemstack1, BLANK_SCROLL, BLANK_SCROLL+1, false))
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
				else if (par2 >= HOTBAR_START && par2 < HOTBAR_END+1 && !this.mergeItemStack(itemstack1, INV_START, HOTBAR_START, false))
				{
					return null;
				}
			}
			// In one of the inscriber slots; try to place in player inventory / action bar
			else if (!this.mergeItemStack(itemstack1, INV_START, HOTBAR_END+1, false))
			{
				return null;
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack)null);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
		}

		return itemstack;
	}
}
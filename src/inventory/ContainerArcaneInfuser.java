package coolalias.arcanelegacy.inventory;

import coolalias.arcanelegacy.item.crafting.ArcaneInfuserRecipes;
import coolalias.arcanelegacy.tileentity.TileEntityArcaneInfuser;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerArcaneInfuser extends Container
{
	private TileEntityArcaneInfuser infuser;
	
    private int lastCookTime;
    
    private int lastBurnTime;
    
    private int lastItemBurnTime;
    
    public static final int INPUT_1 = 0, INPUT_2 = 1, FUEL = 2, OUTPUT = 3, INV_START = OUTPUT+1,
			INV_END = INV_START+26, HOTBAR_START = INV_END+1, HOTBAR_END = HOTBAR_START+8;

    public ContainerArcaneInfuser(InventoryPlayer inventoryPlayer, TileEntityArcaneInfuser par2TileEntityArcaneInfuser)
    {
        this.infuser = par2TileEntityArcaneInfuser;
        this.addSlotToContainer(new Slot(par2TileEntityArcaneInfuser, INPUT_1, 21, 17));
        this.addSlotToContainer(new Slot(par2TileEntityArcaneInfuser, INPUT_2, 56, 17));
        this.addSlotToContainer(new Slot(par2TileEntityArcaneInfuser, FUEL, 56, 53));
        this.addSlotToContainer(new SlotArcaneInfuser(inventoryPlayer.player, par2TileEntityArcaneInfuser, OUTPUT, 116, 35));
        int i;

        for (i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
        }
    }

    public void addCraftingToCrafters(ICrafting iCrafting)
    {
        super.addCraftingToCrafters(iCrafting);
        iCrafting.sendProgressBarUpdate(this, 0, this.infuser.infuserCookTime);
        iCrafting.sendProgressBarUpdate(this, 1, this.infuser.infuserBurnTime);
        iCrafting.sendProgressBarUpdate(this, 2, this.infuser.currentItemBurnTime);
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int i = 0; i < this.crafters.size(); ++i)
        {
            ICrafting icrafting = (ICrafting)this.crafters.get(i);

            if (this.lastCookTime != this.infuser.infuserCookTime)
            {
                icrafting.sendProgressBarUpdate(this, 0, this.infuser.infuserCookTime);
            }

            if (this.lastBurnTime != this.infuser.infuserBurnTime)
            {
                icrafting.sendProgressBarUpdate(this, 1, this.infuser.infuserBurnTime);
            }

            if (this.lastItemBurnTime != this.infuser.currentItemBurnTime)
            {
                icrafting.sendProgressBarUpdate(this, 2, this.infuser.currentItemBurnTime);
            }
        }

        this.lastCookTime = this.infuser.infuserCookTime;
        this.lastBurnTime = this.infuser.infuserBurnTime;
        this.lastItemBurnTime = this.infuser.currentItemBurnTime;
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2)
    {
        if (par1 == 0)
        {
            this.infuser.infuserCookTime = par2;
        }

        if (par1 == 1)
        {
            this.infuser.infuserBurnTime = par2;
        }

        if (par1 == 2)
        {
            this.infuser.currentItemBurnTime = par2;
        }
    }

    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return this.infuser.isUseableByPlayer(par1EntityPlayer);
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(par2);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            // If item is in tile entity
            if (par2 < INV_START)
            {
            	// try to place in player inventory / action bar
                if (!this.mergeItemStack(itemstack1, INV_START, HOTBAR_END + 1, true))
                {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            // Item is in player inventory, try to place in infuser
            else
            {
            	// if it can be charged, place in the input slot
                if (ArcaneInfuserRecipes.infusing().getInfusingResult(itemstack1) != null)
                {
                	// try to place in either Input slot
                    if (!this.mergeItemStack(itemstack1, INPUT_1, INPUT_2+1, false))
                    {
                        return null;
                    }
                }
                // if it's an energy source, place in Fuel slot
                else if (TileEntityArcaneInfuser.isItemFuel(itemstack1))
                {
                    if (!this.mergeItemStack(itemstack1, FUEL, FUEL+1, false))
                    {
                        return null;
                    }
                }
                // item in player's inventory, but not in action bar
                else if (par2 >= INV_START && par2 < HOTBAR_START)
                {
                	// place in action bar
                    if (!this.mergeItemStack(itemstack1, HOTBAR_START, HOTBAR_START + 1, false))
                    {
                        return null;
                    }
                }
                // item in action bar - place in player inventory
                else if (par2 >= HOTBAR_START && par2 < HOTBAR_END + 1 && !this.mergeItemStack(itemstack1, INV_START, INV_END + 1, false))
                {
                    return null;
                }
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
        }
        return itemstack;
    }
}

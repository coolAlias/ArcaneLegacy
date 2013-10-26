package coolalias.arcanelegacy.inventory;

import coolalias.arcanelegacy.inventory.SlotArmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ContainerMagicBag extends Container
{
	/** The Item Inventory for this Container */
	public final InventoryMagicBag inventory;
	
	private static final int ARMOR_START = InventoryMagicBag.INV_SIZE, ARMOR_END = ARMOR_START + 3,
			INV_START = ARMOR_END+1, INV_END = INV_START+26, HOTBAR_START = INV_END+1, HOTBAR_END = HOTBAR_START+8;

	public ContainerMagicBag(EntityPlayer player, InventoryPlayer inventoryPlayer, InventoryMagicBag inventoryItem)
	{
		this.inventory = inventoryItem;
		
		int i;
		
		// CUSTOM INVENTORY SLOTS
		for (i = 0; i < InventoryMagicBag.INV_SIZE; ++i)
		{
			this.addSlotToContainer(new SlotMagicBag(this.inventory, i, 80 + (18*(i%5)), 8 + (18 * (int)(i/5))));
		}
		
		// ARMOR SLOTS
		for (i = 0; i < 4; ++i)
        {
            this.addSlotToContainer(new SlotArmor(player, inventoryPlayer, inventoryPlayer.getSizeInventory() - 1 - i, 8, 8 + i * 18, i));
        }
		
		// PLAYER INVENTORY - uses default locations for standard inventory texture file
		for (i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 9; ++j)
			{
				this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		// PLAYER ACTION BAR - uses default locations for standard action bar texture file
		for (i = 0; i < 9; ++i)
		{
			this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer)
	{
		return true;
	}
	
	/**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     * Only real change we make to this is to set needsUpdate to true at the end
     */
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(par2);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            // If item is in our custom Inventory or an ARMOR slot
            if (par2 < INV_START)
            {
            	// try to place in player inventory / action bar
                if (!this.mergeItemStack(itemstack1, INV_START, HOTBAR_END+1, true))
                {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            // Item is in inventory / hotbar, try to place in custom inventory or armor slots
            else
            {
            	// Item being shift-clicked is armor - try to put in armor slot
            	if (itemstack1.getItem() instanceof ItemArmor)
            	{
                	int type = ((ItemArmor) itemstack1.getItem()).armorType;
                	if (!this.mergeItemStack(itemstack1, ARMOR_START + type, ARMOR_START + type + 1, false))
                	{
                		return null;
                	}
                }
            	
            	// item is in inventory or action bar
            	else if (par2 >= INV_START)
            	{
            		// place in custom inventory
                    if (!this.mergeItemStack(itemstack1, 0, ARMOR_START, false))
                    {
                        return null;
                    }
            	}
            	/* NEVER CALLED
                // item is in player's inventory, but not in action bar
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
                	if (!this.mergeItemStack(itemstack1, INV_START, HOTBAR_START, false))
                	{
                		return null;
                	}
                }
                */
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

            slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
        }
        
        return itemstack;
    }
    
    /**
     * Vanilla mergeItemStack method doesn't correctly handle inventories whose
     * max stack size is 1 when you shift-click into the inventory.
     * This is a modified method I wrote to handle such cases.
     * Note you only need it if your slot / inventory's max stack size is 1
     */
    /*
    @Override
    protected boolean mergeItemStack(ItemStack par1ItemStack, int par2, int par3, boolean par4)
    {
        boolean flag1 = false;
        int k = par2;

        if (par4)
        {
            k = par3 - 1;
        }

        Slot slot;
        ItemStack itemstack1;

        if (par1ItemStack.isStackable())
        {
            while (par1ItemStack.stackSize > 0 && (!par4 && k < par3 || par4 && k >= par2))
            {
                slot = (Slot) this.inventorySlots.get(k);
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

                if (par4)
                {
                    --k;
                }
                else
                {
                    ++k;
                }
            }
        }

        if (par1ItemStack.stackSize > 0)
        {
            if (par4)
            {
                k = par3 - 1;
            }
            else
            {
                k = par2;
            }

            while (!par4 && k < par3 || par4 && k >= par2)
            {
            	slot = (Slot)this.inventorySlots.get(k);
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
                    	this.putStackInSlot(k, new ItemStack(par1ItemStack.getItem(), slot.getSlotStackLimit()));
                        par1ItemStack.stackSize -= slot.getSlotStackLimit();
                        inventory.onInventoryChanged();
                        flag1 = true;
                    }
                }

                if (par4)
                {
                    --k;
                }
                else
                {
                    ++k;
                }
            }
        }

        return flag1;
    }
    */
}

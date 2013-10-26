package coolalias.arcanelegacy.inventory;

import coolalias.arcanelegacy.item.ItemScroll;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotWand extends Slot
{
	public SlotWand(IInventory inventory, int par2, int par3, int par4)
	{
		super(inventory, par2, par3, par4);
	}
	
	/**
	 * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
	 */
	@Override
	public boolean isItemValid(ItemStack itemstack)
	{
		return itemstack.getItem() instanceof ItemScroll;
	}

}
/*
class SlotWandActive extends SlotWand
{
	public SlotWandActive(IInventory inventory, int par2, int par3, int par4)
	{
		super(inventory, par2, par3, par4);
	}
	
	@Override
	public boolean isItemValid(ItemStack itemstack)
	{
		return false;
	}
	
	/**
     * Return whether this slot's stack can be taken from this slot.
     *
	@Override
    public boolean canTakeStack(EntityPlayer par1EntityPlayer)
    {
        return false;
    }
}
*/
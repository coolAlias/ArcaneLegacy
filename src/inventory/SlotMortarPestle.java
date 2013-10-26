package coolalias.arcanelegacy.inventory;

import coolalias.arcanelegacy.tileentity.TileEntityMortarPestle;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.MathHelper;

public class SlotMortarPestle extends Slot
{
	private boolean isvalid;
	
    public SlotMortarPestle(IInventory par2IInventory, int par3, int par4, int par5, boolean isValid)
    {
        super(par2IInventory, par3, par4, par5);
        this.isvalid = isValid;
    }

    /** Check if the stack is a valid item for this slot. Always true beside for the armor slots. */
    public boolean isItemValid(ItemStack itemstack)
    {
        return this.isvalid && TileEntityMortarPestle.isItemGrindable(itemstack);
    }
}

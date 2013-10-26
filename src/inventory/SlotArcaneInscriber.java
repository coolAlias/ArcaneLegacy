package coolalias.arcanelegacy.inventory;

import coolalias.arcanelegacy.spells.SpellRecipes;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.MathHelper;

public class SlotArcaneInscriber extends Slot
{
	/** The player that is using the GUI where this slot resides. */
	private EntityPlayer thePlayer;
	private int field_75228_b;

	public SlotArcaneInscriber(EntityPlayer player, IInventory inventory, int par3, int par4, int par5)
	{
		super(inventory, par3, par4, par5);
		this.thePlayer = player;
	}

	/**
	 * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
	 */
	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return false;
	}

	/**
	 * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
	 * stack.
	 */
	@Override
	public ItemStack decrStackSize(int par1)
	{
		if (this.getHasStack()) {
			this.field_75228_b += Math.min(par1, this.getStack().stackSize);
		}

		return super.decrStackSize(par1);
	}

	@Override
	public void onPickupFromSlot(EntityPlayer player, ItemStack itemstack)
	{
		this.onCrafting(itemstack);
		super.onPickupFromSlot(player, itemstack);
	}

	/**
	 * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
	 * internal count then calls onCrafting(item).
	 */
	@Override
	protected void onCrafting(ItemStack par1ItemStack, int par2)
	{
		this.field_75228_b += par2;
		this.onCrafting(par1ItemStack);
	}

	/**
	 * The itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
	 */
	@Override
	protected void onCrafting(ItemStack par1ItemStack)
	{
		par1ItemStack.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.field_75228_b);

		if (!this.thePlayer.worldObj.isRemote)
		{
			int i = this.field_75228_b;
			float f = SpellRecipes.spells().getExperience(par1ItemStack);
			int j;

			if (f == 0.0F)
			{
				i = 0;
			}
			else if (f < 1.0F)
			{
				j = MathHelper.floor_float((float)i * f);

				if (j < MathHelper.ceiling_float_int((float)i * f) && (float)Math.random() < (float)i * f - (float)j)
				{
					++j;
				}

				i = j;
			}

			while (i > 0)
			{
				j = EntityXPOrb.getXPSplit(i);
				i -= j;
				this.thePlayer.worldObj.spawnEntityInWorld(new EntityXPOrb(this.thePlayer.worldObj, this.thePlayer.posX, this.thePlayer.posY + 0.5D, this.thePlayer.posZ + 0.5D, j));
			}
		}

		this.field_75228_b = 0;
	}
}

class SlotArcaneInscriberDischarge extends Slot
{
	private int field_75228_b;

	public SlotArcaneInscriberDischarge(IInventory inventory, int par3, int par4, int par5) {
		super(inventory, par3, par4, par5);
	}

	/**
	 * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
	 */
	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return false;
	}

	/**
	 * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
	 * stack.
	 */
	public ItemStack decrStackSize(int par1)
	{
		if (this.getHasStack()) {
			this.field_75228_b += Math.min(par1, this.getStack().stackSize);
		}

		return super.decrStackSize(par1);
	}
}

class SlotArcaneInscriberRecipe extends Slot
{
	private int field_75228_b;

	public SlotArcaneInscriberRecipe(IInventory inventory, int par3, int par4, int par5) {
		super(inventory, par3, par4, par5);
	}

	/**
	 * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
	 */
	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return false;
	}
	
	/**
     * Return whether this slot's stack can be taken from this slot.
     */
	@Override
    public boolean canTakeStack(EntityPlayer player) {
        return false;
    }

	/**
	 * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
	 * stack.
	 */
	@Override
	public ItemStack decrStackSize(int par1)
	{
		if (this.getHasStack()) {
			this.field_75228_b += Math.min(par1, this.getStack().stackSize);
		}

		return super.decrStackSize(par1);
	}

	@Override
	public void onPickupFromSlot(EntityPlayer player, ItemStack itemstack) {}
}


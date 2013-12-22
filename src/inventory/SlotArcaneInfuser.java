package coolalias.arcanelegacy.inventory;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import coolalias.arcanelegacy.item.crafting.ArcaneInfuserRecipes;

public class SlotArcaneInfuser extends Slot
{
	/** The player that is using the GUI where this slot resides. */
	private EntityPlayer thePlayer;

	private int amount_crafted;

	public SlotArcaneInfuser(EntityPlayer player, IInventory inv, int index, int xPos, int yPos) {
		super(inv, index, xPos, yPos);
		this.thePlayer = player;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return false;
	}

	@Override
	public ItemStack decrStackSize(int amount)
	{
		if (this.getHasStack()) {
			this.amount_crafted += Math.min(amount, this.getStack().stackSize);
		}

		return super.decrStackSize(amount);
	}

	public void onPickupFromSlot(EntityPlayer player, ItemStack itemstack) {
		this.onCrafting(itemstack);
		super.onPickupFromSlot(player, itemstack);
	}

	@Override
	protected void onCrafting(ItemStack itemstack, int amount) {
		this.amount_crafted += amount;
		this.onCrafting(itemstack);
	}

	@Override
	protected void onCrafting(ItemStack itemstack)
	{
		itemstack.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.amount_crafted);

		if (!this.thePlayer.worldObj.isRemote)
		{
			int i = this.amount_crafted;
			float f = ArcaneInfuserRecipes.infusing().getExperience(itemstack);
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

		this.amount_crafted = 0;
	}
}
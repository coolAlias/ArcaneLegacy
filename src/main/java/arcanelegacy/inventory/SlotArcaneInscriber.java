package arcanelegacy.inventory;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import arcanelegacy.spells.SpellRecipes;

public class SlotArcaneInscriber extends Slot
{
	/** The player that is using the GUI where this slot resides. */
	private EntityPlayer thePlayer;

	private int amount_crafted;

	public SlotArcaneInscriber(EntityPlayer player, IInventory inventory, int index, int xPos, int yPos) {
		super(inventory, index, xPos, yPos);
		thePlayer = player;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return false;
	}

	@Override
	public ItemStack decrStackSize(int amount) {
		if (getHasStack()) {
			amount_crafted += Math.min(amount, getStack().stackSize);
		}
		return super.decrStackSize(amount);
	}

	@Override
	public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
		onCrafting(stack);
		super.onPickupFromSlot(player, stack);
	}

	@Override
	protected void onCrafting(ItemStack stack, int amount) {
		amount_crafted += amount;
		onCrafting(stack);
	}

	@Override
	protected void onCrafting(ItemStack stack) {
		stack.onCrafting(thePlayer.worldObj, thePlayer, amount_crafted);
		if (!thePlayer.worldObj.isRemote) {
			int i = amount_crafted;
			float f = SpellRecipes.spells().getExperience(stack);
			int j;

			if (f == 0.0F) {
				i = 0;
			} else if (f < 1.0F) {
				j = MathHelper.floor_float(i * f);
				if (j < MathHelper.ceiling_float_int(i * f) && Math.random() < i * f - j) {
					++j;
				}
				i = j;
			}

			while (i > 0) {
				j = EntityXPOrb.getXPSplit(i);
				i -= j;
				thePlayer.worldObj.spawnEntityInWorld(new EntityXPOrb(thePlayer.worldObj, thePlayer.posX, thePlayer.posY + 0.5D, thePlayer.posZ + 0.5D, j));
			}
		}

		amount_crafted = 0;
	}
}

class SlotArcaneInscriberDischarge extends Slot {
	public SlotArcaneInscriberDischarge(IInventory inventory, int index, int xPos, int yPos) {
		super(inventory, index, xPos, yPos);
	}

	@Override
	public boolean isItemValid(ItemStack stack) { return false; }
}

class SlotArcaneInscriberRecipe extends Slot {
	public SlotArcaneInscriberRecipe(IInventory inventory, int index, int xPos, int yPos) {
		super(inventory, index, xPos, yPos);
	}

	@Override
	public boolean isItemValid(ItemStack stack) { return false; }

	@Override
	public boolean canTakeStack(EntityPlayer player) { return false; }
}
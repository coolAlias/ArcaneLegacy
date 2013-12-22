package coolalias.arcanelegacy.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import coolalias.arcanelegacy.item.ItemScroll;

public class SlotWand extends Slot
{
	public SlotWand(IInventory inventory, int index, int xPos, int yPos) {
		super(inventory, index, xPos, yPos);
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return itemstack.getItem() instanceof ItemScroll;
	}
}
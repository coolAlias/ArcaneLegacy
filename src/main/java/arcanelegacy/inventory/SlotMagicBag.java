package arcanelegacy.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import arcanelegacy.item.ItemMagicBag;

public class SlotMagicBag extends Slot
{
	public SlotMagicBag(IInventory inv, int index, int xPos, int yPos) {
		super(inv, index, xPos, yPos);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return !(stack.getItem() instanceof ItemMagicBag);
	}
}
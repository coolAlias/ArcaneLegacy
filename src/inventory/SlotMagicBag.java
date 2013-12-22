package coolalias.arcanelegacy.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import coolalias.arcanelegacy.item.ItemMagicBag;

public class SlotMagicBag extends Slot
{
	public SlotMagicBag(IInventory inv, int index, int xPos, int yPos) {
		super(inv, index, xPos, yPos);
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return !(itemstack.getItem() instanceof ItemMagicBag);
	}
}
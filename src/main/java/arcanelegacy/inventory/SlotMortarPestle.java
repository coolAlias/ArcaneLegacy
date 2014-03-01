package arcanelegacy.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import arcanelegacy.tileentity.TileEntityMortarPestle;

public class SlotMortarPestle extends Slot
{
	/** True if items may be placed in this slot; false if it only receives items (i.e. output) */
	private boolean isValid;

	public SlotMortarPestle(IInventory inv, int index, int xPos, int yPos, boolean isValid) {
		super(inv, index, xPos, yPos);
		this.isValid = isValid;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return this.isValid && TileEntityMortarPestle.isItemGrindable(stack);
	}
}
package arcanelegacy.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SlotArmor extends Slot
{
	/** The armor type that can be placed on that slot, it uses the same values of armorType field on ItemArmor. */
	private final int armorType;

	/** The parent class of this slot, ContainerPlayer, SlotArmor is a Anon inner class. */
	private final EntityPlayer player;

	public SlotArmor(EntityPlayer player, IInventory inventory, int index, int xPos, int yPos, int armorType) {
		super(inventory, index, xPos, yPos);
		this.player = player;
		this.armorType = armorType;
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		Item item = (stack == null ? null : stack.getItem());
		return item != null && item.isValidArmor(stack, armorType, player);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getBackgroundIconIndex() {
		return ItemArmor.func_94602_b(armorType);
	}
}
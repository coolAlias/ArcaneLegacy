package arcanelegacy.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import arcanelegacy.ArcaneLegacy;
import arcanelegacy.inventory.InventoryMagicBag;

public class ItemMagicBag extends BaseModItem
{
	public ItemMagicBag(int par1) {
		super(par1);
		this.maxStackSize = 1;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) { return 1; }

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			if (!player.isSneaking()) {
				player.openGui(ArcaneLegacy.instance, ArcaneLegacy.magicBagGuiId, world, (int) player.posX, (int) player.posY, (int) player.posZ);
			} else {
				new InventoryMagicBag(player.getHeldItem()).setInventorySlotContents(0, new ItemStack(Item.diamond,4));
			}
		}

		return itemstack;
	}

	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean par4) {
		list.add(EnumChatFormatting.ITALIC + "A magic bag that holds many items");
	}
}
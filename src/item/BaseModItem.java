package coolalias.arcanelegacy.item;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import coolalias.arcanelegacy.ModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BaseModItem extends Item
{
	public BaseModItem(int par1)
	{
		super(par1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister)
	{
		this.itemIcon = iconRegister.registerIcon(ModInfo.ID + ":" + this.getUnlocalizedName().substring(5).toLowerCase());
	}
}

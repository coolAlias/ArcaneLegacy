package coolalias.arcanelegacy.blocks;

import coolalias.arcanelegacy.ArcaneLegacy;
import coolalias.arcanelegacy.ModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;

/**
 * This class sets the creative tab and registers the block icon
 */
public class BlockGeneric extends Block
{
	public BlockGeneric(int par1, Material par2Material)
	{
		super(par1, par2Material);
		setCreativeTab(ArcaneLegacy.tabArcaneBlocks);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister)
	{
		String name = this.getUnlocalizedName().substring(5);
		if (name.contains("_Ticking"))
			name = name.substring(0, name.indexOf("_Ticking"));
		this.blockIcon = par1IconRegister.registerIcon(ModInfo.ID + ":" + name.toLowerCase());
	}
}

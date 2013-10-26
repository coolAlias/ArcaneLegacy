package coolalias.arcanelegacy.tabs;

import coolalias.arcanelegacy.blocks.ALBlocks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;

public class TabArcaneBlocks extends CreativeTabs {

	public TabArcaneBlocks(String label) {
		super(label);
		// TODO Auto-generated constructor stub
	}

	public TabArcaneBlocks(int par1, String par2Str) {
		super(par1, par2Str);
		// TODO Auto-generated constructor stub
	}

	// How would I make this get the iconFromDamage?
	@SideOnly(Side.CLIENT)
	public int getTabIconItemIndex()
	{
		return ALBlocks.blockSpirit.blockID;
	}

	public String getTranslatedTabLabel()
	{
		return "Blocks";
	}

}

package arcanelegacy.tabs;

import net.minecraft.creativetab.CreativeTabs;
import arcanelegacy.blocks.ALBlocks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TabArcaneBlocks extends CreativeTabs {

	public TabArcaneBlocks(String label) {
		super(label);
	}

	public TabArcaneBlocks(int par1, String par2Str) {
		super(par1, par2Str);
	}

	// How would I make this get the iconFromDamage?
	@SideOnly(Side.CLIENT)
	public int getTabIconItemIndex() {
		return ALBlocks.blockSpirit.blockID;
	}

	public String getTranslatedTabLabel() {
		return "Blocks";
	}
}

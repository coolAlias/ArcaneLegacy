package arcanelegacy.tabs;

import net.minecraft.creativetab.CreativeTabs;
import arcanelegacy.item.ALItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TabArcanecScrolls extends CreativeTabs {

	public TabArcanecScrolls(String label) {
		super(label);
	}

	public TabArcanecScrolls(int par1, String par2Str) {
		super(par1, par2Str);
	}
	
	@SideOnly(Side.CLIENT)
	public int getTabIconItemIndex() {
		return ALItems.scrollBlank.itemID;
	}

	public String getTranslatedTabLabel() {
		return "Magic - Scrolls";
	}
}

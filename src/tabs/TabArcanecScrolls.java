package coolalias.arcanelegacy.tabs;

import coolalias.arcanelegacy.item.ALItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;

public class TabArcanecScrolls extends CreativeTabs {

	public TabArcanecScrolls(String label) {
		super(label);
		// TODO Auto-generated constructor stub
	}

	public TabArcanecScrolls(int par1, String par2Str) {
		super(par1, par2Str);
		// TODO Auto-generated constructor stub
	}
	
	@SideOnly(Side.CLIENT)
	public int getTabIconItemIndex()
	{
		return ALItems.scrollBlank.itemID;
	}

	public String getTranslatedTabLabel()
	{
		return "Magic - Scrolls";
	}
}

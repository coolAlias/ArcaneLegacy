package coolalias.arcanelegacy.tabs;

import coolalias.arcanelegacy.item.ALItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;

public class TabArcaneRunes extends CreativeTabs {

	public TabArcaneRunes(String label) {
		super(label);
		// TODO Auto-generated constructor stub
	}

	public TabArcaneRunes(int par1, String par2Str) {
		super(par1, par2Str);
		// TODO Auto-generated constructor stub
	}

	// How would I make this get the iconFromDamage?
	@SideOnly(Side.CLIENT)
	public int getTabIconItemIndex()
	{
		return ALItems.runeBasic.itemID;
	}

	public String getTranslatedTabLabel()
	{
		return "Runes";
	}

}

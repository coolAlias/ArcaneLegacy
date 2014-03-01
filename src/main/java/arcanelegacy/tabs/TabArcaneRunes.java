package arcanelegacy.tabs;

import net.minecraft.creativetab.CreativeTabs;
import arcanelegacy.item.ALItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TabArcaneRunes extends CreativeTabs {

	public TabArcaneRunes(String label) {
		super(label);
	}

	public TabArcaneRunes(int par1, String par2Str) {
		super(par1, par2Str);
	}

	// How would I make this get the iconFromDamage?
	@SideOnly(Side.CLIENT)
	public int getTabIconItemIndex() {
		return ALItems.runeBasic.itemID;
	}

	public String getTranslatedTabLabel() {
		return "Runes";
	}
}

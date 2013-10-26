package coolalias.arcanelegacy.item;

import java.util.List;

import coolalias.arcanelegacy.ArcaneLegacy;
import coolalias.arcanelegacy.ModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;

public class ItemRune extends Item
{
	/**
	 * Define constant names for each sub-type:
	 * ItemStack runeType = new ItemStack(MagicMod.runeBasic, 1, RUNE_TYPE); 
	 */
	public static final int RUNE_BASE = 0, RUNE_CREATE = 1, RUNE_NEGATE = 2, RUNE_LIFE = 3,
			RUNE_DEATH = 4, RUNE_LIGHT = 5, RUNE_DARK = 6, RUNE_WIND = 7, RUNE_EARTH = 8,
			RUNE_FIRE = 9, RUNE_ICE = 10, RUNE_LIGHTNING = 11, RUNE_ARROW = 12,
			RUNE_AUGMENT = 13, RUNE_DIMINISH = 14, RUNE_TIME = 15, RUNE_SPACE = 16,
			RUNE_MOVE = 17, RUNE_PROTECT = 18, RUNE_POISON = 19, RUNE_MAGIC = 20;

	public static final String[] RUNE_NAME = new String[] {"Blank", "Create", "Negate",
		"Life", "Death", "Light", "Dark", "Wind", "Earth", "Fire", "Ice", "Lightning",
		"Arrow", "Augment", "Diminish", "Time", "Space", "Movement", "Protection",
		"Poison", "Magic"};

	private boolean charge;

	@SideOnly(Side.CLIENT)
	private Icon[] iconArray;

	public ItemRune(int par1)
	{
		super(par1);
		this.setCreativeTab(ArcaneLegacy.tabArcaneRunes);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.charge = false;
	}

	public boolean isCharged() {
		return this.charge;
	}

	public ItemRune setCharge(boolean charge) {
		this.charge = charge;
		return this;
	}

	@SideOnly(Side.CLIENT)
	public Icon getIconFromDamage(int par1)
	{
		/*
		 * clamp_int returns an int between the values of par2 and par3, inclusively
		 * so par3 = number of icons - 1
		 */
		int j = MathHelper.clamp_int(par1, 0, RUNE_NAME.length - 1);
		return this.iconArray[j];
	}

	/**
	 * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
	 * different names based on their damage or NBT.
	 */
	public String getUnlocalizedName(ItemStack par1ItemStack)
	{
		int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, RUNE_NAME.length - 1);
		return super.getUnlocalizedName().substring(5) + "." + RUNE_NAME[i];
	}

	/**
	 * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
	 */
	@SideOnly(Side.CLIENT)
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int j = 0; j < RUNE_NAME.length; ++j)
		{
			par3List.add(new ItemStack(par1, 1, j));
		}
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.iconArray = new Icon[RUNE_NAME.length];

		for (int i = 0; i < RUNE_NAME.length; ++i)
		{
			this.iconArray[i] = par1IconRegister.registerIcon(ModInfo.ID + ":rune_" + RUNE_NAME[i].toLowerCase());
		}
	}

	/**
	 * Gives 'enchanted' glow effect
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack par1ItemStack, int pass)
	{
		return this.charge;
	}
}

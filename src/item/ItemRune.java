package coolalias.arcanelegacy.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import coolalias.arcanelegacy.ArcaneLegacy;
import coolalias.arcanelegacy.ModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemRune extends Item
{
	/** Types of runes */
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

	public ItemRune(int index) {
		super(index);
		this.setCreativeTab(ArcaneLegacy.tabArcaneRunes);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.charge = false;
	}

	/**
	 * Returns whether the rune is charged or not
	 */
	public boolean isCharged() { return this.charge; }

	public ItemRune setCharge(boolean charge) {
		this.charge = charge;
		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIconFromDamage(int index) {
		return iconArray[MathHelper.clamp_int(index, 0, RUNE_NAME.length - 1)];
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName().substring(5) + "." + RUNE_NAME[MathHelper.clamp_int(stack.getItemDamage(), 0, RUNE_NAME.length - 1)];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int index, CreativeTabs tab, List list) {
		for (int i = 0; i < RUNE_NAME.length; ++i) {
			list.add(new ItemStack(index, 1, i));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister register) {
		iconArray = new Icon[RUNE_NAME.length];
		for (int i = 0; i < RUNE_NAME.length; ++i) {
			iconArray[i] = register.registerIcon(ModInfo.ID + ":rune_" + RUNE_NAME[i].toLowerCase());
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack itemstack, int pass) {
		return this.charge;
	}
}
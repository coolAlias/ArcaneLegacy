package arcanelegacy.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import arcanelegacy.ArcaneLegacy;
import arcanelegacy.ModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemDust extends Item
{
	/** Types of dust */
	public static final int DUST_SPIRIT = 0, DUST_DIAMOND = 1, DUST_EMERALD = 2,
			DUST_GOLD = 3, DUST_NETHERRACK = 4, DUST_QUARTZ = 5;

	/** Used to localized names as well as store number of dust types (i.e. String.length). */
	public static final String[] DUST_NAMES = new String[] {"Spirit","Diamond", "Emerald", "Gold", "Netherrack", "Quartz"};

	@SideOnly(Side.CLIENT)
	private Icon[] iconArray;

	public ItemDust(int par1) {
		super(par1);
		this.setCreativeTab(ArcaneLegacy.tabArcaneRunes);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIconFromDamage(int par1) {
		int j = MathHelper.clamp_int(par1, 0, DUST_NAMES.length - 1);
		return this.iconArray[j];
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		int i = MathHelper.clamp_int(itemstack.getItemDamage(), 0, DUST_NAMES.length - 1);
		return super.getUnlocalizedName().substring(5) + "." + DUST_NAMES[i];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int itemID, CreativeTabs tab, List list) {
		for (int i = 0; i < DUST_NAMES.length; ++i) {
			list.add(new ItemStack(itemID, 1, i));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister register) {
		iconArray = new Icon[DUST_NAMES.length];
		for (int i = 0; i < DUST_NAMES.length; ++i) {
			iconArray[i] = register.registerIcon(ModInfo.ID + ":dust_" + DUST_NAMES[i].toLowerCase());
		}
	}

	@Override
	public void addInformation(ItemStack itemstack,	EntityPlayer player, List list, boolean par4) {
		list.add(EnumChatFormatting.ITALIC + "This seems to have magical properties");
	}
}
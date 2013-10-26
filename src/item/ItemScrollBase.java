package coolalias.arcanelegacy.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import coolalias.arcanelegacy.ArcaneLegacy;
import coolalias.arcanelegacy.spells.SpellDescription;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemScrollBase extends BaseModItem
{
	public ItemScrollBase(int par1)
	{
		super(par1);
		// this.setHasSubtypes(true);	// Damage variable used as effect level
		this.setCreativeTab(ArcaneLegacy.tabArcaneScrolls);
	}

	/**
	 * Returns the action that specifies what animation to play when the items is being used
	 */
	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		return EnumAction.block;
	}

	/**
	 * How long it takes to use or consume an item
	 */
	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) {
		return 72000;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack scroll, World world, EntityPlayer player)
	{
		player.setItemInUse(scroll, this.getMaxItemUseDuration(scroll));
		return scroll;
	}

	/**
	 * Allows items to add custom lines of information to the mouseover description
	 */
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean par4)
	{
		String description = (SpellDescription.descriptions().getSpellDescription(itemstack) != null ? SpellDescription.descriptions().getSpellDescription(itemstack) : "No description");
		list.add(EnumChatFormatting.ITALIC + description);

		if (itemstack.itemID != ALItems.scrollBlank.itemID)
		{
			String duration = durationToString(itemstack);
			//String toolTip = String.format(description + "%nCast Time: " + ((ItemScroll) itemstack.getItem()).getCastTime());

			if (((ItemScroll) itemstack.getItem()).getCastTime() > 0)
			{
				list.add(EnumChatFormatting.BLUE + "Cast Time: " + EnumChatFormatting.RESET + ((ItemScroll) itemstack.getItem()).getCastTime());
			}
			if (((ItemScroll) itemstack.getItem()).getDuration() != 0)
			{
				list.add(EnumChatFormatting.GOLD + "Duration: " + EnumChatFormatting.RESET  + duration);
			}
			if (((ItemScroll) itemstack.getItem()).getScrollDamage() != 0)
			{
				list.add(EnumChatFormatting.RED + "Damage: " + EnumChatFormatting.RESET  + ((ItemScroll) itemstack.getItem()).getScrollDamage());
			}
		}
	}

	/**
	 * Returns duration as a string to display for the tooltip on mouseover
	 */
	private String durationToString(ItemStack itemstack)
	{
		String toDisplay;
		int dur = ((ItemScroll)itemstack.getItem()).getDuration();
		switch(dur)
		{
		case -1:
			toDisplay = "Permanent";
			break;
		case 1:
			toDisplay = "Instant";
			break;
		default:
			toDisplay = ("" + (dur > 19 ? dur / 20 + " seconds" : dur + " ticks"));
			break;
		}
		return toDisplay;
	}

	/** Gives 'enchanted' glow effect */
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack itemstack, int pass)
	{
		return (itemstack.itemID == ALItems.scrollBlank.itemID ? false : true);
	}
}

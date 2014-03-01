package arcanelegacy.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import arcanelegacy.ArcaneLegacy;
import arcanelegacy.spells.SpellDescription;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemScrollBase extends BaseModItem
{
	public ItemScrollBase(int par1) {
		super(par1);
		// this.setHasSubtypes(true);	// Damage variable used as effect level
		this.setCreativeTab(ArcaneLegacy.tabArcaneScrolls);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		return EnumAction.block;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) {
		return 72000;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack scroll, World world, EntityPlayer player) {
		player.setItemInUse(scroll, this.getMaxItemUseDuration(scroll));
		return scroll;
	}

	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean par4)
	{
		String description = (SpellDescription.descriptions().getSpellDescription(itemstack) != null ? SpellDescription.descriptions().getSpellDescription(itemstack) : "No description");
		list.add(EnumChatFormatting.ITALIC + description);

		if (itemstack.itemID != ALItems.scrollBlank.itemID)
		{
			String duration = durationToString(itemstack);
			//String toolTip = String.format(description + "%nCast Time: " + ((ItemScroll) itemstack.getItem()).getCastTime());

			if (((ItemScroll) itemstack.getItem()).getCastTime() > 0) {
				list.add(EnumChatFormatting.BLUE + "Cast Time: " + EnumChatFormatting.RESET + ((ItemScroll) itemstack.getItem()).getCastTime());
			}

			if (((ItemScroll) itemstack.getItem()).getDuration() != 0) {
				list.add(EnumChatFormatting.GOLD + "Duration: " + EnumChatFormatting.RESET  + duration);
			}

			if (((ItemScroll) itemstack.getItem()).getScrollDamage() != 0) {
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

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack itemstack, int pass) {
		return (itemstack.itemID == ALItems.scrollBlank.itemID ? false : true);
	}
}
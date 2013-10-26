package coolalias.arcanelegacy.spells;

import java.util.HashMap;

import coolalias.arcanelegacy.Config;
import coolalias.arcanelegacy.item.ALItems;
import coolalias.arcanelegacy.item.ItemScroll;
import net.minecraft.item.ItemStack;

public class SpellDescription
{
	private static final SpellDescription spellDescription = new SpellDescription();

	private HashMap<Integer, String> description = new HashMap<Integer, String>();

	/** Returns instance of class */
	public static final SpellDescription descriptions() {
		return spellDescription;
	}

	private SpellDescription()
	{
		/*
		 * SPELLS TO ADD
		 * 
		 */
		if (Config.enableAuraSpells()) {
			addSpellDescription(ALItems.scrollFireAura.itemID, "Burns everything that comes near");
			addSpellDescription(ALItems.scrollFreezeAuraI.itemID, "Freeze everything that comes near");
			addSpellDescription(ALItems.scrollGrowthIII.itemID, "Every step increases the bounty of nature");
			addSpellDescription(ALItems.scrollHarmAuraI.itemID, "Continually harms all in close proximity");
			addSpellDescription(ALItems.scrollHealAuraI.itemID, "Continually heals all in close proximity");
			addSpellDescription(ALItems.scrollRepulsionField.itemID, "Prevents creatures from coming near");
			addSpellDescription(ALItems.scrollWiltII.itemID, "Continually wilts all plants around caster");
			addSpellDescription(ALItems.scrollNullificationField.itemID, "Creates a teleportation-disrupting field");
		}
		if (Config.enableImprisonSpells()) {
			addSpellDescription(ALItems.scrollImprisonI.itemID, "Create prison of earth around target");
			addSpellDescription(ALItems.scrollImprisonII.itemID, "Create prison of stone around target");
			addSpellDescription(ALItems.scrollImprisonIII.itemID, "Crush target under mass of stone");
		}
		if (Config.enableTickingBlocks()) {
			addSpellDescription(ALItems.scrollDarknessI.itemID, "Create a temporary darkness");
			addSpellDescription(ALItems.scrollLightI.itemID, "");
		}
		addSpellDescription(ALItems.scrollBlank.itemID, "A blank scroll");
		addSpellDescription(ALItems.scrollBanish.itemID, "Banish a single mob or animal");
		addSpellDescription(ALItems.scrollBanishMass.itemID, "Banish a group of mobs or animals");
		addSpellDescription(ALItems.scrollBlink.itemID, "Randomly teleport out of harm's way");
		addSpellDescription(ALItems.scrollCharm.itemID, "Chance of taming a single animal");
		addSpellDescription(ALItems.scrollCharmMass.itemID, "Chance of taming all animals in area");
		addSpellDescription(ALItems.scrollCombust.itemID, "Ignite a block or mob");
		addSpellDescription(ALItems.scrollCombustArea.itemID, "Ignites everything in a large area");
		addSpellDescription(ALItems.scrollDarknessII.itemID, "Create permanent darkness");
		addSpellDescription(ALItems.scrollDispel.itemID, "Dispels all magical effects in area");
		addSpellDescription(ALItems.scrollEgress.itemID, "Return to last bed slept in within the same dimension");
		addSpellDescription(ALItems.scrollFeatherFall.itemID, "Slows rate of falling");
		addSpellDescription(ALItems.scrollFireArrow.itemID, "Shoots a flaming arrow at target");
		addSpellDescription(ALItems.scrollFireball.itemID, "Shoots a giant ball of fire");
		addSpellDescription(ALItems.scrollFreezeI.itemID, "Freeze blocks or enemies");
		addSpellDescription(ALItems.scrollFreezeII.itemID, "Freeze blocks or enemies");
		addSpellDescription(ALItems.scrollFreezeBoltI.itemID, "Shoots a freezing bolt");
		addSpellDescription(ALItems.scrollFreezeBoltII.itemID, "Shoots a freezing bolt");
		addSpellDescription(ALItems.scrollFly.itemID, "Grants ability to fly for a short time");
		addSpellDescription(ALItems.scrollFlyExtended.itemID, "Grants ability to fly for a long time");
		addSpellDescription(ALItems.scrollGrowthI.itemID, "Increase nature's bounty in a small area");
		addSpellDescription(ALItems.scrollGrowthII.itemID, "Increase nature's bounty in a large area");
		addSpellDescription(ALItems.scrollGustI.itemID, "A gust of wind knocks target back");
		addSpellDescription(ALItems.scrollGustII.itemID, "A strong gust of wind knocks target back");
		addSpellDescription(ALItems.scrollGustIII.itemID, "A powerful gust of wind blows target away");
		addSpellDescription(ALItems.scrollHarmI.itemID, "");
		addSpellDescription(ALItems.scrollHarmII.itemID, "");
		addSpellDescription(ALItems.scrollHarmMassI.itemID, "");
		addSpellDescription(ALItems.scrollHarmMassII.itemID, "");
		addSpellDescription(ALItems.scrollHarmRangedI.itemID, "");
		addSpellDescription(ALItems.scrollHarmRangedII.itemID, "");
		addSpellDescription(ALItems.scrollHarmRangedMassI.itemID, "");
		addSpellDescription(ALItems.scrollHarmRangedMassII.itemID, "");
		addSpellDescription(ALItems.scrollHealI.itemID, "");
		addSpellDescription(ALItems.scrollHealII.itemID, "");
		addSpellDescription(ALItems.scrollHealMassI.itemID, "");
		addSpellDescription(ALItems.scrollHealMassII.itemID, "");
		addSpellDescription(ALItems.scrollHealRangedI.itemID, "");
		addSpellDescription(ALItems.scrollHealRangedII.itemID, "");
		addSpellDescription(ALItems.scrollHealRangedMassI.itemID, "");
		addSpellDescription(ALItems.scrollHealRangedMassII.itemID, "");
		addSpellDescription(ALItems.scrollJumpI.itemID, "Slightly increases jump height");
		addSpellDescription(ALItems.scrollJumpII.itemID, "Greatly increases jump height");
		addSpellDescription(ALItems.scrollLightII.itemID, "");
		addSpellDescription(ALItems.scrollNullifyTeleportI.itemID, "Prevents target from teleporting");
		addSpellDescription(ALItems.scrollProtectMagicI.itemID, "Grants some protection against magic");
		addSpellDescription(ALItems.scrollResurrectI.itemID, "Prevents death and restores a little health");
		addSpellDescription(ALItems.scrollResurrectII.itemID, "Prevents death and restores moderate health");
		addSpellDescription(ALItems.scrollShockwaveI.itemID, "Knocks away all around and causes slight damage");
		addSpellDescription(ALItems.scrollShockwaveII.itemID, "Knocks away all around and causes damage");
		addSpellDescription(ALItems.scrollSpeedI.itemID, "Slightly increases speed");
		addSpellDescription(ALItems.scrollSpeedII.itemID, "Greatly increases speed");
		addSpellDescription(ALItems.scrollSpiderClimb.itemID, "Grants ability to climb up walls");
		addSpellDescription(ALItems.scrollSummonPig.itemID, "Great... a pig.");
		addSpellDescription(ALItems.scrollSummonSkeletonI.itemID, "Summons a skeleton minion");
		addSpellDescription(ALItems.scrollSummonSkeletonII.itemID, "Summons two skeleton minions");
		addSpellDescription(ALItems.scrollSummonSkeletonIII.itemID, "Summons three skeleton minions");
		addSpellDescription(ALItems.scrollTeleportI.itemID, "Teleport a short distance");
		addSpellDescription(ALItems.scrollTeleportII.itemID, "Teleport a long distance");
		addSpellDescription(ALItems.scrollTeleportIII.itemID, "Teleport through solid objects");
		addSpellDescription(ALItems.scrollTestMultiEffects.itemID, "");
		addSpellDescription(ALItems.scrollWeb.itemID, "Affected area covered in magical webs");
		addSpellDescription(ALItems.scrollWebRanged.itemID, "Shoot magical webs at target");
		addSpellDescription(ALItems.scrollWiltI.itemID, "Wilts all plants in area touched");
	}
	
	/** Adds description of item to map */
	public void addSpellDescription(int itemid, String desc) {
		description.put(Integer.valueOf(itemid), desc);
	}
	
	/** Returns the description of a spell from ItemStack */
	public String getSpellDescription(ItemStack scroll) {
		return description.get(Integer.valueOf(scroll.itemID));	
	}
}

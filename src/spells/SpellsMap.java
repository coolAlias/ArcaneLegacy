package coolalias.arcanelegacy.spells;

import java.util.HashMap;
import java.util.Map;

import coolalias.arcanelegacy.Config;
import coolalias.arcanelegacy.item.ALItems;
import coolalias.arcanelegacy.item.ItemScroll;

/**
 * IMPORTANT: Every scroll added MUST be entered into this map or the game WILL crash!!!
 * Can use SpellsMap.instance().getSpell(scroll itemID) to get correct ItemScroll returned
 */
public class SpellsMap
{
	private static final SpellsMap instance = new SpellsMap();
	
	/** A mapping of itemID to ItemScroll */
	private Map<Integer, ItemScroll> spellmap = new HashMap<Integer, ItemScroll>();
	
	/** A map used by aura spells to return an alternate id for adding spell effects to affected targets */
	private Map<Integer, Integer> auramap = new HashMap<Integer, Integer>();
	
	/**
	 * Returns instance of class
	 */
	public static final SpellsMap instance() {
		return instance;
	}
	
	/**
	 * Returns the ItemScroll for itemID provided. Will offset internally by ArcaneLegacy.SCROLL_INDEX_START
	 */
	public final ItemScroll getSpell(int id) {
		return spellmap.get(Integer.valueOf(id - Config.scrollStartIndex()));
	}
	
	/**
	 * Places an ItemScroll into the Spells Map, offset by ArcaneLegacy.SCROLL_INDEX_START
	 */
	public final void putScroll(ItemScroll scroll) {
		spellmap.put(Integer.valueOf(scroll.itemID - Config.scrollStartIndex()), scroll);
	}
	
	/**
	 * Adds a special result id for the aura of id given
	 */
	public final void addAuraResult(int id, int result) {
		auramap.put(id, result);
	}
	
	/**
	 * Returns the id of scroll to use when applying aura's effect during tick updates
	 */
	public final int getAuraResult(int id) {
		return auramap.get(id);
	}
	
	/**
	 * Returns true if the id given has a different id result
	 */
	public final boolean hasAuraResult(int id) {
		return auramap.containsKey(id);
	}
	
	private SpellsMap()
	{
		if (Config.enableAuraSpells()) {
			putScroll((ItemScroll) ALItems.scrollFireAura);
			putScroll((ItemScroll) ALItems.scrollFreezeAuraI);
			putScroll((ItemScroll) ALItems.scrollGrowthIII);
			putScroll((ItemScroll) ALItems.scrollHarmAuraI);
			putScroll((ItemScroll) ALItems.scrollHealAuraI);
			putScroll((ItemScroll) ALItems.scrollRepulsionField);
			putScroll((ItemScroll) ALItems.scrollWiltII);
			putScroll((ItemScroll) ALItems.scrollNullificationField);
			addAuraResult(ALItems.scrollNullificationField.itemID, ALItems.scrollNullifyTeleportI.itemID);
		}
		if (Config.enableImprisonSpells()) {
			putScroll((ItemScroll) ALItems.scrollImprisonI);
			putScroll((ItemScroll) ALItems.scrollImprisonII);
			putScroll((ItemScroll) ALItems.scrollImprisonIII);
		}
		if (Config.enableTickingBlocks()) {
			putScroll((ItemScroll) ALItems.scrollDarknessI);
			putScroll((ItemScroll) ALItems.scrollLightI);
		}
		putScroll((ItemScroll) ALItems.scrollBanish);
		putScroll((ItemScroll) ALItems.scrollBanishMass);
		putScroll((ItemScroll) ALItems.scrollBlink);
		putScroll((ItemScroll) ALItems.scrollCharm);
		putScroll((ItemScroll) ALItems.scrollCharmMass);
		putScroll((ItemScroll) ALItems.scrollCombust);
		putScroll((ItemScroll) ALItems.scrollCombustArea);
		putScroll((ItemScroll) ALItems.scrollDarknessII);
		putScroll((ItemScroll) ALItems.scrollDispel);
		putScroll((ItemScroll) ALItems.scrollEgress);
		putScroll((ItemScroll) ALItems.scrollFeatherFall);
		putScroll((ItemScroll) ALItems.scrollFireArrow);
		putScroll((ItemScroll) ALItems.scrollFireball);
		putScroll((ItemScroll) ALItems.scrollFreezeI);
		putScroll((ItemScroll) ALItems.scrollFreezeII);
		putScroll((ItemScroll) ALItems.scrollFreezeBoltI);
		putScroll((ItemScroll) ALItems.scrollFreezeBoltII);
		putScroll((ItemScroll) ALItems.scrollFly);
		putScroll((ItemScroll) ALItems.scrollFlyExtended);
		putScroll((ItemScroll) ALItems.scrollGrowthI);
		putScroll((ItemScroll) ALItems.scrollGrowthII);
		putScroll((ItemScroll) ALItems.scrollGustI);
		putScroll((ItemScroll) ALItems.scrollGustII);
		putScroll((ItemScroll) ALItems.scrollGustIII);
		putScroll((ItemScroll) ALItems.scrollHarmI);
		putScroll((ItemScroll) ALItems.scrollHarmII);
		putScroll((ItemScroll) ALItems.scrollHarmMassI);
		putScroll((ItemScroll) ALItems.scrollHarmMassII);
		putScroll((ItemScroll) ALItems.scrollHarmRangedI);
		putScroll((ItemScroll) ALItems.scrollHarmRangedII);
		putScroll((ItemScroll) ALItems.scrollHarmRangedMassI);
		putScroll((ItemScroll) ALItems.scrollHarmRangedMassII);
		putScroll((ItemScroll) ALItems.scrollHealI);
		putScroll((ItemScroll) ALItems.scrollHealII);
		putScroll((ItemScroll) ALItems.scrollHealMassI);
		putScroll((ItemScroll) ALItems.scrollHealMassII);
		putScroll((ItemScroll) ALItems.scrollHealRangedI);
		putScroll((ItemScroll) ALItems.scrollHealRangedII);
		putScroll((ItemScroll) ALItems.scrollHealRangedMassI);
		putScroll((ItemScroll) ALItems.scrollHealRangedMassII);
		putScroll((ItemScroll) ALItems.scrollJumpI);
		putScroll((ItemScroll) ALItems.scrollJumpII);
		putScroll((ItemScroll) ALItems.scrollLightII);
		putScroll((ItemScroll) ALItems.scrollProtectMagicI);
		putScroll((ItemScroll) ALItems.scrollNullifyTeleportI);
		putScroll((ItemScroll) ALItems.scrollResurrectI);
		putScroll((ItemScroll) ALItems.scrollResurrectII);
		putScroll((ItemScroll) ALItems.scrollShockwaveI);
		putScroll((ItemScroll) ALItems.scrollShockwaveII);
		putScroll((ItemScroll) ALItems.scrollSpeedI);
		putScroll((ItemScroll) ALItems.scrollSpeedII);
		putScroll((ItemScroll) ALItems.scrollSpiderClimb);
		putScroll((ItemScroll) ALItems.scrollSummonPig);
		putScroll((ItemScroll) ALItems.scrollSummonSkeletonI);
		putScroll((ItemScroll) ALItems.scrollSummonSkeletonII);
		putScroll((ItemScroll) ALItems.scrollSummonSkeletonIII);
		putScroll((ItemScroll) ALItems.scrollTeleportI);
		putScroll((ItemScroll) ALItems.scrollTeleportII);
		putScroll((ItemScroll) ALItems.scrollTeleportIII);
		putScroll((ItemScroll) ALItems.scrollTestMultiEffects);
		putScroll((ItemScroll) ALItems.scrollWeb);
		putScroll((ItemScroll) ALItems.scrollWebRanged);
		putScroll((ItemScroll) ALItems.scrollWiltI);
		// putScroll((ItemScroll) ALItems);
	}

}

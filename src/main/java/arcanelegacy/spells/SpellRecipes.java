package arcanelegacy.spells;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import arcanelegacy.Config;
import arcanelegacy.inventory.ContainerArcaneInscriber;
import arcanelegacy.item.ALItems;
import arcanelegacy.item.ItemRune;

public class SpellRecipes
{
	private static final SpellRecipes spells = new SpellRecipes();

	private HashMap<List<Integer>, ItemStack> metaInscribingList = new HashMap<List<Integer>, ItemStack>();
	private HashMap<List<Integer>, Float> metaExperience = new HashMap<List<Integer>, Float>();

	/** Used to call methods addInscribing and getInscribingResult. */
	public static final SpellRecipes spells() {
		return spells;
	}

	/** EXP given is (number of runes) x 0.15, minimum of 0.1, maximum of 1.0 */
	private SpellRecipes()
	{
		/*
		ItemStack[] runeArray = new ItemStack[ItemRune.RUNE_NAME.length];
		for (int i = 0; i < ItemRune.RUNE_NAME.length; ++i) {
			i] = new ItemStack(ArcaneLegacy.runeCharged,1,i);
		}
		*/
		/*
		 * Spell recipe template: [adj1][adj2][base recipe][modifier2][modifier1]
		 * The base recipe is sandwiched between modifiers
		 * EXAMPLE: Heal is "augment/life," to add area we need "augment" and "space"
		 * so the recipe becomes: [augment][augment+life][space]
		 */
		/*
		 * RECIPES to re-think:
		 * Imprison I, II, III (lightning-earth? for stone)
		 */
		
		/* SPELLS TO ADD:
		scrollBanish
		scrollBanishMass
		scrollBlink
		scrollCharm
		scrollCharmMass
		scrollDispel
		scrollEgress
		scrollFeatherFall
		scrollNullifyTeleportI, NullificationField
		scrollProtectMagic
		scrollSpiderClimb
		scrollSummonPig
		scrollTeleport I, II, III
		scrollTestMultiEffects
		*/
		
		if (Config.enableAuraSpells()) {
			this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_AUGMENT,ItemRune.RUNE_CREATE,ItemRune.RUNE_FIRE,ItemRune.RUNE_SPACE,ItemRune.RUNE_TIME),new ItemStack(ALItems.scrollFireAura), 0.9F);
			this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_AUGMENT,ItemRune.RUNE_CREATE,ItemRune.RUNE_ICE,ItemRune.RUNE_SPACE,ItemRune.RUNE_TIME),new ItemStack(ALItems.scrollFreezeAuraI), 0.9F);
			this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_AUGMENT,ItemRune.RUNE_AUGMENT,ItemRune.RUNE_EARTH,ItemRune.RUNE_LIFE,ItemRune.RUNE_SPACE,ItemRune.RUNE_TIME),new ItemStack(ALItems.scrollGrowthIII), 1.0F);
			this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_AUGMENT,ItemRune.RUNE_CREATE,ItemRune.RUNE_DIMINISH,ItemRune.RUNE_LIFE,ItemRune.RUNE_SPACE,ItemRune.RUNE_TIME),new ItemStack(ALItems.scrollHarmAuraI), 1.0F);
			this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_AUGMENT,ItemRune.RUNE_CREATE,ItemRune.RUNE_AUGMENT,ItemRune.RUNE_LIFE,ItemRune.RUNE_SPACE,ItemRune.RUNE_TIME),new ItemStack(ALItems.scrollHealAuraI), 1.0F);
			this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_AUGMENT,ItemRune.RUNE_CREATE,ItemRune.RUNE_WIND,ItemRune.RUNE_SPACE,ItemRune.RUNE_TIME),new ItemStack(ALItems.scrollRepulsionField), 0.9F);
			this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_DIMINISH,ItemRune.RUNE_EARTH,ItemRune.RUNE_LIFE,ItemRune.RUNE_TIME),new ItemStack(ALItems.scrollWiltII), 0.75F);
		}
		if (Config.enableImprisonSpells()) {
			this.addInscribing(Arrays.asList(ItemRune.RUNE_CREATE,ItemRune.RUNE_EARTH,ItemRune.RUNE_SPACE,ItemRune.RUNE_MAGIC),new ItemStack(ALItems.scrollImprisonI), 0.6F);
			this.addInscribing(Arrays.asList(ItemRune.RUNE_CREATE,ItemRune.RUNE_LIGHTNING,ItemRune.RUNE_EARTH,ItemRune.RUNE_SPACE,ItemRune.RUNE_MAGIC),new ItemStack(ALItems.scrollImprisonII), 0.75F);
			this.addInscribing(Arrays.asList(ItemRune.RUNE_CREATE,ItemRune.RUNE_LIGHTNING,ItemRune.RUNE_EARTH,ItemRune.RUNE_DIMINISH,ItemRune.RUNE_SPACE,ItemRune.RUNE_MAGIC),new ItemStack(ALItems.scrollImprisonIII), 0.9F);
		}
		if (Config.enableTickingBlocks()) {
			this.addInscribing(Arrays.asList(ItemRune.RUNE_CREATE,ItemRune.RUNE_DARK,ItemRune.RUNE_SPACE),new ItemStack(ALItems.scrollDarknessI), 0.45F);
			this.addInscribing(Arrays.asList(ItemRune.RUNE_CREATE,ItemRune.RUNE_LIGHT),new ItemStack(ALItems.scrollLightI), 0.3F);
		}
		this.addInscribing(Arrays.asList(ItemRune.RUNE_CREATE,ItemRune.RUNE_FIRE),new ItemStack(ALItems.scrollCombust), 0.3F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_CREATE,ItemRune.RUNE_FIRE,ItemRune.RUNE_SPACE),new ItemStack(ALItems.scrollCombustArea), 0.6F);
		
		this.addInscribing(Arrays.asList(ItemRune.RUNE_CREATE,ItemRune.RUNE_DIMINISH,ItemRune.RUNE_LIFE,ItemRune.RUNE_FIRE,ItemRune.RUNE_ARROW),new ItemStack(ALItems.scrollFireArrow), 0.75F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_CREATE,ItemRune.RUNE_DIMINISH,ItemRune.RUNE_LIFE,ItemRune.RUNE_FIRE,ItemRune.RUNE_ARROW,ItemRune.RUNE_SPACE),new ItemStack(ALItems.scrollFireball), 1.0F);
		
		this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_WIND,ItemRune.RUNE_MOVE,ItemRune.RUNE_NEGATE,ItemRune.RUNE_EARTH),new ItemStack(ALItems.scrollFly), 0.75F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_AUGMENT,ItemRune.RUNE_WIND,ItemRune.RUNE_MOVE,ItemRune.RUNE_NEGATE,ItemRune.RUNE_EARTH,ItemRune.RUNE_TIME), new ItemStack(ALItems.scrollFlyExtended), 1.0F);
		
		this.addInscribing(Arrays.asList(ItemRune.RUNE_CREATE,ItemRune.RUNE_ICE),new ItemStack(ALItems.scrollFreezeI), 0.3F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_CREATE,ItemRune.RUNE_ICE),new ItemStack(ALItems.scrollFreezeII), 0.45F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_CREATE,ItemRune.RUNE_ICE,ItemRune.RUNE_ARROW),new ItemStack(ALItems.scrollFreezeBoltI), 0.45F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_CREATE,ItemRune.RUNE_ICE,ItemRune.RUNE_ARROW),new ItemStack(ALItems.scrollFreezeBoltII), 0.6F);
		
		this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_EARTH,ItemRune.RUNE_LIFE),new ItemStack(ALItems.scrollGrowthI), 0.45F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_AUGMENT,ItemRune.RUNE_EARTH,ItemRune.RUNE_LIFE,ItemRune.RUNE_SPACE),new ItemStack(ALItems.scrollGrowthII), 0.75F);
		
		this.addInscribing(Arrays.asList(ItemRune.RUNE_CREATE,ItemRune.RUNE_WIND,ItemRune.RUNE_ARROW),new ItemStack(ALItems.scrollGustI), 0.45F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_CREATE,ItemRune.RUNE_WIND,ItemRune.RUNE_ARROW),new ItemStack(ALItems.scrollGustII), 0.6F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_AUGMENT,ItemRune.RUNE_CREATE,ItemRune.RUNE_WIND,ItemRune.RUNE_ARROW),new ItemStack(ALItems.scrollGustIII), 0.75F);
		
		this.addInscribing(Arrays.asList(ItemRune.RUNE_DIMINISH,ItemRune.RUNE_LIFE),new ItemStack(ALItems.scrollHarmI), 0.3F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_DIMINISH,ItemRune.RUNE_DIMINISH,ItemRune.RUNE_LIFE),new ItemStack(ALItems.scrollHarmII), 0.45F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_DIMINISH,ItemRune.RUNE_LIFE,ItemRune.RUNE_SPACE),new ItemStack(ALItems.scrollHarmMassI), 0.6F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_DIMINISH,ItemRune.RUNE_DIMINISH,ItemRune.RUNE_LIFE,ItemRune.RUNE_SPACE),new ItemStack(ALItems.scrollHarmMassII), 0.75F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_CREATE,ItemRune.RUNE_DIMINISH,ItemRune.RUNE_LIFE,ItemRune.RUNE_ARROW),new ItemStack(ALItems.scrollHarmRangedI), 0.6F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_CREATE,ItemRune.RUNE_DIMINISH,ItemRune.RUNE_DIMINISH,ItemRune.RUNE_LIFE,ItemRune.RUNE_ARROW),new ItemStack(ALItems.scrollHarmRangedII), 0.75F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_CREATE,ItemRune.RUNE_DIMINISH,ItemRune.RUNE_LIFE,ItemRune.RUNE_ARROW,ItemRune.RUNE_SPACE),new ItemStack(ALItems.scrollHarmRangedMassI), 0.9F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_CREATE,ItemRune.RUNE_DIMINISH,ItemRune.RUNE_DIMINISH,ItemRune.RUNE_LIFE,ItemRune.RUNE_ARROW,ItemRune.RUNE_SPACE),new ItemStack(ALItems.scrollHarmRangedMassII), 1.0F);
		
		this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_LIFE),new ItemStack(ALItems.scrollHealI), 0.3F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_AUGMENT,ItemRune.RUNE_LIFE),new ItemStack(ALItems.scrollHealII), 0.45F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_AUGMENT,ItemRune.RUNE_LIFE,ItemRune.RUNE_SPACE),new ItemStack(ALItems.scrollHealMassI), 0.6F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_AUGMENT,ItemRune.RUNE_AUGMENT,ItemRune.RUNE_LIFE,ItemRune.RUNE_SPACE),new ItemStack(ALItems.scrollHealMassII), 0.75F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_CREATE,ItemRune.RUNE_AUGMENT,ItemRune.RUNE_LIFE,ItemRune.RUNE_ARROW),new ItemStack(ALItems.scrollHealRangedI), 0.6F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_CREATE,ItemRune.RUNE_AUGMENT,ItemRune.RUNE_AUGMENT,ItemRune.RUNE_LIFE,ItemRune.RUNE_ARROW),new ItemStack(ALItems.scrollHealRangedII), 0.75F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_CREATE,ItemRune.RUNE_AUGMENT,ItemRune.RUNE_LIFE,ItemRune.RUNE_ARROW,ItemRune.RUNE_SPACE),new ItemStack(ALItems.scrollHealRangedMassI), 0.9F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_CREATE,ItemRune.RUNE_AUGMENT,ItemRune.RUNE_AUGMENT,ItemRune.RUNE_LIFE,ItemRune.RUNE_ARROW,ItemRune.RUNE_SPACE),new ItemStack(ALItems.scrollHealRangedMassII), 1.0F);
		
		this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_WIND,ItemRune.RUNE_MOVE),new ItemStack(ALItems.scrollJumpI), 0.45F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_AUGMENT,ItemRune.RUNE_WIND,ItemRune.RUNE_MOVE),new ItemStack(ALItems.scrollJumpII), 0.6F);
		
		this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_CREATE,ItemRune.RUNE_DARK,ItemRune.RUNE_SPACE,ItemRune.RUNE_TIME),new ItemStack(ALItems.scrollDarknessII), 0.75F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_CREATE,ItemRune.RUNE_LIGHT,ItemRune.RUNE_TIME),new ItemStack(ALItems.scrollLightII), 0.6F);
		
		this.addInscribing(Arrays.asList(ItemRune.RUNE_NEGATE,ItemRune.RUNE_DEATH,ItemRune.RUNE_AUGMENT,ItemRune.RUNE_PROTECT,ItemRune.RUNE_LIFE,ItemRune.RUNE_MAGIC),new ItemStack(ALItems.scrollResurrectI), 0.9F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_NEGATE,ItemRune.RUNE_DEATH,ItemRune.RUNE_AUGMENT,ItemRune.RUNE_AUGMENT,ItemRune.RUNE_PROTECT,ItemRune.RUNE_LIFE,ItemRune.RUNE_MAGIC),new ItemStack(ALItems.scrollResurrectII), 1.0F);
		
		this.addInscribing(Arrays.asList(ItemRune.RUNE_CREATE,ItemRune.RUNE_MOVE,ItemRune.RUNE_EARTH,ItemRune.RUNE_MAGIC),new ItemStack(ALItems.scrollShockwaveI), 0.6F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_CREATE,ItemRune.RUNE_MOVE,ItemRune.RUNE_EARTH,ItemRune.RUNE_MAGIC,ItemRune.RUNE_SPACE),new ItemStack(ALItems.scrollShockwaveII), 0.9F);
		
		this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_MOVE),new ItemStack(ALItems.scrollSpeedI), 0.3F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_AUGMENT,ItemRune.RUNE_AUGMENT,ItemRune.RUNE_MOVE),new ItemStack(ALItems.scrollSpeedII), 0.45F);
		
		this.addInscribing(Arrays.asList(ItemRune.RUNE_CREATE,ItemRune.RUNE_ARROW,ItemRune.RUNE_DEATH,ItemRune.RUNE_LIFE),new ItemStack(ALItems.scrollSummonSkeletonI), 0.6F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_CREATE,ItemRune.RUNE_CREATE,ItemRune.RUNE_ARROW,ItemRune.RUNE_DEATH,ItemRune.RUNE_LIFE),new ItemStack(ALItems.scrollSummonSkeletonII), 0.75F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_CREATE,ItemRune.RUNE_CREATE,ItemRune.RUNE_CREATE,ItemRune.RUNE_ARROW,ItemRune.RUNE_DEATH,ItemRune.RUNE_LIFE),new ItemStack(ALItems.scrollSummonSkeletonIII), 0.9F);
		
		// Web uses Earth rune to distinguish from Slow
		this.addInscribing(Arrays.asList(ItemRune.RUNE_CREATE,ItemRune.RUNE_DIMINISH,ItemRune.RUNE_MOVE,ItemRune.RUNE_EARTH,ItemRune.RUNE_SPACE),new ItemStack(ALItems.scrollWeb), 0.75F);
		this.addInscribing(Arrays.asList(ItemRune.RUNE_CREATE,ItemRune.RUNE_DIMINISH,ItemRune.RUNE_MOVE,ItemRune.RUNE_EARTH,ItemRune.RUNE_SPACE,ItemRune.RUNE_ARROW),new ItemStack(ALItems.scrollWebRanged), 0.9F);
		
		this.addInscribing(Arrays.asList(ItemRune.RUNE_DIMINISH,ItemRune.RUNE_EARTH,ItemRune.RUNE_LIFE),new ItemStack(ALItems.scrollWiltI), 0.45F);
	}

	/**
	 * Adds an array of runes, the resulting scroll, and experience given
	 */
	public void addInscribing(List<Integer> runes, ItemStack scroll, float experience)
	{
		if (metaInscribingList.containsKey(runes))
		{
			System.out.println("[WARNING] Conflicting recipe: " + runes.toString() + " for " + metaInscribingList.get(runes).toString());
		}
		else
		{
			metaInscribingList.put(runes, scroll);
			metaExperience.put(Arrays.asList(scroll.itemID, scroll.getItemDamage()), experience);
		}
	}

	/**
	 * Used to get the resulting ItemStack form a source inventory
	 * @param item The Source inventory
	 * @return The result ItemStack
	 */
	public ItemStack getInscribingResult(ItemStack[] runes) 
	{
		int recipeLength = 0;
		for (int i = 0; i < runes.length && runes[i] != null && i < ContainerArcaneInscriber.RUNE_SLOTS; ++i)
		{
			++recipeLength;
		}
		Integer[] idIndex = new Integer[recipeLength];
		for (int i = 0; i < recipeLength; ++i) {
			idIndex[i] = (Integer.valueOf(runes[i].getItemDamage()));
		}
		return (ItemStack) metaInscribingList.get(Arrays.asList(idIndex));	
	}

	/**
	 * Grabs the amount of base experience for this item to give when pulled from the furnace slot.
	 */
	public float getExperience(ItemStack item)
	{
		if (item == null || item.getItem() == null)
		{
			return 0;
		}
		float ret = -1; // value returned by "item.getItem().getSmeltingExperience(item);" when item doesn't specify experience to give
		if (ret < 0 && metaExperience.containsKey(Arrays.asList(item.itemID, item.getItemDamage())))
		{
			ret = metaExperience.get(Arrays.asList(item.itemID, item.getItemDamage()));
		}
		return (ret < 0 ? 0 : ret);
	}

	public Map<List<Integer>, ItemStack> getMetaInscribingList()
	{
		return metaInscribingList;
	}
}

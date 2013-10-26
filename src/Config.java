package coolalias.arcanelegacy;

import net.minecraftforge.common.Configuration;
import coolalias.arcanelegacy.entity.projectile.EntitySpell;
import coolalias.arcanelegacy.entity.summons.SummonPig;
import coolalias.arcanelegacy.entity.summons.SummonSkeleton;
import coolalias.arcanelegacy.registry.RegisterKeyBindings;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.EntityRegistry;

public class Config
{
	// Default starting index values
	private static final int MOD_BLOCK_INDEX_DEFAULT = 500, MOD_ITEM_INDEX_DEFAULT = 6000,
			MOD_ENTITY_INDEX_DEFAULT = 1, SUMMON_DURATION_DEFAULT = 1200, MIN_SUMMON_DURATION = 120;

	// Variables to read from Config:
	private static int modBlockIndex, modItemIndex, modEntityIndex, scrollStartIndex;

	// Spell Config:
	private static boolean enableTickingBlocks, enableAuraSpells, enableImprisonSpells,
	enablePermanentSummons;
	private static int baseSummonDuration;

	// Magic Bag Config:
	private static boolean enableMagicBag, enableMagicBagRecipe;

	// Crafting Config:
	private static boolean enableRuneCopying;

	// Trade Config:
	private static boolean enableTrading, enableAllRuneTrades, enableBlankRuneTrades;

	public static final void init(Configuration config)
	{
		config.load();

		// BLOCK CONFIG
		modBlockIndex = config.getBlock("modBlockIndex", MOD_BLOCK_INDEX_DEFAULT).getInt();
		//System.out.println("[CONFIG] mod block index from config: " + modBlockIndex);
		enableTickingBlocks = config.get("block", "enableTickingBlocks", true).getBoolean(true);

		// ITEM CONFIG
		scrollStartIndex = config.getItem("modItemIndex", MOD_ITEM_INDEX_DEFAULT).getInt();
		modItemIndex = scrollStartIndex - 256;
		//System.out.println("[CONFIG] mod item index from config: " + modItemIndex);
		//System.out.println("[CONFIG] mod scroll start index from config: " + scrollStartIndex);

		// ENTITY CONFIG
		modEntityIndex = config.get("entity", "modEntityIndex", MOD_ENTITY_INDEX_DEFAULT).getInt();
		//System.out.println("[CONFIG] mod entity index from config: " + modEntityIndex);

		// SPELL CONFIG:
		enableAuraSpells = config.get("spell", "EnableAuraSpells", true).getBoolean(true);
		enableImprisonSpells = config.get("spell", "EnableImprisonSpells", true).getBoolean(true);
		enablePermanentSummons = config.get("spell", "EnablePermanentSummons", false).getBoolean(false);
		baseSummonDuration = config.get("spell", "BaseSummonDuration", SUMMON_DURATION_DEFAULT).getInt();
		// Minimum summoning duration is 6 seconds (120 ticks)
		if (baseSummonDuration < MIN_SUMMON_DURATION) baseSummonDuration = MIN_SUMMON_DURATION;
		// Maximum summoning duration is 10 minutes (100 times the minimum, or 10 times the default value)
		else if (baseSummonDuration > MIN_SUMMON_DURATION * 100) baseSummonDuration = MIN_SUMMON_DURATION * 100;
		//System.out.println("[CONFIG] base summon duration from config: " + (enablePermanentSummons ? "permanent" : baseSummonDuration));

		// MAGIC BAG CONFIG:
		enableMagicBag = config.get("item", "EnableMagicBag", true).getBoolean(true);
		enableMagicBagRecipe = config.get("item", "EnableMagicBagRecipe", false).getBoolean(false);

		// CRAFTING CONFIG
		enableRuneCopying = config.get("crafting", "EnableRuneCopying", true).getBoolean(true);

		// TRADE CONFIG
		enableTrading = config.get("trade", "EnableTrading", true).getBoolean(true);
		enableAllRuneTrades = config.get("trade", "EnableAllRuneTrades", false).getBoolean(false);
		enableBlankRuneTrades = config.get("trade", "EnableBlankRuneTrades", true).getBoolean(true);

		// KEY CONFIG
		if (FMLCommonHandler.instance().getSide().isClient())
			RegisterKeyBindings.init(config);

		config.save();
	}
	
	public static final void registerEntities()
	{
		EntityRegistry.registerModEntity(SummonPig.class, "SummonPig", nextModEntityID(), ArcaneLegacy.instance, 80, 3, true);
		EntityRegistry.registerModEntity(SummonSkeleton.class, "SummonSkeleton", nextModEntityID(), ArcaneLegacy.instance, 80, 3, true);
		//EntityRegistry.registerGlobalEntityID(SummonSkeleton.class, "SummonSkeleton", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerModEntity(EntitySpell.class, "Spell", nextModEntityID(), ArcaneLegacy.instance, 64, 10, true);
	}

	public static final int scrollStartIndex() {
		return scrollStartIndex;
	}

	public static final int nextModItemID() {
		return modItemIndex++;
	}

	public static final int nextModBlockID() {
		return modBlockIndex++;
	}

	public static final int nextModEntityID() {
		return modEntityIndex++;
	}

	public static final boolean enableTickingBlocks() {
		return enableTickingBlocks;
	}

	public static final boolean enableAuraSpells() {
		return enableAuraSpells;
	}

	public static final boolean enableImprisonSpells() {
		return enableImprisonSpells;
	}

	public static final boolean enablePermanentSummons() {
		return enablePermanentSummons;
	}

	public static final int baseSummonDuration() {
		return baseSummonDuration;
	}

	public static final boolean enableMagicBag() {
		return enableMagicBag;
	}

	public static final boolean enableMagicBagRecipe() {
		return enableMagicBagRecipe;
	}

	public static final boolean enableRuneCopying() {
		return enableRuneCopying;
	}

	public static final boolean enableTrading() {
		return enableTrading;
	}

	public static final boolean enableAllRuneTrades() {
		return enableAllRuneTrades;
	}

	public static final boolean enableBlankRuneTrades() {
		return enableBlankRuneTrades;
	}
}

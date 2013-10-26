package coolalias.arcanelegacy.registry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import coolalias.arcanelegacy.item.ALItems;
import coolalias.arcanelegacy.item.ItemRune;

public class RegisterDungeonLoot
{
	/*
	 * WeightedRandomChestContent arguments for Constructor 1:
	 * ItemStack to generate, Minimum & Maximum number for the stack size,
	 * and frequency (weight) with which item is chosen.
	 * Constructor 2: ID of Item to create, metadata / damage value for Item, Min, Max, Weight
	 */
	public static void init()
	{
		// RUNES
		for (int i = 0; i < ItemRune.RUNE_NAME.length; i++)
		{
			// Low chance for uncharged runes
			WeightedRandomChestContent tmpLow = new WeightedRandomChestContent(ALItems.runeBasic.itemID, i, 1, 1, getRuneWeight(i));
			// High chance for uncharged runes
			WeightedRandomChestContent tmpHigh = new WeightedRandomChestContent(ALItems.runeBasic.itemID, i, 1, 1, getRuneWeight(i)*10);
			// 'Stronghold' chance for uncharged runes
			WeightedRandomChestContent stronghold = new WeightedRandomChestContent(ALItems.runeBasic.itemID, i, 1, 1, getRuneWeight(i)*50);
			// Low chance for charged runes
			WeightedRandomChestContent tmpCharged = new WeightedRandomChestContent(ALItems.runeCharged.itemID, i, 1, 1, 1);
			// High ('Stronhold') chance for charged runes
			WeightedRandomChestContent strongholdCharged = new WeightedRandomChestContent(ALItems.runeCharged.itemID, i, 1, 1, getRuneWeight(i)*50);
			
			// Uncharged Runes
			ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(tmpLow);
			ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_DESERT_CHEST).addItem(tmpHigh);
			ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_CHEST).addItem(tmpHigh);
			ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_DISPENSER).addItem(tmpLow);
			ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_CORRIDOR).addItem(stronghold);
			ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_LIBRARY).addItem(stronghold);
			ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_CROSSING).addItem(stronghold);
			ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(tmpLow);
			ChestGenHooks.getInfo(ChestGenHooks.BONUS_CHEST).addItem(tmpHigh);
			ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(tmpLow);
			
			// Charged Runes
			ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(tmpCharged);
			ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_CORRIDOR).addItem(strongholdCharged);
			ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_LIBRARY).addItem(strongholdCharged);
			ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_CROSSING).addItem(strongholdCharged);
			ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(tmpCharged);
			ChestGenHooks.getInfo(ChestGenHooks.BONUS_CHEST).addItem(tmpCharged);
		}
		
		// RECIPES
		
		// SCROLLS
		ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(ALItems.scrollBlank.itemID, 0, 1, 4, 50));
		ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(new WeightedRandomChestContent(ALItems.scrollBlank.itemID, 0, 1, 4, 50));
		ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_DESERT_CHEST).addItem(new WeightedRandomChestContent(ALItems.scrollBlank.itemID, 0, 1, 4, 50));
		ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_CHEST).addItem(new WeightedRandomChestContent(ALItems.scrollBlank.itemID, 0, 1, 4, 50));
		
		// MISC ITEMS / BLOCKS
		
	}
	
	/*
	 * Returns the weighted value of specific runes for chest generation
	 * @param runeIndex is the metadata (damage) value of the rune desired
	 */
	private static int getRuneWeight(int runeIndex)
	{
		int weight;
		switch(runeIndex) {
		case ItemRune.RUNE_CREATE:
			weight = 15;
			break;
		default:
			weight = 10;
			break;
		}
		return weight;
	}
}

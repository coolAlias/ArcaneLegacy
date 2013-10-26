package coolalias.arcanelegacy.registry;

import java.util.Random;

import coolalias.arcanelegacy.Config;
import coolalias.arcanelegacy.item.ALItems;
import coolalias.arcanelegacy.item.ItemRune;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;

public class RegisterTradeHandler implements IVillageTradeHandler
{
	public static void init()
	{
		for (int i = 0; i < 5; ++i) {
			VillagerRegistry.instance().registerVillageTradeHandler(i, new RegisterTradeHandler());
		}
	}
	
	@Override
	public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random)
	{
		switch(villager.getProfession()) {
		case 0:	// FARMER
			break;
		case 1:	// LIBRARIAN
			recipeList.add(new MerchantRecipe(new ItemStack(Item.emerald, 2), new ItemStack(ALItems.scrollBlank, 1)));
			break;
		case 2:	// PRIEST
			if (Config.enableBlankRuneTrades()) {
				recipeList.add(new MerchantRecipe(new ItemStack(Item.emerald, 33 + random.nextInt(16)), new ItemStack(ALItems.spiritShard, 5 + random.nextInt(4)), new ItemStack(ALItems.runeBasic, 1, 0)));
			}
			if (Config.enableAllRuneTrades()) {
				for (int i = 1; i < ItemRune.RUNE_NAME.length; ++i) {
					recipeList.add(new MerchantRecipe(new ItemStack(Item.emerald, 33 + random.nextInt(16)), new ItemStack(ALItems.spiritShard, 5 + random.nextInt(4)), new ItemStack(ALItems.runeBasic, 1, i)));
				}
			}
			break;
		case 3:	// BLACKSMITH
			if (Config.enableRuneCopying())
				recipeList.add(new MerchantRecipe(new ItemStack(Item.emerald, 2), new ItemStack(Item.diamond, 1), new ItemStack(ALItems.arcaneChisel, 1)));
			break;
		case 4: // BUTCHER
			break;
		default:
			break;
		}
	}
}

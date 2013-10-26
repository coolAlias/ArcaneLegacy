package coolalias.arcanelegacy.item.crafting;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import coolalias.arcanelegacy.item.ALItems;
import coolalias.arcanelegacy.item.ItemDust;
import coolalias.arcanelegacy.item.ItemRune;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ArcaneInfuserRecipes
{
	private static final ArcaneInfuserRecipes infusingBase = new ArcaneInfuserRecipes();

	/**
	 * metaResultList is used to determine if a single item with subtypes is involved in any
     * infusing recipes. Needed for ContainerArcaneInfuser transferItemInSlot method
     */
    private HashMap<List<Integer>, ItemStack> metaResultList = new HashMap<List<Integer>, ItemStack>();
    private HashMap<List<Integer>, ItemStack> metaInfusingList = new HashMap<List<Integer>, ItemStack>();
    private HashMap<List<Integer>, Float> metaExperience = new HashMap<List<Integer>, Float>();
    private Map experienceList = new HashMap();
    
    /**
     * Used to call methods addInfusing and getInfusingResult.
     */
    public static final ArcaneInfuserRecipes infusing()
    {
        return infusingBase;
    }

    private ArcaneInfuserRecipes()
    {
    	for (int i = 0; i < ItemRune.RUNE_NAME.length; i++) {
    		ItemStack dust = getSecondaryIngredient(i);
    		this.addInfusing(ALItems.runeBasic.itemID, i, dust.itemID, dust.getItemDamage(), new ItemStack(ALItems.runeCharged,1,i), 1.0F);
    	}
    }
    
    /**
     * Returns ItemStack required to charge rune of subtype runeIndex
     */
    private ItemStack getSecondaryIngredient(int runeIndex)
    {
    	ItemStack dust;
    	switch(runeIndex) {
    	case ItemRune.RUNE_EARTH:
    		dust = new ItemStack(ALItems.dust,1,ItemDust.DUST_DIAMOND);
    		break;
    	case ItemRune.RUNE_FIRE:
    		dust = new ItemStack(Item.blazePowder);
    		break;
    	case ItemRune.RUNE_DEATH:
    		dust = new ItemStack(Item.dyePowder,1,15);	// Bonemeal = 15
    		break;
    	case ItemRune.RUNE_WIND:
    		dust = new ItemStack(ALItems.dust,1,ItemDust.DUST_EMERALD);
    		break;
    	default:
    		dust = new ItemStack(ALItems.dust,1,ItemDust.DUST_DIAMOND);
    		break;
    	}
    	return dust;
    }
    
    /**
     * For adding 2-input:1-output recipes
     * Will add both recipe configurations automatically
     * A metadata sensitive version of adding a furnace recipe.
     */
    public void addInfusing(int itemID1, int metadata1, int itemID2, int metadata2, ItemStack itemstack, float experience)
    {
    	metaResultList.put(Arrays.asList(itemID1, metadata1), itemstack);
    	metaResultList.put(Arrays.asList(itemID2, metadata2), itemstack);
        metaInfusingList.put(Arrays.asList(itemID1, metadata1, itemID2, metadata2), itemstack);
        metaInfusingList.put(Arrays.asList(itemID2, metadata2, itemID1, metadata1), itemstack);
        metaExperience.put(Arrays.asList(itemstack.itemID, itemstack.getItemDamage()), experience);
    }
    
    /**
     * Used to get the resulting ItemStack form a source ItemStack
     * @param item The Source ItemStack
     * @return The result ItemStack
     */
    public ItemStack getInfusingResult(ItemStack item) 
    {
        if (item == null)
        {
            return null;
        }
        ItemStack ret = (ItemStack)metaInfusingList.get(Arrays.asList(item.itemID, item.getItemDamage()));
        if (ret != null) 
        {
            return ret;
        }
        return (ItemStack)metaResultList.get(Arrays.asList(item.itemID, item.getItemDamage()));
    }
    
    /**
     * Used to get the resulting ItemStack form 2 source ItemStacks
     * @param item The Source ItemStack
     * @return The result ItemStack
     */
    public ItemStack getInfusingResult(ItemStack item1, ItemStack item2) 
    {
        if (item1 == null || item2 == null) { return null; }
        
        return (ItemStack)metaInfusingList.get(Arrays.asList(item1.itemID, item1.getItemDamage(), item2.itemID, item2.getItemDamage()));
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
        if (ret < 0 && experienceList.containsKey(item.itemID))
        {
            ret = ((Float)experienceList.get(item.itemID)).floatValue();
        }
        return (ret < 0 ? 0 : ret);
    }

    public Map<List<Integer>, ItemStack> getMetaInfusingList()
    {
        return metaInfusingList;
    }
}

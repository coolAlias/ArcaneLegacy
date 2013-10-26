package coolalias.arcanelegacy.blocks;

import coolalias.arcanelegacy.ArcaneLegacy;
import coolalias.arcanelegacy.Config;
import coolalias.arcanelegacy.item.ALItems;
import coolalias.arcanelegacy.tileentity.TileEntityArcaneInfuser;
import coolalias.arcanelegacy.tileentity.TileEntityArcaneInscriber;
import coolalias.arcanelegacy.tileentity.TileEntityMortarPestle;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class ALBlocks
{
	// BLOCKS
	public static Block blockSpirit;
	public static Block blockDarkness, blockDarknessTicking;
	public static Block blockLight, blockLightTicking;
	public static Block blockWebTicking;

	// BLOCKS with TILE ENTITIES
	public static Block mortarPestleIdle, mortarPestleActive;
	public static Block arcaneInfuserIdle, arcaneInfuserActive;
	public static Block arcaneInscriberIdle, arcaneInscriberActive;

	public static final void init()
	{
		blockSpirit = new BlockSpirit(Config.nextModBlockID(), Material.rock).setUnlocalizedName("block_Spirit");
		blockDarkness = new BlockMagicDarkness(Config.nextModBlockID()).setUnlocalizedName("block_Darkness");
		blockLight = new BlockMagicLight(Config.nextModBlockID()).setUnlocalizedName("block_Light");

		if (Config.enableTickingBlocks()) {
			blockDarknessTicking = new BlockMagicDarknessTicking(Config.nextModBlockID()).setUnlocalizedName("block_Darkness_Ticking");
			blockLightTicking = new BlockMagicLight(Config.nextModBlockID()).setUnlocalizedName("block_Light_Ticking");
			blockWebTicking = new BlockWebTicking(Config.nextModBlockID()).setUnlocalizedName("block_Web_Ticking");
		}

		// BLOCKS with TILE ENTITIES
		mortarPestleIdle = new BlockMortarPestle(Config.nextModBlockID(), false).setUnlocalizedName("mortar_pestle_idle").setCreativeTab(ArcaneLegacy.tabArcaneBlocks);
		mortarPestleActive = new BlockMortarPestle(Config.nextModBlockID(), true).setUnlocalizedName("mortar_pestle_active");
		arcaneInfuserIdle = new BlockArcaneInfuser(Config.nextModBlockID(), false).setUnlocalizedName("arcane_infuser_idle").setCreativeTab(ArcaneLegacy.tabArcaneBlocks);
		arcaneInfuserActive = new BlockArcaneInfuser(Config.nextModBlockID(), true).setUnlocalizedName("arcane_infuser_active");
		arcaneInscriberIdle = new BlockArcaneInscriber(Config.nextModBlockID(), false).setUnlocalizedName("arcane_inscriber_idle").setCreativeTab(ArcaneLegacy.tabArcaneBlocks);
		arcaneInscriberActive = new BlockArcaneInscriber(Config.nextModBlockID(), true).setUnlocalizedName("arcane_inscriber_active");
		
		register();
		
		addNames();
	}
	
	private static final void register()
	{
		// BLOCKS
		GameRegistry.registerBlock(ALBlocks.blockSpirit, "blockSpirit");
		MinecraftForge.setBlockHarvestLevel(ALBlocks.blockSpirit, "pickaxe", 2);
		GameRegistry.registerBlock(ALBlocks.blockDarkness, "blockDarkness");
		GameRegistry.registerBlock(ALBlocks.blockLight, "blockLight");
		
		if (Config.enableTickingBlocks())
		{
			GameRegistry.registerBlock(ALBlocks.blockDarknessTicking, "blockDarknessTicking");
			GameRegistry.registerBlock(ALBlocks.blockLightTicking, "blockLightTicking");
			GameRegistry.registerBlock(ALBlocks.blockWebTicking, "blockWebTicking");
			//MinecraftForge.setBlockHarvestLevel(ALBlocks.blockWebTicking, "shears", 0);
		}
		
		// CONTAINER BLOCKS
		GameRegistry.registerBlock(ALBlocks.arcaneInfuserActive, "arcaneInfuserActive");
		MinecraftForge.setBlockHarvestLevel(ALBlocks.arcaneInfuserActive, "pickaxe", 1);
		GameRegistry.registerBlock(ALBlocks.arcaneInfuserIdle, "arcaneInfuserIdle");
		MinecraftForge.setBlockHarvestLevel(ALBlocks.arcaneInfuserIdle, "pickaxe", 1);
		
		GameRegistry.registerBlock(ALBlocks.arcaneInscriberActive, "arcaneInscriberActive");
		MinecraftForge.setBlockHarvestLevel(ALBlocks.arcaneInscriberActive, "pickaxe", 1);
		GameRegistry.registerBlock(ALBlocks.arcaneInscriberIdle, "arcaneInscriberIdle");
		MinecraftForge.setBlockHarvestLevel(ALBlocks.arcaneInscriberIdle, "pickaxe", 1);
		
		GameRegistry.registerBlock(ALBlocks.mortarPestleActive, "mortarPestleActive");
		MinecraftForge.setBlockHarvestLevel(ALBlocks.mortarPestleActive, "pickaxe", 1);
		GameRegistry.registerBlock(ALBlocks.mortarPestleIdle, "mortarPestleIdle");
		MinecraftForge.setBlockHarvestLevel(ALBlocks.mortarPestleIdle, "pickaxe", 1);
		
		// TILE ENTITIES
		GameRegistry.registerTileEntity(TileEntityArcaneInfuser.class, "tileEntityArcaneInfuser");
		GameRegistry.registerTileEntity(TileEntityArcaneInscriber.class, "tileEntityArcaneInscriber");
		GameRegistry.registerTileEntity(TileEntityMortarPestle.class, "tileEntityMortarPestle");
	}
	
	private static final void addNames()
	{
		LanguageRegistry.addName(ALBlocks.blockSpirit, "Spirit Block");
		LanguageRegistry.addName(ALBlocks.blockDarkness, "Darkness");
		LanguageRegistry.addName(ALBlocks.blockLight, "Light");
		
		if (Config.enableTickingBlocks()) {
			LanguageRegistry.addName(ALBlocks.blockDarknessTicking, "Darkness (Ticking)");
			LanguageRegistry.addName(ALBlocks.blockLightTicking, "Light (Ticking)");
			LanguageRegistry.addName(ALBlocks.blockWebTicking, "Web (Ticking)");
		}
		
		LanguageRegistry.addName(ALBlocks.arcaneInfuserActive, "Arcane Infuser (Active)");
		LanguageRegistry.addName(ALBlocks.arcaneInfuserIdle, "Arcane Infuser (Idle)");
		LanguageRegistry.addName(ALBlocks.arcaneInscriberActive, "Arcane Inscriber (Active)");
		LanguageRegistry.addName(ALBlocks.arcaneInscriberIdle, "Arcane Inscriber (Idle)");
		LanguageRegistry.addName(ALBlocks.mortarPestleActive, "Mortar and Pestle (Active)");
		LanguageRegistry.addName(ALBlocks.mortarPestleIdle, "Mortar and Pestle (Idle)");
	}
	
	public static final void addRecipes()
	{
		GameRegistry.addShapelessRecipe(new ItemStack(ALBlocks.arcaneInscriberIdle), Block.dirt, Block.dirt, Block.dirt, Block.dirt);
		GameRegistry.addShapelessRecipe(new ItemStack(ALBlocks.arcaneInfuserIdle), ALItems.arcaneChisel, Block.stone);
	}
}

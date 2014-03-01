package arcanelegacy.world.gen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import arcanelegacy.blocks.ALBlocks;
import cpw.mods.fml.common.IWorldGenerator;

public class WorldGenBlocks implements IWorldGenerator {

	public WorldGenBlocks() {}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		switch(world.provider.dimensionId) {
		case -1:
			generateNether(world, random, chunkX * 16, chunkZ * 16);
			break;
		case 0:
			generateSurface(world, random, chunkX * 16, chunkZ * 16);
			break;
		default:
			break;
		}
	}
	
	/*
	 * This might get weird, as Nether generation uses a population method
	 * for most blocks other than netherrack.
	 * EXAMPLE:
	 * i1 = this.hellRNG.nextInt(this.hellRNG.nextInt(10) + 1);

        doGen = TerrainGen.populate(par1IChunkProvider, worldObj, hellRNG, par2, par3, false, GLOWSTONE);
        for (j1 = 0; doGen && j1 < i1; ++j1)
        {
            k1 = k + this.hellRNG.nextInt(16) + 8;
            l1 = this.hellRNG.nextInt(120) + 4;
            i2 = l + this.hellRNG.nextInt(16) + 8;
            (new WorldGenGlowStone1()).generate(this.worldObj, this.hellRNG, k1, l1, i2);
        }
	 */
	private void generateNether(World world, Random rand, int chunkX, int chunkZ)
	{
		int rarity = 10, genHeight1 = 120, genHeight2 = 128;
		int randRarity = rand.nextInt(rand.nextInt(10) + 1);
		
		// Generate Magic Blocks
		for (int i = 0; i < randRarity; i++) {
			int randPosX = chunkX + rand.nextInt(16) + 8;
			int randPosY = rand.nextInt(genHeight1) + 4;
			int randPosZ = chunkZ + rand.nextInt(16) + 8;
			(new WorldGenSpiritBlock()).generate(world, rand, randPosX, randPosY, randPosZ);
		}
		for (int i = 0; i < rarity; i++) {
			int randPosX = chunkX + rand.nextInt(16) + 8;
			int randPosY = rand.nextInt(genHeight2);
			int randPosZ = chunkZ + rand.nextInt(16) + 8;
			(new WorldGenSpiritBlock()).generate(world, rand, randPosX, randPosY, randPosZ);
		}
	}
	
	public void generateSurface(World world, Random rand, int chunkX, int chunkZ) {
		int rarity = 8, veinSize = 4, genHeight = 64, blockToReplace = Block.stone.blockID;
		
		// Generate Magic Blocks - very rare in overworld
		for (int i = 0; i < rarity; i++) {
			int randPosX = chunkX + rand.nextInt(16);
			int randPosY = rand.nextInt(genHeight);
			int randPosZ = chunkZ + rand.nextInt(16);

			/*
			 * WorldGenMinable parameters:
			 * Constructor 1: new Block.ID, # to generate, block.ID to replace (default Stone)
			 * Constructor 2: new Block.ID, new Block metadata value, # to generate, block.ID to replace
			 */
			(new WorldGenMinable(ALBlocks.blockSpirit.blockID, veinSize, blockToReplace)).generate(world, rand, randPosX, randPosY, randPosZ);
		}
		
		// Generate Other Blocks...
	}
	
	/*
	 * EXAMPLE of Biome Specific Generation
	public void generateSurface(World par1world, Random par2Random, int chunkX, int chunkZ)
	{
		String s = par1World.getBiomeGenForCoords(chunkX + 8, chunkZ + 8).biomeName;
		if (s.startsWith("Desert"))
		{
			int rarity = 2;
			int veinSize = 4;
			int height = 12;
			for (int i = 0; i < rarity; ++i)
			{
				int randomPosX = chunkX + par2Random.nextInt(16);
				int randomPosY = par2Random.nextInt(height);
				int randomPosZ = chunkZ + par2Random.nextInt(16);
				(new WorldGenMinable(MyMod.myOre.blockID, veinSize)).generate(par1world, par2Random, randomPosX, randomPosY, randomPosZ);
			}
		}
	}
	 */
}

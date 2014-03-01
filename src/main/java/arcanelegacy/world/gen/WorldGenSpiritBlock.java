package arcanelegacy.world.gen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import arcanelegacy.blocks.ALBlocks;

public class WorldGenSpiritBlock extends WorldGenerator
{
	public boolean generate(World world, Random rand, int x, int y, int z) {
		if (!world.isAirBlock(x, y, z)) {
			return false;
		} else if (world.getBlockId(x, y + 1, z) != Block.netherrack.blockID) {
			return false;
		} else {
			world.setBlock(x, y, z, ALBlocks.blockSpirit.blockID, 0, 2);
			for (int l = 0; l < 1500; ++l) {
				int i1 = x + rand.nextInt(8) - rand.nextInt(8);
				int j1 = y - rand.nextInt(12);
				int k1 = z + rand.nextInt(8) - rand.nextInt(8);

				if (world.getBlockId(i1, j1, k1) == 0) {
					int l1 = 0;
					for (int i2 = 0; i2 < 6; ++i2) {
						int j2 = 0;
						switch(i2) {
						case 0: j2 = world.getBlockId(i1 - 1, j1, k1); break;
						case 1: j2 = world.getBlockId(i1 + 1, j1, k1); break;
						case 2: j2 = world.getBlockId(i1, j1 - 1, k1); break;
						case 3: j2 = world.getBlockId(i1, j1 + 1, k1); break;
						case 4: j2 = world.getBlockId(i1, j1, k1 - 1); break;
						case 5: j2 = world.getBlockId(i1, j1, k1 + 1); break;
						}

						if (j2 == ALBlocks.blockSpirit.blockID) {
							++l1;
						}
					}

					if (l1 == 1) {
						world.setBlock(i1, j1, k1, ALBlocks.blockSpirit.blockID, 0, 2);
					}
				}
			}

			return true;
		}
	}
}

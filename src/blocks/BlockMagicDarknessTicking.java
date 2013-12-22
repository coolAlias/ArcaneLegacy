package coolalias.arcanelegacy.blocks;

import java.util.Random;

import net.minecraft.world.World;

public class BlockMagicDarknessTicking extends BlockMagicDarkness
{
	public BlockMagicDarknessTicking(int par1) {
		super(par1);
		this.setTickRandomly(true);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		int l = world.getBlockMetadata(x, y, z);
		if (l < 1) { world.setBlockMetadataWithNotify(x, y, z, 1, 2); }
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random par5Random)
	{
		super.updateTick(world, x, y, z, par5Random);

		int l = world.getBlockMetadata(x, y, z);

		if (l > 0)
		{
			--l;
			if (l == 0)
				world.setBlockToAir(x, y, z);
			else
				world.setBlockMetadataWithNotify(x, y, z, l, 2);
		}
		else
		{
			world.setBlock(x, y, z, ALBlocks.blockDarkness.blockID);
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int neighborID) {
		this.updateTick(world, x, y, z, world.rand);
	}

	@Override
	public int tickRate(World world) { return 60; }
}
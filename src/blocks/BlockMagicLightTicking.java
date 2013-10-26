package coolalias.arcanelegacy.blocks;

import java.util.Random;

import net.minecraft.world.World;
import coolalias.arcanelegacy.ArcaneLegacy;

public class BlockMagicLightTicking extends BlockMagicLight
{
	public BlockMagicLightTicking(int par1)
	{
		super(par1);
		this.setTickRandomly(true);
	}

	/** Called whenever the block is added into the world. Args: world, x, y, z */
	@Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		int l = world.getBlockMetadata(x, y, z);
		if (l < 1) { world.setBlockMetadataWithNotify(x, y, z, 1, 2); }
	}

	/** Ticks the block if it's been scheduled */
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
			world.setBlock(x, y, z, ALBlocks.blockLight.blockID);
		}
	}

	/** How many world ticks before ticking */
	@Override
	public int tickRate(World par1World)
	{
		return 10;
	}
}

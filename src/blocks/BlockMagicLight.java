package coolalias.arcanelegacy.blocks;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import coolalias.arcanelegacy.Config;

public class BlockMagicLight extends BlockGeneric
{
	public BlockMagicLight(int par1) {
		super(par1, Material.air);
		this.setLightValue(1.0F);
		this.setBlockBounds(0.45F, 0.45F, 0.45F, 0.55F, 0.55F, 0.55F);
		this.setResistance(5000.0F);
		this.setBlockUnbreakable();
		this.blockParticleGravity = 0.0F;
	}

	@Override
	public boolean isCollidable() { return false; }

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public boolean isOpaqueCube() { return false; }

	@Override
	public int quantityDropped(Random rand) { return 0; }

	@Override
	public boolean canDropFromExplosion(Explosion explosion) { return false; }

	/**
	 * Checks to see if its valid to put this block at the specified coordinates.
	 * Args: world, x, y, z
	 */
	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		int currentID = world.getBlockId(x, y, z);
		return (world.isAirBlock(x, y, z));
	}

	/**
	 * Checks to see if block at the specified coordinates is destroyed when attempting to place this block.
	 * Args: world, x, y, z
	 */
	public boolean canDestroyBlockAt(World world, int x, int y, int z) {
		int currentID = world.getBlockId(x, y, z);
		return (currentID == ALBlocks.blockDarkness.blockID || (Config.enableTickingBlocks() && currentID == ALBlocks.blockDarknessTicking.blockID));
	}
}
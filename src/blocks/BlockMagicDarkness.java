package coolalias.arcanelegacy.blocks;

import java.util.Random;

import coolalias.arcanelegacy.ArcaneLegacy;
import coolalias.arcanelegacy.Config;
import coolalias.arcanelegacy.item.ItemScroll;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMagicDarkness extends BlockGeneric
{
	public BlockMagicDarkness(int par1)
	{
		super(par1, Material.circuits);
		this.setLightOpacity(255);
		this.setResistance(5000.0F);
		this.setBlockUnbreakable();
		this.blockParticleGravity = 0.0F;
	}

	/** If false, block cannot be interacted with (i.e. blocks cannot place on it) */
	@Override
	public boolean isCollidable()
	{
		return false;
	}

	/**
	 * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
	 * cleared to be reused)
	 */
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
	{
		return null;
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
	 * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
	 */
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	/** Returns the quantity of items to drop on block destruction. */
	@Override
	public int quantityDropped(Random par1Random)
	{
		return 0;
	}

	/*
    @Override
    public boolean getBlocksMovement(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
    	System.out.println("[BLOCK] Overriding getBlocksMovement");
        return false;
    }
	 */

	/**
	 * Returns Returns true if the given side of this block type should be rendered (if it's solid or not), if the
	 * adjacent block is at the given coordinates. Args: blockAccess, x, y, z, side
	 */
	/*
    @Override
    public boolean isBlockSolid(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return true;
    }
	 */
	/** Return whether this block can drop from an explosion. */
	@Override
	public boolean canDropFromExplosion(Explosion par1Explosion)
	{
		return false;
	}

	/** Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z */
	/*
    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
        int currentID = world.getBlockId(x, y, z);
        return (world.isAirBlock(x, y, z) || !Block.blocksList[currentID].blockMaterial.blocksMovement());
    }
	 */

	/** Checks to see if block at the specified coordinates is destroyed when attempting to place this block. Args: world, x, y, z */
	public boolean canDestroyBlockAt(World world, int x, int y, int z)
	{
		int currentID = world.getBlockId(x, y, z);
		return (currentID == ALBlocks.blockLight.blockID || (Config.enableTickingBlocks() && currentID == ALBlocks.blockLightTicking.blockID));
	}
}

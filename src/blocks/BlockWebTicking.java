package coolalias.arcanelegacy.blocks;

import java.util.ArrayList;
import java.util.Random;

import coolalias.arcanelegacy.ArcaneLegacy;
import coolalias.arcanelegacy.ModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWeb;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

public class BlockWebTicking extends BlockWeb implements IShearable
{
	public BlockWebTicking(int par1)
	{
		super(par1);
		this.setTickRandomly(true);
		setLightOpacity(1);
		setHardness(4.0F);
		setCreativeTab(ArcaneLegacy.tabArcaneBlocks);
	}

	/** Called whenever the block is added into the world. Args: world, x, y, z */
	@Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		int l = world.getBlockMetadata(x, y, z);
		if (l < 1) { world.setBlockMetadataWithNotify(x, y, z, 1, 2); }
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
			world.setBlock(x, y, z, Block.web.blockID);
		}
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor blockID
	 */
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int neighborID)
	{
		this.updateTick(world, x, y, z, world.rand);
	}

	/** How many world ticks before ticking */
	@Override
	public int tickRate(World par1World)
	{
		return 60;
	}

	/** Returns the quantity of items to drop on block destruction. */
	@Override
	public int quantityDropped(Random par1Random)
	{
		return 0;
	}

	/** Return true if a player with Silk Touch can harvest this block directly, and not its normal drops. */
	@Override
	protected boolean canSilkHarvest()
	{
		return false;
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon(ModInfo.ID + ":block_web");
	}

	/*
	@Override
	public boolean isShearable(ItemStack item, World world, int x, int y, int z) {
		return (item != null && item.itemID == Item.shears.itemID && world.getBlockId(x, y, z) == ALBlocks.blockWebTicking.blockID);
	}
	 */
	@Override
	public boolean isShearable(ItemStack item, World world, int x, int y, int z)
	{
		System.out.println("[WEB] Checking is shearable?");
		return true;
	}

	@Override
	public ArrayList<ItemStack> onSheared(ItemStack item, World world, int x,
			int y, int z, int fortune) {
		return new ArrayList<ItemStack>();
	}

}

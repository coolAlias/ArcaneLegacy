package coolalias.arcanelegacy.blocks;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockWeb;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import coolalias.arcanelegacy.ArcaneLegacy;
import coolalias.arcanelegacy.ModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockWebTicking extends BlockWeb implements IShearable
{
	public BlockWebTicking(int par1) {
		super(par1);
		this.setTickRandomly(true);
		setLightOpacity(1);
		setHardness(4.0F);
		setCreativeTab(ArcaneLegacy.tabArcaneBlocks);
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
			world.setBlock(x, y, z, Block.web.blockID);
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int neighborID) {
		this.updateTick(world, x, y, z, world.rand);
	}

	@Override
	public int tickRate(World world) { return 60; }

	@Override
	public int quantityDropped(Random rand) { return 0; }

	@Override
	protected boolean canSilkHarvest() { return false; }

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister register) {
		this.blockIcon = register.registerIcon(ModInfo.ID + ":block_web");
	}

	// TODO shearing doesn't work
	@Override
	public boolean isShearable(ItemStack item, World world, int x, int y, int z) {
		return true;
	}

	@Override
	public ArrayList<ItemStack> onSheared(ItemStack item, World world, int x, int y, int z, int fortune) {
		return new ArrayList<ItemStack>();
	}
}
package arcanelegacy.blocks;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import arcanelegacy.ArcaneLegacy;
import arcanelegacy.ModInfo;
import arcanelegacy.tileentity.TileEntityArcaneInscriber;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockArcaneInscriber extends BlockContainer
{
	private final boolean isActive;

	private static boolean keepInventory;

	@SideOnly(Side.CLIENT)
	private Icon inscriberIconTop;

	@SideOnly(Side.CLIENT)
	private Icon inscriberIconFront;

	public BlockArcaneInscriber(int id, boolean active) {
		super(id, Material.rock);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
		this.setLightOpacity(0);
		setUnlocalizedName("block_arcane_inscriber");
		this.isActive = active;
	}

	@Override
	public int idDropped(int id, Random random, int meta) {
		return ALBlocks.arcaneInscriberIdle.blockID;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int idPicked(World world, int x, int y, int z) {
		return ALBlocks.arcaneInscriberIdle.blockID;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
	{
		if (!world.isRemote)
		{
			TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
			if (tileEntity == null || player.isSneaking()) {
				return false;
			}
			player.openGui(ArcaneLegacy.instance, ArcaneLegacy.arcaneInscriberGuiId, world, x, y, z);
		}
		return true;
	}

	/**
	 * Update which block ID the inscriber is using depending on whether or not it is burning
	 */
	public static void updateInscriberBlockState(boolean active, World world, int x, int y, int z)
	{
		int l = world.getBlockMetadata(x, y, z);
		TileEntity tileentity = world.getBlockTileEntity(x, y, z);
		keepInventory = true;

		if (active) {
			world.setBlock(x, y, z, ALBlocks.arcaneInscriberActive.blockID);
		} else {
			world.setBlock(x, y, z, ALBlocks.arcaneInscriberIdle.blockID);
		}

		keepInventory = false;
		world.setBlockMetadataWithNotify(x, y, z, l, 2);

		if (tileentity != null)
		{
			tileentity.validate();
			world.setBlockTileEntity(x, y, z, tileentity);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random random)
	{
		if (this.isActive)
		{
			float spawnX = (float) x + 1.0F - random.nextFloat();
			float spawnY = (float) y + 0.7F + random.nextFloat();
			float spawnZ = (float) z + 1.0F - random.nextFloat();

			world.spawnParticle("magicCrit", spawnX, spawnY, spawnZ, 0.0D, 0.35D, 0.0D);
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityArcaneInscriber();
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int meta)
	{
		if (!keepInventory)
		{
			TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
			if (!(tileEntity instanceof IInventory)) { return; }
			IInventory inventory = (IInventory) tileEntity;

			for (int i = 0; i < inventory.getSizeInventory(); ++i)
			{
				ItemStack itemstack = inventory.getStackInSlotOnClosing(i);

				if (itemstack != null)
				{
					float spawnX = x + world.rand.nextFloat();
					float spawnY = y + world.rand.nextFloat();
					float spawnZ = z + world.rand.nextFloat();

					EntityItem entityitem = new EntityItem(world, spawnX, spawnY, spawnZ, itemstack);

					float f3 = 0.05F;
					entityitem.motionX = (-0.5F + world.rand.nextGaussian()) * f3;
					entityitem.motionY = (4 + world.rand.nextGaussian()) * f3;
					entityitem.motionZ = (-0.5F + world.rand.nextGaussian()) * f3;

					world.spawnEntityInWorld(entityitem);
				}
			}
		}

		super.breakBlock(world, x, y, z, id, meta);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int side, int meta) {
		return side == 1 ? this.inscriberIconTop : (side == 0 ? this.inscriberIconTop : this.blockIcon);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.blockIcon = par1IconRegister.registerIcon(ModInfo.ID + ":arcane_inscriber_side");
		this.inscriberIconTop = par1IconRegister.registerIcon(this.isActive ? (ModInfo.ID + ":arcane_inscriber_top_active") : (ModInfo.ID + ":arcane_inscriber_top_idle"));
	}
}
package coolalias.arcanelegacy.blocks;

import java.util.Random;

import coolalias.arcanelegacy.ArcaneLegacy;
import coolalias.arcanelegacy.ModInfo;
import coolalias.arcanelegacy.tileentity.TileEntityArcaneInscriber;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockArcaneInscriber extends BlockContainer
{
	private final boolean isActive;

	/**
	 * This flag is used to prevent the inscriber inventory to be dropped upon block removal, is used internally when the
	 * inscriber block changes from idle to active and vice-versa.
	 */
	private static boolean keepInventory;
	@SideOnly(Side.CLIENT)
	private Icon inscriberIconTop;
	@SideOnly(Side.CLIENT)
	private Icon inscriberIconFront;

	public BlockArcaneInscriber(int id, boolean active)
	{
		super(id, Material.rock);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
		this.setLightOpacity(0);
		setUnlocalizedName("block_arcane_inscriber");
		this.isActive = active;
	}

	/**
	 * Returns the ID of the items to drop on destruction.
	 */
	@Override
	public int idDropped(int id, Random random, int meta) {
		return ALBlocks.arcaneInscriberIdle.blockID;
	}

	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public int idPicked(World world, int x, int y, int z) {
		return ALBlocks.arcaneInscriberIdle.blockID;
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		super.onBlockAdded(world, x, y, z);
		//this.setDefaultDirection(world, x, y, z);
	}

	/**
	 * set a blocks direction
	 */
	private void setDefaultDirection(World world, int x, int y, int z)
	{
		if (!world.isRemote)
		{
			int l = world.getBlockId(x, y, z - 1);
			int i1 = world.getBlockId(x, y, z + 1);
			int j1 = world.getBlockId(x - 1, y, z);
			int k1 = world.getBlockId(x + 1, y, z);
			byte direction = 3;

			if (Block.opaqueCubeLookup[l] && !Block.opaqueCubeLookup[i1]) { direction = 3; }
			if (Block.opaqueCubeLookup[i1] && !Block.opaqueCubeLookup[l]) { direction = 2; }
			if (Block.opaqueCubeLookup[j1] && !Block.opaqueCubeLookup[k1]) { direction = 5; }
			if (Block.opaqueCubeLookup[k1] && !Block.opaqueCubeLookup[j1]) { direction = 4; }

			world.setBlockMetadataWithNotify(x, y, z, direction, 2);
		}
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
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

	/**
	 * A randomly called display update to be able to add particles or other items for display
	 */
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

	/**
	 * Returns a new instance of a block's tile entity class. Called on placing the block.
	 */
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityArcaneInscriber();
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
	 * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
	 */
	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	/**
	 * Called when the block is placed in the world by an entity.
	 */
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemstack)
	{
		/*
		int facing = MathHelper.floor_double((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		
		switch(facing) {
		case 0: world.setBlockMetadataWithNotify(x, y, z, 2, 2); break;
		case 1: world.setBlockMetadataWithNotify(x, y, z, 5, 2); break;
		case 2: world.setBlockMetadataWithNotify(x, y, z, 3, 2); break;
		case 3: world.setBlockMetadataWithNotify(x, y, z, 4, 2); break;
		}
		*/
		if (itemstack.hasDisplayName()) {
			((TileEntityArcaneInscriber) world.getBlockTileEntity(x, y, z)).setGuiDisplayName(itemstack.getDisplayName());
		}
	}

	/**
	 * Ejects contained items into the world, and notifies neighbours of an update, as appropriate
	 */
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
		// return par1 == 1 ? this.inscriberIconTop : (par1 == 0 ? this.inscriberIconTop : (par1 != par2 ? this.blockIcon : this.inscriberIconFront));
		return side == 1 ? this.inscriberIconTop : (side == 0 ? this.inscriberIconTop : this.blockIcon);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon(ModInfo.ID + ":arcane_inscriber_side");
		this.inscriberIconTop = par1IconRegister.registerIcon(this.isActive ? (ModInfo.ID + ":arcane_inscriber_top_active") : (ModInfo.ID + ":arcane_inscriber_top_idle"));
		//this.inscriberIconFront = par1IconRegister.registerIcon(ModInfo.ID + ":arcane_inscriber_side");
	}
}

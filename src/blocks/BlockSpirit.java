package coolalias.arcanelegacy.blocks;

import java.util.Random;

import coolalias.arcanelegacy.item.ALItems;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.MathHelper;

public class BlockSpirit extends BlockGeneric
{
	public BlockSpirit(int par1, Material par2Material)
	{
		super(par1, par2Material);
		setHardness(1.0F);
		setLightValue(1.0F);
		setResistance(5.0F);
		setStepSound(Block.soundStoneFootstep);
	}

	/**
	 * Returns the usual quantity dropped by the block plus a bonus of 1 to 'i' (inclusive).
	 */
	public int quantityDroppedWithBonus(int par1, Random par2Random)
	{
		return MathHelper.clamp_int(this.quantityDropped(par2Random) + par2Random.nextInt(par1 + 1), 1, 4);
	}

	/**
	 * Returns the quantity of items to drop on block destruction.
	 */
	public int quantityDropped(Random par1Random)
	{
		return 2 + par1Random.nextInt(3);
	}

	public int idDropped(int par1, Random par2Random, int par3)
	{
		return ALItems.spiritShard.itemID;
	}
}

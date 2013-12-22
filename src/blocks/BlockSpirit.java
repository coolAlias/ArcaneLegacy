package coolalias.arcanelegacy.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.MathHelper;
import coolalias.arcanelegacy.item.ALItems;

public class BlockSpirit extends BlockGeneric
{
	public BlockSpirit(int par1, Material material) {
		super(par1, material);
		setHardness(1.0F);
		setLightValue(1.0F);
		setResistance(5.0F);
		setStepSound(Block.soundStoneFootstep);
	}

	@Override
	public int quantityDroppedWithBonus(int bonus, Random rand) {
		return MathHelper.clamp_int(this.quantityDropped(rand) + rand.nextInt(bonus + 1), 1, 4);
	}

	@Override
	public int quantityDropped(Random rand) {
		return 2 + rand.nextInt(3);
	}

	@Override
	public int idDropped(int par1, Random rand, int par3) {
		return ALItems.spiritShard.itemID;
	}
}
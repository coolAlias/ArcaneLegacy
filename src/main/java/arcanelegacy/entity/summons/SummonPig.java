package arcanelegacy.entity.summons;

import net.minecraft.entity.passive.EntityPig;
import net.minecraft.world.World;
import arcanelegacy.Config;

public class SummonPig extends EntityPig
{
	private int lifespan;

	public SummonPig(World par1World)
	{
		this(par1World, Config.baseSummonDuration());
	}

	public SummonPig(World par1World, int lifespan)
	{
		super(par1World);
		this.setLifespan(lifespan);
	}

	public SummonPig setLifespan(int par1)
	{
		this.lifespan = (par1 != 0 ? par1 : Config.baseSummonDuration());
		return this;
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	@Override
	public void onLivingUpdate()
	{
		if (!this.worldObj.isRemote) { 
			if (lifespan > 0) { --lifespan; }
			if (lifespan == 0) {
				this.worldObj.removeEntity(this);
			}
		}
		super.onLivingUpdate();
	}
}

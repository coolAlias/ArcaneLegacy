package coolalias.arcanelegacy.entity.ai;

import coolalias.arcanelegacy.entity.summons.ISummonedCreature;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIFollowSummoner extends EntityAIBase
{
	private final ISummonedCreature summoned;
	private EntityCreature summonedCreature;
	private EntityLivingBase owner;
	World theWorld;
	private double field_75336_f;
	private final PathNavigate petPathfinder;
	private int field_75343_h;
	float maxDist;
	float minDist;
	private boolean avoidsWater;

	public EntityAIFollowSummoner(ISummonedCreature par1Summoned, EntityLivingBase par2Owner, double par3, float min, float max)
	{
		this.summoned = par1Summoned;
		this.summonedCreature = ((EntityCreature) par1Summoned);
		this.theWorld = ((EntityCreature) par1Summoned).worldObj;
		this.field_75336_f = par3;
		this.petPathfinder = ((EntityCreature) par1Summoned).getNavigator();
		this.minDist = min;
		this.maxDist = max;
		this.setMutexBits(3);
	}

	@Override
	public boolean shouldExecute()
	{
		EntityLivingBase owner = this.summoned.getOwner();

		if (owner == null || this.summoned.isSitting() || this.summonedCreature.getDistanceSqToEntity(owner)
				< (double)(this.minDist * this.minDist)) { return false; }

		this.owner = owner;
		return true;
	}


	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean continueExecuting()
	{
		return !this.petPathfinder.noPath() && this.summonedCreature.getDistanceSqToEntity(this.owner) > (double)(this.maxDist * this.maxDist) && !this.summoned.isSitting();
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting()
	{
		this.field_75343_h = 0;
		this.avoidsWater = this.summonedCreature.getNavigator().getAvoidsWater();
		this.summonedCreature.getNavigator().setAvoidsWater(false);
	}

	/**
	 * Resets the task
	 */
	public void resetTask()
	{
		this.owner = null;
		this.petPathfinder.clearPathEntity();
		this.summonedCreature.getNavigator().setAvoidsWater(this.avoidsWater);
	}

	/**
	 * Updates the task
	 */
	public void updateTask()
	{
		boolean isSitting = false;
		this.summonedCreature.getLookHelper().setLookPositionWithEntity(this.owner, 10.0F, (float)this.summonedCreature.getVerticalFaceSpeed());

		if (!this.summoned.isSitting())
		{
			if (--this.field_75343_h <= 0)
			{
				this.field_75343_h = 10;

				if (!this.petPathfinder.tryMoveToEntityLiving(this.owner, this.field_75336_f))
				{
					if (!this.summonedCreature.getLeashed())
					{
						if (this.summonedCreature.getDistanceSqToEntity(this.owner) >= 144.0D)
						{
							int i = MathHelper.floor_double(this.owner.posX) - 2;
							int j = MathHelper.floor_double(this.owner.posZ) - 2;
							int k = MathHelper.floor_double(this.owner.boundingBox.minY);

							for (int l = 0; l <= 4; ++l)
							{
								for (int i1 = 0; i1 <= 4; ++i1)
								{
									if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.theWorld.doesBlockHaveSolidTopSurface(i + l, k - 1, j + i1) && !this.theWorld.isBlockNormalCube(i + l, k, j + i1) && !this.theWorld.isBlockNormalCube(i + l, k + 1, j + i1))
									{
										this.summonedCreature.setLocationAndAngles((double)((float)(i + l) + 0.5F), (double)k, (double)((float)(j + i1) + 0.5F), this.summonedCreature.rotationYaw, this.summonedCreature.rotationPitch);
										this.petPathfinder.clearPathEntity();
										return;
									}
								}
							}
						}
					}
				}
			}
		}
	}
}

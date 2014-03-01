package arcanelegacy.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import arcanelegacy.entity.summons.ISummonedCreature;

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

	public EntityAIFollowSummoner(ISummonedCreature par1Summoned, EntityLivingBase par2Owner, double par3, float min, float max) {
		summoned = par1Summoned;
		summonedCreature = ((EntityCreature) par1Summoned);
		theWorld = ((EntityCreature) par1Summoned).worldObj;
		field_75336_f = par3;
		petPathfinder = ((EntityCreature) par1Summoned).getNavigator();
		minDist = min;
		maxDist = max;
		setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {
		EntityLivingBase owner = summoned.getOwner();
		if (owner == null || summoned.isSitting() || summonedCreature.getDistanceSqToEntity(owner) < (minDist * minDist)) {
			return false;
		}
		this.owner = owner;
		return true;
	}


	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean continueExecuting() {
		return !petPathfinder.noPath() && summonedCreature.getDistanceSqToEntity(owner) > (double)(maxDist * maxDist) && !summoned.isSitting();
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting() {
		field_75343_h = 0;
		avoidsWater = summonedCreature.getNavigator().getAvoidsWater();
		summonedCreature.getNavigator().setAvoidsWater(false);
	}

	/**
	 * Resets the task
	 */
	public void resetTask() {
		owner = null;
		petPathfinder.clearPathEntity();
		summonedCreature.getNavigator().setAvoidsWater(avoidsWater);
	}

	/**
	 * Updates the task
	 */
	public void updateTask()
	{
		//boolean isSitting = false;
		summonedCreature.getLookHelper().setLookPositionWithEntity(owner, 10.0F, summonedCreature.getVerticalFaceSpeed());
		if (!summoned.isSitting()) {
			if (--field_75343_h <= 0) {
				field_75343_h = 10;
				if (!petPathfinder.tryMoveToEntityLiving(owner, field_75336_f)) {
					if (!summonedCreature.getLeashed()) {
						if (summonedCreature.getDistanceSqToEntity(owner) >= 144.0D) {
							int i = MathHelper.floor_double(owner.posX) - 2;
							int j = MathHelper.floor_double(owner.posZ) - 2;
							int k = MathHelper.floor_double(owner.boundingBox.minY);

							for (int l = 0; l <= 4; ++l) {
								for (int i1 = 0; i1 <= 4; ++i1) {
									if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && theWorld.doesBlockHaveSolidTopSurface(i + l, k - 1, j + i1) && !theWorld.isBlockNormalCube(i + l, k, j + i1) && !theWorld.isBlockNormalCube(i + l, k + 1, j + i1)) {
										summonedCreature.setLocationAndAngles((i + l) + 0.5F, k, (j + i1) + 0.5F, summonedCreature.rotationYaw, summonedCreature.rotationPitch);
										petPathfinder.clearPathEntity();
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

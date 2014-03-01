package arcanelegacy.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import arcanelegacy.entity.summons.ISummonedCreature;

public class EntityAISummonSit extends EntityAIBase
{
	private ISummonedCreature theEntity;

	/** If the EntitySummoned is sitting. */
	private boolean isSitting;

	public EntityAISummonSit(ISummonedCreature creature) {
		theEntity = creature;
		setMutexBits(5);
	}

	@Override
	public boolean shouldExecute() {
		if (!theEntity.isTamed()) {
			return false;
		} else if (((EntityLivingBase) theEntity).isInWater()) {
			return false;
		} else if (!((EntityLivingBase) theEntity).onGround) {
			return false;
		} else {
			EntityLivingBase entitylivingbase = (EntityLivingBase) theEntity.getOwner();
			return entitylivingbase == null ? true : (((EntityLivingBase) theEntity).getDistanceSqToEntity(entitylivingbase) < 144.0D && entitylivingbase.getAITarget() != null ? false : isSitting);
		}
	}

	@Override
	public void startExecuting() {
		((EntityCreature) theEntity).getNavigator().clearPathEntity();
		theEntity.setSitting(true);
	}

	@Override
	public void resetTask() {
		theEntity.setSitting(false);
	}

	/**
	 * Sets the sitting flag.
	 */
	public void setSitting(boolean par1) {
		isSitting = par1;
	}
}

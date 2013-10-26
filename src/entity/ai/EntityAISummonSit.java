package coolalias.arcanelegacy.entity.ai;

import coolalias.arcanelegacy.entity.summons.ISummonedCreature;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAISummonSit extends EntityAIBase
{
	private ISummonedCreature theEntity;

	/** If the EntitySummoned is sitting. */
	private boolean isSitting;

	public EntityAISummonSit(ISummonedCreature creature) {
		this.theEntity = creature;
		this.setMutexBits(5);
	}

	@Override
	public boolean shouldExecute()
	{
		if (!this.theEntity.isTamed())
		{
			return false;
		}
		else if (((EntityLivingBase) this.theEntity).isInWater())
		{
			return false;
		}
		else if (!((EntityLivingBase) this.theEntity).onGround)
		{
			return false;
		}
		else
		{
			EntityLivingBase entitylivingbase = (EntityLivingBase) this.theEntity.getOwner();
			return entitylivingbase == null ? true : (((EntityLivingBase) this.theEntity).getDistanceSqToEntity(entitylivingbase) < 144.0D && entitylivingbase.getAITarget() != null ? false : this.isSitting);
		}
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	 public void startExecuting()
	 {
		 ((EntityCreature) this.theEntity).getNavigator().clearPathEntity();
		 this.theEntity.setSitting(true);
	 }

	 /**
	  * Resets the task
	  */
	 public void resetTask()
	 {
		 this.theEntity.setSitting(false);
	 }

	 /**
	  * Sets the sitting flag.
	  */
	 public void setSitting(boolean par1)
	 {
		 this.isSitting = par1;
	 }

}

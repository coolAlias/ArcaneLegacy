package coolalias.arcanelegacy.entity.ai;

import coolalias.arcanelegacy.entity.summons.ISummonedCreature;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

public class EntityAISummonerHurtByTarget extends EntityAITarget
{
	ISummonedCreature summoned;
	EntityLivingBase owner;
	private int field_142051_e;

	public EntityAISummonerHurtByTarget(ISummonedCreature creature)
	{
		super((EntityCreature) creature, false);
		this.summoned = creature;
		this.setMutexBits(1);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute()
	{
		EntityLivingBase entitylivingbase = this.summoned.getOwner();

		if (entitylivingbase == null)
		{
			return false;
		}
		else
		{
			this.owner = entitylivingbase.getAITarget();
			int i = entitylivingbase.func_142015_aE();
			return i != this.field_142051_e && this.isSuitableTarget(this.owner, false);
		}
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting()
	{
		this.taskOwner.setAttackTarget(this.owner);
		EntityLivingBase entitylivingbase = this.summoned.getOwner();

		if (entitylivingbase != null)
		{
			this.field_142051_e = entitylivingbase.func_142015_aE();
		}

		super.startExecuting();
	}
}

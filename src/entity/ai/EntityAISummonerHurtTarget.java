package coolalias.arcanelegacy.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import coolalias.arcanelegacy.entity.summons.ISummonedCreature;

/**
 * Called when Summoner damages a target
 */
public class EntityAISummonerHurtTarget extends EntityAITarget
{
	ISummonedCreature summoned;
	EntityLivingBase theTarget;
	private int lastAttackerTime;

	public EntityAISummonerHurtTarget(ISummonedCreature creature)
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
		EntityLivingBase owner = this.summoned.getOwner();

		if (owner != null)
		{
			this.theTarget = owner.getLastAttacker();
			int i = owner.getLastAttackerTime();
			return i != this.lastAttackerTime && this.isSuitableTarget(this.theTarget, false);
		}

		return false;
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting()
	{
		this.taskOwner.setAttackTarget(this.theTarget);
		EntityLivingBase owner = this.summoned.getOwner();

		if (owner != null) { this.lastAttackerTime = owner.getLastAttackerTime(); }

		super.startExecuting();
	}
}

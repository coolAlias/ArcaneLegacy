package arcanelegacy.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import arcanelegacy.entity.summons.ISummonedCreature;

/**
 * Called when Summoner damages a target
 */
public class EntityAISummonerHurtTarget extends EntityAITarget
{
	ISummonedCreature summoned;
	EntityLivingBase theTarget;
	private int lastAttackerTime;

	public EntityAISummonerHurtTarget(ISummonedCreature creature) {
		super((EntityCreature) creature, false);
		summoned = creature;
		setMutexBits(1);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		EntityLivingBase owner = summoned.getOwner();
		if (owner != null) {
			theTarget = owner.getLastAttacker();
			int i = owner.getLastAttackerTime();
			return i != lastAttackerTime && isSuitableTarget(theTarget, false);
		}
		return false;
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting() {
		taskOwner.setAttackTarget(theTarget);
		EntityLivingBase owner = summoned.getOwner();
		if (owner != null) {
			lastAttackerTime = owner.getLastAttackerTime();
		}
		super.startExecuting();
	}
}

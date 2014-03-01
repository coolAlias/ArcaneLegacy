package arcanelegacy.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import arcanelegacy.entity.summons.ISummonedCreature;

public class EntityAISummonerHurtByTarget extends EntityAITarget
{
	ISummonedCreature summoned;
	EntityLivingBase owner;
	private int field_142051_e;

	public EntityAISummonerHurtByTarget(ISummonedCreature creature) {
		super((EntityCreature) creature, false);
		summoned = creature;
		setMutexBits(1);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		EntityLivingBase entitylivingbase = summoned.getOwner();
		if (entitylivingbase == null) {
			return false;
		} else {
			owner = entitylivingbase.getAITarget();
			int i = entitylivingbase.func_142015_aE();
			return i != field_142051_e && isSuitableTarget(owner, false);
		}
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting() {
		taskOwner.setAttackTarget(owner);
		EntityLivingBase entitylivingbase = summoned.getOwner();
		if (entitylivingbase != null) {
			field_142051_e = entitylivingbase.func_142015_aE();
		}
		super.startExecuting();
	}
}

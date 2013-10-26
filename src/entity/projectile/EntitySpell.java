package coolalias.arcanelegacy.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import coolalias.arcanelegacy.item.ItemScroll;
import coolalias.arcanelegacy.spells.SpellUtils;

public class EntitySpell extends EntityThrowable
{
	protected ItemStack scroll = null;

	public EntitySpell(World world) {
		super(world);
	}

	public EntitySpell(World world, EntityLivingBase player) {
		super(world, player);
	}

	public EntitySpell(World world, double par2, double par4, double par6) {
		super(world, par2, par4, par6);
	}

	public ItemScroll getScroll() {
		return (ItemScroll) scroll.getItem();
	}

	public EntitySpell setScroll(ItemStack scroll) {
		this.scroll = scroll;
		return this;
	}

	@Override
	protected float getGravityVelocity() {
		return 0;
	}

	/**
	 * Velocity of entity; default value 1.5F for ThrowableEntity
	 */
	@Override
	protected float func_70182_d() {
		return 2.0F;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		
		if (!isDead && !inGround)
		{
			float f4 = 0.25F;
			
			for (int k = 0; k < 4; ++k) {
				worldObj.spawnParticle("crit", posX - motionX * f4, posY - motionY * f4, posZ - motionZ * f4, motionX, motionY, motionZ);
			}
		}
	}

	@Override
	protected void onImpact(MovingObjectPosition movingobjectposition)
	{
		if (!worldObj.isRemote) {
			SpellUtils.defaultSpell(scroll, worldObj, (EntityLivingBase) getThrower(), movingobjectposition, false);
			setDead();
		}
	}
}


package coolalias.arcanelegacy.entity.projectile;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntitySpellBanish extends EntitySpell
{

	public EntitySpellBanish(World par1World) {
		super(par1World);
		// TODO Auto-generated constructor stub
	}

	public EntitySpellBanish(World par1World, EntityLivingBase par2Caster) {
		super(par1World, par2Caster);
		// TODO Auto-generated constructor stub
	}

	public EntitySpellBanish(World par1World, double par2, double par4,
			double par6) {
		super(par1World, par2, par4, par6);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onImpact(MovingObjectPosition movingobjectposition)
	{
		if (!this.worldObj.isRemote)
		{
			super.onImpact(movingobjectposition);
			this.setDead();
		}
		/*
		Random rand = new Random();
		float factor = 0.05F;
		if (!this.worldObj.isRemote)
		{ 
			Entity target = movingobjectposition.entityHit;

			if (target != null && (target instanceof EntityMob || target instanceof EntityAnimal) && this.worldObj.rand.nextFloat() < this.scroll.getChance(0))
			{
				// check if spell matches mob ID
				// target.setDead();
				this.worldObj.removeEntity(target);
			}
			
			// play sound here
			this.setDead();
		}
		*/
		/* For some reason particles only spawn some of the time...
		 * - Always works when impacting tile entity
		 * - Rarely works when removing a target EntityLivingBase
		 * - Tried putting this.setDead() after spawning particles, no change.
		 * - Tried spawning particles before if(!this.worldObj.isRemote), no change.
		 * - Tried spawning particles within if(!this.worldObj.isRemote), but
		 *   then they don't spawn even when hitting tile entities
		 * - Tried changing to 'target.setDead()' same problem
		 */
		/*
		for (int i = 0; i < 8; ++i) {
			float rx = rand.nextFloat() * 0.8F + 0.1F;
			float ry = rand.nextFloat() * 0.8F + 0.1F;
			float rz = rand.nextFloat() * 0.8F + 0.1F;
			this.worldObj.spawnParticle("largesmoke", this.posX + rx, this.posY + ry, this.posZ + rz, 0.0D, 0.0D, 0.0D); //, rand.nextGaussian() * factor, rand.nextGaussian() * factor + 0.2F, rand.nextGaussian() * factor);
		}
		*/
	}
}

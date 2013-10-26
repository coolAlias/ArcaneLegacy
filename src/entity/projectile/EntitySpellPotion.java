package coolalias.arcanelegacy.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntitySpellPotion extends EntitySpell
{
	public EntitySpellPotion(World par1World) {
		super(par1World);
		// TODO Auto-generated constructor stub
	}

	public EntitySpellPotion(World par1World, EntityLivingBase par2Caster) {
		super(par1World, par2Caster);
		// TODO Auto-generated constructor stub
	}

	public EntitySpellPotion(World par1World, double par2, double par4,
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
		if (!this.worldObj.isRemote)
		{
			EntityLivingBase target = (movingobjectposition.entityHit instanceof EntityLivingBase ? (EntityLivingBase)movingobjectposition.entityHit : null);
			if (target != null)
			{
				for (int i = 0; i < scroll.getNumEffects(); ++i)
				{
					if (Potion.potionTypes[scroll.getEffectID(i)].isInstant())
					{
						System.out.println("[SPELL] Potion effect is instant");
						// 4th argument is %damage based on distance
						Potion.potionTypes[scroll.getEffectID(i)].affectEntity(this.getThrower(), target, scroll.getAmplifier(i), 1.0D);
					}
					else
					{
						target.addPotionEffect(new PotionEffect(scroll.getEffectID(i), scroll.getDuration(i), scroll.getAmplifier(i), false));
					}
				}
			}
			this.setDead();
		}
		*/
	}
}

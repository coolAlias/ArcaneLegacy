package coolalias.arcanelegacy.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import coolalias.arcanelegacy.spells.SpellUtils;

public class EntitySpellBeam extends EntitySpell
{
	private int duration;
	
	public EntitySpellBeam(World par1World) {
		super(par1World);
	}

	public EntitySpellBeam(World par1World, EntityLivingBase par2Player) {
		super(par1World, par2Player);
	}

	public EntitySpellBeam(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
	}
	
	@Override
    protected float func_70182_d() {
        return 0.5F;
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
		if (!this.worldObj.isRemote) {
			SpellUtils.defaultSpell(scroll, worldObj, (EntityLivingBase) getThrower(), movingobjectposition, false);
			setDead();
		}
	}

}

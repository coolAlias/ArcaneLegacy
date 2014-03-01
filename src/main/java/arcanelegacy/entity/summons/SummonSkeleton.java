package arcanelegacy.entity.summons;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.world.World;
import arcanelegacy.Config;
import arcanelegacy.entity.ai.EntityAIFollowSummoner;
import arcanelegacy.entity.ai.EntityAISummonSit;
import arcanelegacy.entity.ai.EntityAISummonerHurtByTarget;
import arcanelegacy.entity.ai.EntityAISummonerHurtTarget;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SummonSkeleton extends EntitySkeleton implements IRangedAttackMob, ISummonedCreature
{
	private final String name = "Skeleton";

	private int lifespan;

	private EntityPlayer owner;

	protected EntityAISummonSit aiSit = new EntityAISummonSit(this);

	public SummonSkeleton(World par1World) {
		this(par1World, null, Config.baseSummonDuration());
	}

	public SummonSkeleton(World world, EntityPlayer player, int lifespan) {
		super(world);
		setLifespan(lifespan);
		setCurrentItemOrArmor(0, new ItemStack(Item.bow));
		if (player != null) { setOwner(player.username); }
		owner = player;
		setTamed(true);

		// Set new AI tasks for Summoned Creature
		tasks.addTask(2, aiSit); // overrides AIRestrictSun
		if (owner != null) {
			tasks.addTask(4, new EntityAIFollowSummoner(this, owner, 1.0D, 10.0F, 2.0F));
		}
		// Reset default target AI
		targetTasks.taskEntries.clear();
		targetTasks.addTask(1, new EntityAISummonerHurtByTarget(this));
		targetTasks.addTask(2, new EntityAISummonerHurtTarget(this));
		targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
		targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityMob.class, 0, true));
	}

	@Override
	protected final void entityInit() {
		super.entityInit();
		dataWatcher.addObject(16, Byte.valueOf((byte) 0));
		dataWatcher.addObject(17, "");
	}

	@Override
	public final void onLivingUpdate() {
		if (!worldObj.isRemote) { 
			if (lifespan > 0) { --lifespan; }
			if (lifespan == 0) { worldObj.removeEntity(this); }
		}
		super.onLivingUpdate();
	}

	@Override
	public boolean interact(EntityPlayer player) {
		if (player.getCommandSenderName().equalsIgnoreCase(getOwnerName()) && !worldObj.isRemote) {
			aiSit.setSitting(!isSitting());
			isJumping = false;
			setPathToEntity((PathEntity) null);
			setTarget((Entity) null);
			setAttackTarget((EntityLivingBase) null);
		}
		return super.interact(player);
	}

	@Override
	public Team getTeam() {
		if (isTamed()) {
			EntityLivingBase entitylivingbase = getOwner();
			if (entitylivingbase != null) {
				return entitylivingbase.getTeam();
			}
		}
		return super.getTeam();
	}

	@Override
	public boolean isOnSameTeam(EntityLivingBase entity) {
		if (isTamed()) {
			EntityLivingBase entitylivingbase1 = getOwner();
			if (entity == entitylivingbase1) {
				return true;
			}
			if (entitylivingbase1 != null) {
				return entitylivingbase1.isOnSameTeam(entity);
			}
		}
		return super.isOnSameTeam(entity);
	}

	@Override
	public final void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		if (getOwnerName() == null) {
			compound.setString("Owner", "");
		} else {
			compound.setString("Owner", getOwnerName());
		}
		compound.setBoolean("Sitting", isSitting());
		compound.setInteger("Lifespan", lifespan);
	}

	@Override
	public final void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		String s = compound.getString("Owner");
		if (s.length() > 0) {
			setOwner(s);
			setTamed(true);
		}
		aiSit.setSitting(compound.getBoolean("Sitting"));
		setSitting(compound.getBoolean("Sitting"));
		setLifespan(compound.getInteger("Lifespan"));
	}

	@Override
	public final void attackEntityWithRangedAttack(EntityLivingBase entity, float par2) {
		EntityArrow arrow = new EntityArrow(worldObj, this, entity, 1.6F, (14 - worldObj.difficultySetting * 4));
		int i = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, getHeldItem());
		int j = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, getHeldItem());
		// arrow.setDamage((double)(par2 * 2.0F) + rand.nextGaussian() * 0.25D + (double)((float)worldObj.difficultySetting * 0.11F));
		arrow.setDamage(2.0F);	// takes 6 hits to kill a zombie at 2.0F

		if (i > 0) {
			arrow.setDamage(arrow.getDamage() + i * 0.5D + 0.5D);
		}
		if (j > 0) {
			arrow.setKnockbackStrength(j);
		}
		if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, getHeldItem()) > 0 || getSkeletonType() == 1) {
			arrow.setFire(100);
		}

		playSound("random.bow", 1.0F, 1.0F / (getRNG().nextFloat() * 0.4F + 0.8F));
		worldObj.spawnEntityInWorld(arrow);
	}
	
	@Override
    public boolean canAttackClass(Class par1Class) {
    	return true;
    }

	@Override
	@SideOnly(Side.CLIENT)
	public boolean getAlwaysRenderNameTagForRender() {
		return true;
	}

	@Override
	public String getTranslatedEntityName() {
		return ScorePlayerTeam.formatPlayerName(getTeam(), getOwnerName() + "'s " + name);
	}

	@Override
	public final void setLifespan(int par1) {
		lifespan = (par1 != 0 ? par1 : Config.baseSummonDuration());
	}

	@Override
	public final boolean isTamed() {
		return (dataWatcher.getWatchableObjectByte(16) & 4) != 0;
	}

	@Override
	public final void setTamed(boolean par1) {
		byte b0 = dataWatcher.getWatchableObjectByte(16);
		if (par1) {
			dataWatcher.updateObject(16, Byte.valueOf((byte)(b0 | 4)));
		} else {
			dataWatcher.updateObject(16, Byte.valueOf((byte)(b0 & -5)));
		}

		/*
        if (par1)
        {
        	// Sets health to 20
            func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(20.0D);
        }
		 */
	}

	@Override
	public boolean isSitting() {
		return (dataWatcher.getWatchableObjectByte(16) & 1) != 0;
	}

	@Override
	public void setSitting(boolean par1) {
		byte b0 = dataWatcher.getWatchableObjectByte(16);
		if (par1) {
			dataWatcher.updateObject(16, Byte.valueOf((byte)(b0 | 1)));
		} else {
			dataWatcher.updateObject(16, Byte.valueOf((byte)(b0 & -2)));
		}
	}

	/**
	 * Sets owner's name and adds AI to follow owner
	 */
	public final void setOwner(String name) {
		dataWatcher.updateObject(17, name);
		if (name.length() > 0) {
			tasks.addTask(4, new EntityAIFollowSummoner(this, getOwner(), 1.0D, 10.0F, 2.0F));
		}
	}

	@Override
	public final EntityLivingBase getOwner() {
		return worldObj.getPlayerEntityByName(getOwnerName());
	}

	@Override
	public final String getOwnerName() {
		return dataWatcher.getWatchableObjectString(17);
	}
}

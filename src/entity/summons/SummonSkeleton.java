package coolalias.arcanelegacy.entity.summons;

import coolalias.arcanelegacy.Config;
import coolalias.arcanelegacy.entity.ai.EntityAIFollowSummoner;
import coolalias.arcanelegacy.entity.ai.EntityAISummonSit;
import coolalias.arcanelegacy.entity.ai.EntityAISummonerHurtByTarget;
import coolalias.arcanelegacy.entity.ai.EntityAISummonerHurtTarget;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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

public class SummonSkeleton extends EntitySkeleton implements IRangedAttackMob, ISummonedCreature
{
	private final String name = "Skeleton";

	private int lifespan;

	private EntityPlayer owner;

	protected EntityAISummonSit aiSit = new EntityAISummonSit(this);

	public SummonSkeleton(World par1World) {
		this(par1World, null, Config.baseSummonDuration());
	}

	/**
	 * Recommended Constructor
	 */
	public SummonSkeleton(World world, EntityPlayer player, int lifespan)
	{
		super(world);
		this.setLifespan(lifespan);
		this.setCurrentItemOrArmor(0, new ItemStack(Item.bow));
		if (player != null) { this.setOwner(player.username); }
		this.owner = player;
		this.setTamed(true);

		// Set new AI tasks for Summoned Creature
		this.tasks.addTask(2, this.aiSit); // overrides AIRestrictSun
		if (this.owner != null) {
			this.tasks.addTask(4, new EntityAIFollowSummoner(this, this.owner, 1.0D, 10.0F, 2.0F));
		}
		// Reset default target AI
		this.targetTasks.taskEntries.clear();
		this.targetTasks.addTask(1, new EntityAISummonerHurtByTarget(this));
		this.targetTasks.addTask(2, new EntityAISummonerHurtTarget(this));
		this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
		this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityMob.class, 0, true));
	}

	/**
	 * INHERITED METHODS
	 */

	/**
	 * Override entityInit to add objects 16 and 17 to data watcher
	 */
	@Override
	protected final void entityInit()
	{
		super.entityInit();
		this.dataWatcher.addObject(16, Byte.valueOf((byte) 0));
		this.dataWatcher.addObject(17, "");
	}

	/**
	 * Override onLivingUpdate to decrement lifespan and despawn entity
	 */
	@Override
	public final void onLivingUpdate()
	{
		if (!this.worldObj.isRemote)
		{ 
			if (lifespan > 0) { --lifespan; }
			if (lifespan == 0) { this.worldObj.removeEntity(this); }
		}

		super.onLivingUpdate();
	}

	/**
	 * Override interact to implement sitting functionality
	 */
	@Override
	public boolean interact(EntityPlayer player)
	{
		if (player.getCommandSenderName().equalsIgnoreCase(this.getOwnerName()) && !this.worldObj.isRemote)
		{
			this.aiSit.setSitting(!this.isSitting());
			this.isJumping = false;
			this.setPathToEntity((PathEntity) null);
			this.setTarget((Entity) null);
			this.setAttackTarget((EntityLivingBase) null);
		}

		return super.interact(player);
	}

	/**
	 * Override for proper team checking
	 */
	@Override
	public Team getTeam()
	{
		if (this.isTamed())
		{
			EntityLivingBase entitylivingbase = this.getOwner();

			if (entitylivingbase != null) {
				return entitylivingbase.getTeam();
			}
		}

		return super.getTeam();
	}

	/**
	 * Override for proper team checking
	 */
	@Override
	public boolean isOnSameTeam(EntityLivingBase par1EntityLivingBase)
	{
		if (this.isTamed())
		{
			EntityLivingBase entitylivingbase1 = this.getOwner();

			if (par1EntityLivingBase == entitylivingbase1) {
				return true;
			}
			if (entitylivingbase1 != null) {
				return entitylivingbase1.isOnSameTeam(par1EntityLivingBase);
			}
		}

		return super.isOnSameTeam(par1EntityLivingBase);
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	@Override
	public final void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeEntityToNBT(par1NBTTagCompound);

		if (this.getOwnerName() == null) {
			par1NBTTagCompound.setString("Owner", "");
		} else {
			par1NBTTagCompound.setString("Owner", this.getOwnerName());
		}

		par1NBTTagCompound.setBoolean("Sitting", this.isSitting());
		par1NBTTagCompound.setInteger("Lifespan", this.lifespan);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	public final void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readEntityFromNBT(par1NBTTagCompound);
		String s = par1NBTTagCompound.getString("Owner");

		if (s.length() > 0)
		{
			this.setOwner(s);
			this.setTamed(true);
		}

		this.aiSit.setSitting(par1NBTTagCompound.getBoolean("Sitting"));
		this.setSitting(par1NBTTagCompound.getBoolean("Sitting"));
		this.setLifespan(par1NBTTagCompound.getInteger("Lifespan"));
	}

	/**
	 * Attack the specified entity using a ranged attack.
	 */
	@Override
	public final void attackEntityWithRangedAttack(EntityLivingBase par1EntityLivingBase, float par2)
	{
		EntityArrow entityarrow = new EntityArrow(this.worldObj, this, par1EntityLivingBase, 1.6F, (float)(14 - this.worldObj.difficultySetting * 4));
		int i = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, this.getHeldItem());
		int j = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, this.getHeldItem());
		// entityarrow.setDamage((double)(par2 * 2.0F) + this.rand.nextGaussian() * 0.25D + (double)((float)this.worldObj.difficultySetting * 0.11F));
		entityarrow.setDamage((double)(2.0F));	// takes 6 hits to kill a zombie at 2.0F

		if (i > 0) {
			entityarrow.setDamage(entityarrow.getDamage() + (double)i * 0.5D + 0.5D);
		}

		if (j > 0) {
			entityarrow.setKnockbackStrength(j);
		}

		if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, this.getHeldItem()) > 0 || this.getSkeletonType() == 1) {
			entityarrow.setFire(100);
		}

		this.playSound("random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
		this.worldObj.spawnEntityInWorld(entityarrow);
	}
	
	/**
     * Returns true if this entity can attack entities of the specified class.
     */
	@Override
    public boolean canAttackClass(Class par1Class) {
    	return true;
    }

	@Override
	@SideOnly(Side.CLIENT)
	public boolean getAlwaysRenderNameTagForRender() {
		return true;
	}

	/**
	 * Returns the name to display above the summoned creature
	 */
	@Override
	public String getTranslatedEntityName() {
		return ScorePlayerTeam.formatPlayerName(this.getTeam(), this.getOwnerName() + "'s " + this.name);
	}

	/** INTERFACE METHODS */

	/**
	 * Sets lifespan, in ticks, to par1, or Config.baseSummonDuration() if par1 is 0
	 */
	@Override
	public final void setLifespan(int par1) {
		this.lifespan = (par1 != 0 ? par1 : Config.baseSummonDuration());
	}

	/**
	 * Returns true if the summoned creature is tamed
	 */
	@Override
	public final boolean isTamed() {
		return (this.dataWatcher.getWatchableObjectByte(16) & 4) != 0;
	}

	/**
	 * Sets whether the summoned creature is tamed or not
	 */
	@Override
	public final void setTamed(boolean par1)
	{
		byte b0 = this.dataWatcher.getWatchableObjectByte(16);

		if (par1) {
			this.dataWatcher.updateObject(16, Byte.valueOf((byte)(b0 | 4)));
		} else {
			this.dataWatcher.updateObject(16, Byte.valueOf((byte)(b0 & -5)));
		}

		/*
        if (par1)
        {
        	// Sets health to 20
            this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(20.0D);
        }
		 */
	}

	/**
	 * Returns whether the summoned creature is 'sitting'
	 */
	@Override
	public boolean isSitting() {
		return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
	}

	/**
	 * Sets whether the summoned creature will 'sit' in place,
	 * meaning no longer follow the owner
	 */
	@Override
	public void setSitting(boolean par1)
	{
		byte b0 = this.dataWatcher.getWatchableObjectByte(16);

		if (par1)
		{
			this.dataWatcher.updateObject(16, Byte.valueOf((byte)(b0 | 1)));
		}
		else
		{
			this.dataWatcher.updateObject(16, Byte.valueOf((byte)(b0 & -2)));
		}
	}

	/**
	 * Sets owner's name and adds AI to follow owner
	 */
	public final void setOwner(String par1Str)
	{
		this.dataWatcher.updateObject(17, par1Str);
		if (par1Str.length() > 0)
			this.tasks.addTask(4, new EntityAIFollowSummoner(this, this.getOwner(), 1.0D, 10.0F, 2.0F));
	}

	/** INHERITED INTERFACE METHODS */

	/**
	 * Returns the player owner of this summoned Entity
	 */
	@Override
	public final EntityLivingBase getOwner() {
		return this.worldObj.getPlayerEntityByName(this.getOwnerName());
	}

	/**
	 * Returns owner's name or "" if not owned
	 */
	@Override
	public final String getOwnerName() {
		return this.dataWatcher.getWatchableObjectString(17);
	}
}

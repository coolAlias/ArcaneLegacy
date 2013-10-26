package coolalias.arcanelegacy.entity.summons;

import coolalias.arcanelegacy.Config;
import coolalias.arcanelegacy.entity.ai.EntityAIFollowSummoner;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityOwnable;
import net.minecraft.scoreboard.ScorePlayerTeam;

/**
 * Ideally, this would be an abstract class with all of the methods already fully
 * implemented to guarantee a certain functionality. Because Summons already extend
 * a base Entity, however, it must be an interface. Use the supplied suggested
 * implementations for best results.
 * 
 * 'Necessary Variables': Variables required for a Summoned Entity to function
 * 'Sample Constructor': A sample constructor showing usage of Summons AI and setting of owner
 * 'Inherited Methods': Inherited methods that must be overridden
 * 'Interface Methods': Methods required by the interface, with suggested implementations
 * 'Inherited Interface Methods': Methods from EntityOwnable, with suggested implementations
 */
public interface ISummonedCreature extends EntityOwnable
{
	/** NECESSARY VARIABLES */
	/*
	// Name for display purposes
	private final String name;

	// Lifespan remaining for this creature
	int lifespan;

	// Owner of this creature
	EntityPlayer owner;

	// AI that allows owner to tell creature to stay put
	// NOTE: Must be added to the entity's task list:
		// this.tasks.addTask(2, this.aiSit);
	EntityAISummonSit aiSit = new EntityAISummonSit(this);
	 */

	/** SAMPLE CONSTRUCTOR */
	/*
	public SummonSkeleton(World world, EntityPlayer player, int lifespan)
	{
		super(world);
		this.name = "Skeleton";
		this.setLifespan(lifespan);
		this.setCurrentItemOrArmor(0, new ItemStack(Item.bow));

		if (player != null) { this.setOwner(player.username); }
		this.owner = player;

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
		this.setTamed(true);
	}
	 */

	/** INHERITED METHODS */

	/**
	 * Override entityInit to add objects 16 and 17 to data watcher
	 */
	/*
	@Override
	protected final void entityInit()
	{
		super.entityInit();
		this.dataWatcher.addObject(16, Byte.valueOf((byte)0));
		this.dataWatcher.addObject(17, "");
	}
	 */

	/**
	 * Override onLivingUpdate to decrement lifespan and despawn entity
	 */
	/*
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
	 */

	/**
	 * Override interact to implement sitting functionality
	 */
	/*
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
	 */

	/**
	 * Override for proper team checking
	 */
	/*
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
	 */

	/**
	 * Override for proper team checking
	 */
	/*
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
	 */

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	/*
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
	 */

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	/*
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
	 */

	/**
	 * This will make it so the Summoned Creature always has a name tag rendered
	 */
	/*
	@Override
	@SideOnly(Side.CLIENT)
    public boolean getAlwaysRenderNameTagForRender()
    {
        return true;
    }
	 */

	/**
	 * Returns the name to display above the summoned creature
	 */
	/*
	@Override
    public String getTranslatedEntityName()
    {
        return ScorePlayerTeam.formatPlayerName(this.getTeam(), this.getOwnerName() + "'s " + this.name);
    }
	 */

	/** INTERFACE METHODS */

	/**
	 * Should set the amount of time before the summoned creature despawns, in ticks
	 * A negative value is interpreted as permanent, so the entity will not despawn
	 */
	public void setLifespan(int par1);
	/*
	{ this.lifespan = (par1 != 0 ? par1 : Config.baseSummonDuration()); }
	 */

	/**
	 * Returns true if the summoned creature is tamed
	 */
	public boolean isTamed();
	/*
	{ return (this.dataWatcher.getWatchableObjectByte(16) & 4) != 0; }
	 */

	/**
	 * Sets whether the summoned creature is tamed or not
	 */
	public void setTamed(boolean par1);
	/* Suggested implementation:
	{
        byte b0 = this.dataWatcher.getWatchableObjectByte(16);

        if (par1) {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte)(b0 | 4)));
        } else {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte)(b0 & -5)));
        }
	}
	 */

	/**
	 * Returns whether the summoned creature is 'sitting'
	 */
	public boolean isSitting();
	/*
	{ return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0; }
	 */

	/**
	 * Sets whether the summoned creature will 'sit' in place,
	 * meaning no longer follow the owner
	 */
	public void setSitting(boolean par1);
	/*
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
	 */

	/**
	 * Sets owner's name and adds AI to follow owner
	 */
	public void setOwner(String par1Str);
	/*
	{
		this.dataWatcher.updateObject(17, par1Str);
		if (par1Str.length() > 0)
			this.tasks.addTask(4, new EntityAIFollowSummoner(this, this.getOwner(), 1.0D, 10.0F, 2.0F));
	}
	 */

	/** INHERITED INTERFACE METHODS */

	/**
	 * Returns the player owner of this summoned Entity
	 * Changed to return EntityLivingBase instead of Entity for convenience
	 */
	@Override
	public EntityLivingBase getOwner();
	/*
	{ return this.worldObj.getPlayerEntityByName(this.getOwnerName()); }
	 */

	/**
	 * Returns owner's name or "" if not owned
	 */
	@Override
	public String getOwnerName();
	/*
	{ return this.dataWatcher.getWatchableObjectString(17); }
	 */
}

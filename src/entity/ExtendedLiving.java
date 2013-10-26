package coolalias.arcanelegacy.entity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import coolalias.arcanelegacy.ModInfo;
import coolalias.arcanelegacy.item.ItemScroll;
import coolalias.arcanelegacy.network.ALClientPacketHandler;
import coolalias.arcanelegacy.network.packet.PacketReduceFallDistance;
import coolalias.arcanelegacy.spells.SpellEffect;
import coolalias.arcanelegacy.spells.SpellsMap;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class ExtendedLiving implements IExtendedEntityProperties
{
	/** The name of these extended properties */
	public final static String EXT_PROP_NAME = "ALExtendedLiving";

	/** The entity to which these extended properties belong. */
	private final EntityLivingBase entity;

	/** Stores spell effects in the following format:
	 * List<effectType, effectID> returns matching SpellEffect */
	private Map<List<Integer>, SpellEffect> activeSpellEffects = new HashMap<List<Integer>, SpellEffect>();

	/** Whether the DataWatcher needs to update with the active spells */
	private boolean spellsNeedUpdate = false;

	/** Timer if the entity is in the process of resurrecting */
	private int resurrecting = 0;

	/** Entity's fall distance will be reduced by this amount upon impact */
	private float reduceFallDistance = 0.0F;

	public ExtendedLiving(EntityLivingBase entity) {
		this.entity = entity;
	}

	/**
	 * Used to register these extended properties for EntityLiving during EntityConstructing event
	 */
	public static void register(EntityLivingBase entity) {
		entity.registerExtendedProperties(EXT_PROP_NAME, new ExtendedLiving(entity));
	}

	/**
	 * Returns ExtendedLiving properties for EntityLiving
	 */
	public static ExtendedLiving get(EntityLivingBase entity) {
		return (ExtendedLiving) entity.getExtendedProperties(EXT_PROP_NAME);
	}

	/**
	 * Adds a SpellEffect to the entity.
	 */
	public final void addSpellEffect(SpellEffect par1SpellEffect)
	{
		if (isSpellActive(par1SpellEffect.getEffectType(), par1SpellEffect.getEffectID()))
		{
			getActiveSpell(par1SpellEffect.getEffectType(), par1SpellEffect.getEffectID()).combine(par1SpellEffect);
			this.onChangedSpellEffect(getActiveSpell(par1SpellEffect.getEffectType(), par1SpellEffect.getEffectID()), true);
		}
		else
		{
			this.activeSpellEffects.put(Arrays.asList(Integer.valueOf(par1SpellEffect.getEffectType().ordinal()), Integer.valueOf(par1SpellEffect.getEffectID())), par1SpellEffect);
			this.onNewSpellEffect(par1SpellEffect);
		}
		this.sync();
	}

	/**
	 * Clears all active spell effects from the entity.
	 */
	public final void clearActiveSpells()
	{
		Iterator iterator = this.activeSpellEffects.values().iterator();

		while (iterator.hasNext())
		{
			SpellEffect spelleffect = (SpellEffect)iterator.next();
			iterator.remove();
			this.onFinishedSpellEffect(spelleffect, false);
		}
	}

	/**
	 * Returns true if spell of type and ID is active
	 */
	public final boolean isSpellActive(ItemScroll.SpellType type, int id) {
		return this.activeSpellEffects.containsKey(Arrays.asList(Integer.valueOf(type.ordinal()), Integer.valueOf(id)));
	}

	/**
	 * Returns true if spell of type is active.
	 * Spell must not have any sub-types. Mostly for FLY, JUMP, etc.
	 */
	public final boolean isSpellActive(ItemScroll.SpellType type) {
		return this.activeSpellEffects.containsKey(Arrays.asList(Integer.valueOf(type.ordinal()), 0));
	}

	/**
	 * Returns SpellEffect of type and id 0 or null if no active spell of that type
	 * This method used only for spells with no special ID, such as FLY, JUMP, etc.
	 */
	public final SpellEffect getActiveSpell(ItemScroll.SpellType type) {
		return (SpellEffect) this.activeSpellEffects.get(Arrays.asList(Integer.valueOf(type.ordinal()), 0));
	}

	/**
	 * Returns SpellEffect of type and id or null if no active spell of that type
	 */
	public final SpellEffect getActiveSpell(ItemScroll.SpellType type, int id) {
		return (SpellEffect) this.activeSpellEffects.get(Arrays.asList(Integer.valueOf(type.ordinal()), Integer.valueOf(id)));
	}

	/**
	 * Remove the specified spell effect from this entity.
	 */
	public final void removeSpellEffect(ItemScroll.SpellType type, int id)
	{
		SpellEffect spelleffect = (SpellEffect) this.activeSpellEffects.remove(Arrays.asList(Integer.valueOf(type.ordinal()), Integer.valueOf(id)));

		if (spelleffect != null)
		{
			this.onFinishedSpellEffect(spelleffect, true);
		}
	}

	/**
	 * Updates all active spell effects, decrementing duration and performing effects.
	 */
	public final void updateSpellEffects()
	{
		Iterator iterator = this.activeSpellEffects.values().iterator();

		while (iterator.hasNext())
		{
			SpellEffect spelleffect = (SpellEffect) iterator.next();

			if (!spelleffect.onUpdate(this.entity))
			{
				if (!this.entity.worldObj.isRemote)
				{
					System.out.println("[EXT PROPS] Removing spell effect.");
					iterator.remove();
				}
				
				this.onFinishedSpellEffect(spelleffect, true);
			}
			else if (spelleffect.getDuration() % 10 == 0)
			{
				// this method just sets "this.spellsNeedUpdate" to true
				//this.onChangedSpellEffect(spelleffect, false);
				this.spawnSpellParticles(spelleffect, false);
			}
		}

		int i;

		if (this.spellsNeedUpdate)
		{
			if (!this.entity.worldObj.isRemote)
			{	
				this.sync();
				if (this.activeSpellEffects.isEmpty())
				{
					System.out.println("[EXT PROPS] Active Spell Effects is empty");
					//this.dataWatcher.updateObject(8, Byte.valueOf((byte)0));
					//this.dataWatcher.updateObject(7, Integer.valueOf(0));
					//this.setInvisible(false);
				}
				else
				{
					System.out.println("[EXT PROPS] Active Spell Effects is NOT empty");
					//i = PotionHelper.calcPotionLiquidColor(this.activeSpellEffects.values());
					//this.dataWatcher.updateObject(8, Byte.valueOf((byte)(PotionHelper.func_82817_b(this.activeSpellEffects.values()) ? 1 : 0)));
					//this.dataWatcher.updateObject(7, Integer.valueOf(i));
					//this.setInvisible(this.isSpellActive(Potion.invisibility.id));
				}
			}

			this.spellsNeedUpdate = false;
		}
	}

	/**
	 * Called when an already active spell effect is changed somehow
	 */
	protected final void onChangedSpellEffect(SpellEffect spellEffect, boolean par2)
	{
		this.spellsNeedUpdate = true;

		if (par2 && !this.entity.worldObj.isRemote)
		{
			if (spellEffect.getEffectType() == ItemScroll.SpellType.JUMP_SPELL)
			{
				this.reduceFallDistance = 4.0F * (spellEffect.getAmplifier() + 1);
			}
			//Potion.potionTypes[par1PotionEffect.getPotionID()].func_111187_a(this, this.func_110140_aT(), par1PotionEffect.getAmplifier());
			//Potion.potionTypes[par1PotionEffect.getPotionID()].func_111185_a(this, this.func_110140_aT(), par1PotionEffect.getAmplifier());
		}
	}

	/**
	 * Removes any lasting effects from SpellEffect; spawns particles if spawn is true.
	 */
	protected final void onFinishedSpellEffect(SpellEffect spellEffect, boolean spawn)
	{
		this.spellsNeedUpdate = true;

		if (!this.entity.worldObj.isRemote)
		{
			switch(spellEffect.getEffectType()) {
			case JUMP_SPELL:
				if (entity.fallDistance == 0)
					this.reduceFallDistance = 0;
				break;
			case FEATHER_FALL:
				//entity.stepHeight = 0.5F;
				break;
			default:
				break;
			}
			// Potion.potionTypes[par1PotionEffect.getPotionID()].func_111187_a(this, this.func_110140_aT(), par1PotionEffect.getAmplifier());
		}
		if (spawn) { this.spawnSpellParticles(spellEffect, true); }
	}

	/**
	 * Called when a new spell effect is added for the first time to an entity
	 */
	protected final void onNewSpellEffect(SpellEffect spellEffect)
	{
		this.spellsNeedUpdate = true;

		if (!this.entity.worldObj.isRemote)
		{
			switch(spellEffect.getEffectType()) {
			case JUMP_SPELL:
				this.reduceFallDistance = 4.0F * (spellEffect.getAmplifier() + 1);
				break;
			case FEATHER_FALL:
				//entity.stepHeight += 1.0F;
				break;
			default:
				break;
			}
			// Potion.potionTypes[par1PotionEffect.getPotionID()].func_111185_a(this, this.func_110140_aT(), par1PotionEffect.getAmplifier());
		}
	}

	/**
	 * Spawns particles for the spell when spell is / is not finished
	 */
	protected final void spawnSpellParticles(SpellEffect par1SpellEffect, boolean finished)
	{
		double radius = ((ItemScroll) SpellsMap.instance().getSpell(par1SpellEffect.getScrollID())).getAreaOfEffect();
		String particle = "";

		switch (par1SpellEffect.getEffectType()) {
		case FIRE_SPELL:
			particle = "flame";
			break;
		case ICE_SPELL:
			particle = "magicCrit";
			break;
		case RESURRECT:
			particle = "heart";
			break;
		default:
			break;
		}
		//if (particle.length() > 0) System.out.println("[EXT PROPS] Particle to spawn: " + particle);

		if (particle.length() > 0)
		{
			Random rand = new Random();

			double posX = entity.posX;
			double posY = entity.posY + 0.5D;
			double posZ = entity.posZ;

			if (radius > 0.5) {
				for (int x = (int)(posX - radius); x < (int)(posX + radius + 1.5D); ++x)
				{
					for (int y = (int)(posY - (radius / 2)); y < (int)(posY + (radius / 2) + 1.25D); ++y)
					{
						for (int z = (int)(posZ - radius); z < (int)(posZ + radius + 1.5D); ++z)
						{
							if (entity.worldObj.isAirBlock(x, y, z))
								entity.worldObj.spawnParticle(particle, (double) x, (double) y, (double) z, entity.motionX, entity.motionY, entity.motionZ);
						}
					}
				}
			}
			else
			{
				posX -= (double)(MathHelper.cos(entity.rotationYaw / 180.0F * (float)Math.PI) * 0.16D);
				posY += 0.2F;
				posZ -= (double)(MathHelper.sin(entity.rotationYaw / 180.0F * (float)Math.PI) * 0.16D);

				for (int i = 0; i < 4; ++i)
				{
					/*
					float factor = 0.05F;
					double motionX = entity.motionX + rand.nextGaussian() * factor;
					double motionY = entity.motionY + rand.nextGaussian() * factor + 0.2F;
					double motionZ = entity.motionZ + rand.nextGaussian() * factor;
					 */
					float f = 0.4F;
					double motionX = (double)(-MathHelper.sin(entity.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(entity.rotationPitch / 180.0F * (float)Math.PI) * f);
					double motionZ = (double)(MathHelper.cos(entity.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(entity.rotationPitch / 180.0F * (float)Math.PI) * f);
					double motionY = (double)(-MathHelper.sin((entity.rotationPitch + 2.0F) / 180.0F * (float)Math.PI) * f);

					// entity.worldObj.spawnParticle(particle, entity.posX, entity.posY, entity.posZ, 0.0D, 0.2D, 0.0D);
					//entity.worldObj.spawnParticle(particle, posX + entity.motionX * (double) i / 4.0D, posY + entity.motionY * (double) i / 4.0D, posZ + entity.motionZ * (double) i / 4.0D, -entity.motionX, -entity.motionY + 0.2D, -entity.motionZ);
					entity.worldObj.spawnParticle(particle, posX + motionX * (double) i / 4.0D, posY + motionY * (double) i / 4.0D, posZ + motionZ * (double) i / 4.0D, motionX, motionY, motionZ);
				}
			}
		}
	}
	/* From ENTITY VILLAGER
    @SideOnly(Side.CLIENT)
    private void generateRandomParticles(String particle)
    {
        for (int i = 0; i < 5; ++i)
        {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.worldObj.spawnParticle(par1Str, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 1.0D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
        }
    }
	 */

	/**
	 * Returns distance by which to reduce fall
	 */
	public final float getReduceFallDistance() {
		return this.reduceFallDistance;
	}

	/**
	 * Add amount to reduce fall by to current amount
	 */
	public final void addReduceFallDistance(float distance)
	{
		this.reduceFallDistance += distance;
		if (this.entity instanceof EntityPlayer && !this.entity.worldObj.isRemote)
			PacketDispatcher.sendPacketToPlayer(PacketReduceFallDistance.getPacket(this.reduceFallDistance), (Player) this.entity);
		else
			System.out.println("[EXT PROPS] Not sending reduce fall packet");
	}

	/**
	 * Set amount to reduce fall by to amount given
	 */
	public final void setReduceFallDistance(float distance)
	{
		this.reduceFallDistance = distance;
		if (this.entity instanceof EntityPlayer && !this.entity.worldObj.isRemote)
			PacketDispatcher.sendPacketToPlayer(PacketReduceFallDistance.getPacket(this.reduceFallDistance), (Player) this.entity);
		else
			System.out.println("[EXT PROPS] Not sending reduce fall packet");
	}

	/**
	 * Returns time remaining to resurrect
	 */
	public final int getResurrectingTime() {
		return this.resurrecting;
	}

	/**
	 * Sets time until resurrection; automatically sets hurtResistantTime and syncs client
	 */
	public final void setResurrecting(int time)
	{
		this.resurrecting = time;
		this.entity.hurtResistantTime = time + 20;
		// System.out.println("[EXT LIVING] Set resurrect time: " + this.resurrecting + ", hurt resistant time: " + this.entity.hurtResistantTime);
		syncResurrecting();
	}

	/**
	 * Handles updating of resurrection timer; returns true when it reaches 0
	 */
	public final boolean updateResurrecting()
	{
		if (this.resurrecting > 0)
			--this.resurrecting;
		if (this.resurrecting == 0)
			syncResurrecting();

		return this.resurrecting == 0;
	}

	/**
	 * Called when the entity that this class is attached to is saved.
	 * Any custom entity data  that needs saving should be saved here.
	 * @param compound The compound to save to.
	 */
	@Override
	public final void saveNBTData(NBTTagCompound compound)
	{
		NBTTagCompound properties = new NBTTagCompound();

		if (!this.activeSpellEffects.isEmpty())
		{
			NBTTagList nbttaglist = new NBTTagList();

			Iterator iterator = this.activeSpellEffects.values().iterator();
			while (iterator.hasNext()) {
				SpellEffect spelleffect = (SpellEffect)iterator.next();
				nbttaglist.appendTag(spelleffect.writeCustomSpellEffectToNBT(new NBTTagCompound()));
			}

			properties.setTag("ALSpellEffects", nbttaglist);
			compound.setTag(EXT_PROP_NAME, properties);
		}
	}

	/**
	 * Called when the entity that this class is attached to is loaded.
	 * In order to hook into this, you will need to subscribe to the EntityConstructing event.
	 * Otherwise, you will need to initialize manually.
	 * @param compound The compound to load from.
	 */
	@Override
	public final void loadNBTData(NBTTagCompound compound)
	{
		NBTTagCompound properties = (NBTTagCompound) compound.getTag(EXT_PROP_NAME);

		if (properties != null && properties.hasKey("ALSpellEffects"))
		{
			NBTTagList nbttaglist = properties.getTagList("ALSpellEffects");

			for (int i = 0; i < nbttaglist.tagCount(); ++i)
			{
				NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.tagAt(i);
				SpellEffect spelleffect = SpellEffect.readCustomSpellEffectFromNBT(nbttagcompound1);
				this.activeSpellEffects.put(Arrays.asList(Integer.valueOf(spelleffect.getEffectType().ordinal()), Integer.valueOf(spelleffect.getEffectID())), spelleffect);
				//this.addSpellEffect(spelleffect);
			}
		}
	}

	/**
	 * Used to initialize the extended properties with the entity that this is attached to, as well
	 * as the world object.
	 * Called automatically if you register with the EntityConstructing event.
	 * @param entity  The entity that this extended properties is attached to
	 * @param world  The world in which the entity exists
	 */
	@Override
	public final void init(Entity entity, World world) {}

	public final void sync()
	{
		if (this.entity instanceof EntityPlayer && !this.entity.worldObj.isRemote)
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream outputStream = new DataOutputStream(bos);
			
			System.out.println("[EXT PROPS] active spell effects size: " + this.activeSpellEffects.size());
			System.out.println("[EXT PROPS] Server side entity step height: " + entity.stepHeight);
			
			try {
				outputStream.writeByte(ALClientPacketHandler.PACKET_ACTIVE_SPELL);
				outputStream.writeInt(this.activeSpellEffects.size());
				Iterator iterator = this.activeSpellEffects.values().iterator();

				while (iterator.hasNext())
				{
					SpellEffect spelleffect = (SpellEffect) iterator.next();
					spelleffect.writeToStream(outputStream);
				}

				outputStream.writeFloat(this.reduceFallDistance);
				// Hard-coded workaround for problem of onEntityJoinWorld server side initially has stepHeight = 0;
				// If ever add spells to reduce stepHeight, need to check if active here
				outputStream.writeFloat(entity.stepHeight < 0.5F ? 0.5F : entity.stepHeight);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			PacketDispatcher.sendPacketToPlayer(PacketDispatcher.getPacket(ModInfo.CHANNEL_EXT_PROPS, bos.toByteArray()), (Player) this.entity);
		}
	}

	private final void syncResurrecting()
	{
		if (this.entity instanceof EntityPlayer && !this.entity.worldObj.isRemote)
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream outputStream = new DataOutputStream(bos);
			
			try {
				outputStream.writeByte(ALClientPacketHandler.PACKET_RES_TIMER);
				outputStream.writeInt(this.resurrecting);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			PacketDispatcher.sendPacketToPlayer(PacketDispatcher.getPacket(ModInfo.CHANNEL_EXT_PROPS, bos.toByteArray()), (Player) this.entity);
		}
	}
}

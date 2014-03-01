package arcanelegacy.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import arcanelegacy.Config;
import arcanelegacy.entity.ExtendedPlayer;
import arcanelegacy.spells.SpellUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemScroll extends ItemScrollBase
{
	/** Define effect type constants.
	 * GROWTH spells will apply bonemeal effect a number of times equal to spell's duration
	 * ICE_SPELL's first effect defines the block to create, additional effects add POTION effects.
	 * TELEPORT only works for entity that casts the spell. Uses amplifier[0] for max distance. */
	public static enum SpellType {
		GENERIC, BANISH, CHARM, CREATE_BLOCK, DISPEL, EGRESS, FEATHER_FALL, FIRE_SPELL,
		FLY_SPELL, GROWTH, ICE_SPELL, JUMP_SPELL, POTION, PROTECTION, RESURRECT,
		SPIDER_CLIMB, SUMMON, TELEPORT, WILT
	}

	// can't enum EffectID because it's also used for Potions...

	/** Maximum number of effects a spell can have; must be of the same effect type
	 	Maximum value to which knockback can be set. */
	public final static int MAX_EFFECTS = 3, MAX_KNOCKBACK = 5;

	/** GENERIC effectIDs */
	/* Just as simple to make these all unique spell types, above */
	//public static final int FEATHER_FALL = 1, FLY_SPELL = 2, JUMP_SPELL = 3, RESURRECT = 4, SPIDER_CLIMB = 5;

	/** PROTECTION effectIDs */
	public static final int PROTECT_ALL = 0, PROTECT_ARROW = 1, PROTECT_FIRE = 2, PROTECT_MAGIC = 3;

	/** SUMMON effectIDs */
	public final static int NONE = 0, PIG = 1, SKELETON = 2;

	/** TELEPORT effectIDs */
	public final static int BLINK = 1, PHASE = 2, NULLIFY = 3;

	/** Define types for Range. GENERIC, SELF, TOUCH and MISSILE spells with an AoE default to spherical shape.
	 * GENERIC will attempt TOUCH first, then default to SELF if no target was acquired.
	 * SELF centers on player (caster), but AoE may affect other targets.
	 * TOUCH will never center on caster, but may target if within AoE.
	 * Shapes BEAM and CONE start from caster or tile struck and affect area in direction caster is facing. */
	public static enum Range { GENERIC, SELF, TOUCH, MISSILE, BEAM, CONE }

	/** Status Icon Index adjustments */
	private static final int NUM_AURA_SPELLS = 9, NUM_IMPRISON_SPELLS = 3, NUM_TICKING_BLOCK_SPELLS = 2;

	/** Type of effect; e.g. POTION mimics vanilla Potion effects */
	private SpellType effectType;

	/** Stores ID of effect to produce: Potion.id, ID of entity to summon, etc. */
	private int effectID[] = new int[MAX_EFFECTS];

	/** Duration of each effect, usually in seconds. */
	private int duration[] = new int[MAX_EFFECTS];

	/** Amplifier of effect. For POTION type spells, 0 = level 1, 1 = level 2 potion effect. */
	private int amplifier[] = new int[MAX_EFFECTS];

	/** Chance a particular effect will affect target. May be scaled based on distance from center. */
	private float chance[] = new float[MAX_EFFECTS];

	/** Number of effects this scroll has. */
	private int numEffects = 1;

	/** Number of ticks required to use scroll. */
	private int castTime;

	/** The range and / or AoE shape of this spell. See types defined above. */
	private Range range;

	/** Damage inflicted to targets; may be scaled based on distance from point of impact. */
	private double scrollDamage;

	/** If true, damage is scaled based on distance from point of impact */
	private boolean isDmgScaled;

	/** Amount to knockback target affected by scroll's spell */
	private int knockback;

	/** Bounding box this scroll will check for targets; OR Max # targets? */
	private double area;

	/** If true, probability is scaled based on distance from point of impact */
	private boolean isChanceScaled;

	/** If true, active spell effect will perform spell effect every few ticks */
	private boolean isAura;

	/** Index of spell's icon for display in HUD when in active wand slot */
	private int statusIconIndex;

	/**
	 * Basic spell scroll constructor. Default parameter values:
	 * Duration = 1 tick, Cast time = 10, Chance = 1.0F, all else 0
	 */
	public ItemScroll(int par1) {
		this(par1, SpellType.GENERIC, 0, 1, 0, 1.0F, Range.GENERIC, 10, 0.0D, 0.0D, false);
	}

	/**
	 * Creates a new spell scroll of specified Type and CastTime. Duration is 1 tick, chance 1.0F, all else 0.
	 */
	public ItemScroll(int par1, SpellType par2EffectType, int par8CastTime) {
		this(par1, par2EffectType, 0, 1, 0, 1.0F, Range.GENERIC, par8CastTime, 0.0D, 0.0D, false);
	}

	/**
	 * Create a scroll with specified effect and no amplifier.
	 * @param par1: Item ID
	 * @param par2EffectType: Spell type, e.g. SUMMON
	 * @param par3EffectID: Effect ID, such as creature to summon, e.g. SKELETON
	 * @param par4Duration: Duration, in seconds if par11 is true
	 * @param par8CastTime: Time required to cast spell
	 * @param par11isInSeconds: sets duration in seconds if true, in ticks if false
	 */
	public ItemScroll(int par1, SpellType par2EffectType, int par3EffectID, int par4Duration, int par8CastTime, boolean par11isInSeconds) {
		this(par1, par2EffectType, par3EffectID, par4Duration, 0, 1.0F, Range.GENERIC, par8CastTime, 0.0D, 0.0D, par11isInSeconds);
	}

	/**
	 * Full spell scroll constructor.
	 * @param par1: Item ID
	 * @param par2EffectType: e.g. POTION, SUMMON, etc.
	 * @param par3EffectID: id of primary effect, e.g. Potion.harm.id, SKELETON, etc.
	 * @param par4Duration: duration of primary effect, in seconds or ticks
	 * @param par5Amplifier: amplifier of primary effect, used for potion effects only
	 * @param par6Chance: probability of primary effect occurring
	 * @param par7Range: Defines range and shape of spell, e.g. SELF, MISSILE, CONE, etc.
	 * @param par8CastTime: time in ticks to cast spell
	 * @param par9Damage: damage inflicted, if any
	 * @param par10Area: amount to expand collision box upon impact, default 1.0F
	 * @param par11isInSeconds: true if the duration is in seconds, false is in ticks
	 */
	public ItemScroll(int par1, SpellType par2EffectType, int par3EffectID, int par4Duration, int par5Amplifier,
			float par6Chance, Range par7Range, int par8CastTime, double par9Damage, double par10Area, boolean par11isInSeconds)
	{
		super(par1);
		this.setStatusIconIndex();
		// System.out.println("[SCROLL] status index: " + this.statusIconIndex);
		// this.setHasSubtypes(true);	// Damage variable used as effect type
		this.effectType = par2EffectType;
		this.setScrollEffect(par3EffectID, par4Duration, par5Amplifier, par6Chance, par11isInSeconds);
		this.range = par7Range;
		this.castTime = par8CastTime;
		this.scrollDamage = par9Damage;
		this.isDmgScaled = true;
		this.knockback = 0;
		this.isChanceScaled = true;
		this.area = par10Area;
		this.isAura = false;
	}

	/** Returns the scroll's Type - used to determine how to handle spell when cast. */
	public final SpellType getEffectType() { return effectType; }

	/** Returns effect ID of primary effect */
	public final int getEffectID() { return effectID[0]; }

	/** Returns effect ID of effect[index] */
	public final int getEffectID(int index) { return effectID[index]; }

	/** Returns total number of effects for this scroll */
	public final int getNumEffects() { return numEffects; }

	/** Returns duration of the primary effect */
	public final int getDuration() { return duration[0]; }

	/** Sets first scroll effect duration, in seconds if inSeconds is true */
	public final ItemScroll setDuration(int duration, boolean inSeconds) {
		setDuration(0, duration, inSeconds);
		return this;
	}

	/** Returns duration of effect[index] */
	public final int getDuration(int index) { return duration[index]; }

	/** Sets scroll effect duration in index, in seconds if inSeconds is true unless duration parameter equals -1 */
	private final void setDuration(int index, int duration, boolean inSeconds) {
		this.duration[index] = (inSeconds && duration != -1 ? duration * 20 : duration);
	}

	/** Returns amplifier of primary effect */
	public final int getAmplifier() { return amplifier[0]; }

	/** Returns amplifier of effect[index] */
	public final int getAmplifier(int index) { return amplifier[index]; }

	/** Sets amplifier for primary effect */
	public final ItemScroll setAmplifier(int par1) {
		amplifier[0] = par1;
		return this;
	}

	/**
	 * Returns probability that effect[index] will affect target.
	 * Probability may be scaled based on distance from point of impact,
	 * so values greater than 1.0 are allowed.
	 */
	public final float getChance(int index) { return chance[index]; }

	/** Set scroll chance[index] to value. */
	public final ItemScroll setChance(int index, float value) {
		chance[index] = value;
		return this;
	}

	/** Returns number of ticks required to cast the spell from a scroll */
	public final int getCastTime() { return castTime; }

	/** Sets time required to cast spell, in ticks */
	public final ItemScroll setCastTime(int castTime) {
		this.castTime = castTime;
		return this;
	}

	/** Returns range definition of spell: GENERIC, SELF, TOUCH, MISSILE, BEAM, CONE. */
	public final Range getRange() { return range; }

	/** Sets scroll's range to value: GENERIC, SELF, TOUCH, MISSILE, BEAM, CONE. */
	public final ItemScroll setRange(Range range) {
		this.range = range;
		return this;
	}

	/** Returns damage inflicted by this spell; may be scaled with distance. */
	public final double getScrollDamage() { return scrollDamage; }

	/** Sets damage inflicted by this scroll. Damage will be scaled with distance. */
	public final ItemScroll setDamage(double damage) {
		this.scrollDamage = damage;
		this.isDmgScaled = true;
		return this;
	}

	/** Sets damage inflicted by this scroll and whether it is scaled with distance or not. */
	public final ItemScroll setDamage(double damage, boolean isScaled) {
		this.scrollDamage = damage;
		this.isDmgScaled = isScaled;
		return this;
	}

	/** Returns true if damage should be scaled based on distance from point of impact. */
	public final boolean isDamageScaled() { return this.isDmgScaled; }

	/** Sets whether damage should be scaled based on distance from point of impact. */
	public final ItemScroll setDamageScaled(boolean isScaled) {
		this.isDmgScaled = isScaled;
		return this;
	}

	/** Returns multiplier for distance to knock targets back. */
	public final int getKnockback() { return this.knockback; }

	/** Sets value of knockback variable for this scroll. Hard-capped at MAX_KNOCKBACK. */
	public final ItemScroll setKnockback(int value) {
		this.knockback = (value > MAX_KNOCKBACK ? MAX_KNOCKBACK : value);
		return this;
	}

	/** Returns true if probability should be scaled based on distance from point of impact. */
	public final boolean isChanceScaled() { return this.isChanceScaled; }

	/** Sets whether probability should be scaled based on distance from point of impact. */
	public final ItemScroll setChanceScaled(boolean isScaled) {
		this.isChanceScaled = isScaled;
		return this;
	}

	/** Returns the radius of the bounding box in which this scroll will check for targets. */
	public final double getAreaOfEffect() { return area; }

	/**
	 * Set the radius of the bounding box in which to check for targets.
	 * Must be greater than 0 to affect blocks.
	 */
	public final ItemScroll setEffectRadius(double radius) {
		this.area = radius;
		return this;
	}

	/** Returns true if the spell creates an aura. */
	public final boolean isAura() { return isAura; }

	/** Sets whether spell is an aura effect. */
	public final ItemScroll setIsAura(boolean aura) {
		this.isAura = aura;
		return this;
	}

	/**
	 * This method sets the first effect of a spell and sets numEffects to 1
	 * @param effectID = e.g. ID of potion.effect to replicate
	 * @param duration = duration, in seconds if inSeconds is true
	 * @param amplifier = amplifier of effect; for potions: 0 is level 1, 1 is level 2
	 * @param chance = chance effect will be applied
	 * @param inSeconds = sets duration in seconds if true, in ticks if false
	 */
	public final ItemScroll setScrollEffect(int effectID, int duration, int amplifier, float chance, boolean inSeconds)
	{
		this.effectID[0] = effectID;
		this.setDuration(0, duration, inSeconds);
		this.amplifier[0] = amplifier;
		this.chance[0] = chance;
		this.numEffects = 1;
		return this;
	}

	/**
	 * Adds an additional effect that is identical to the primary effect in all but effectID.
	 * Added to next available slot in array index. Useful for multi-Potion effect spells.
	 */
	public final ItemScroll addScrollEffect(int effectID)
	{
		if (this.numEffects < MAX_EFFECTS) {
			return this.addScrollEffect(effectID, this.duration[0], this.amplifier[0], this.chance[0], false);
		} else {
			System.out.println("[WARNING] Too many scroll effects added!");
			return this;
		}
	}

	/**
	 * Adds an additional effect to a scroll in the next available array index
	 */
	public final ItemScroll addScrollEffect(int effectID, int duration, int amplifier, float chance, boolean inSeconds)
	{
		if (this.numEffects < MAX_EFFECTS) {
			this.effectID[this.numEffects] = effectID;
			this.setDuration(this.numEffects, duration, inSeconds);
			this.amplifier[this.numEffects] = amplifier;
			this.chance[this.numEffects] = chance;
			++this.numEffects;
		} else {
			System.out.println("[WARNING] Too many scroll effects added!");
		}

		return this;
	}

	/** Returns true if the scroll has a status icon to display when in a wand's active slot. */
	@SideOnly(Side.CLIENT)
	public final boolean hasStatusIcon() { return statusIconIndex >= 0; }

	/** Returns the index for the icon to display when the scroll is active. */
	@SideOnly(Side.CLIENT)
	public final int getStatusIconIndex() { return statusIconIndex; }

	/** Used to set the scrolls status icon index for display when active */
	public final ItemScroll setStatusIconIndex(int index) {
		this.statusIconIndex = index;
		return this;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack scroll, World world, EntityPlayer player) {
		ArrowNockEvent event = new ArrowNockEvent(player, scroll);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) { return event.result; }
		player.setItemInUse(scroll, this.getMaxItemUseDuration(scroll));
		return scroll;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack scroll, World world, EntityPlayer player, int par4)
	{
		int ticksInUse = this.getMaxItemUseDuration(scroll) - par4;
		ArrowLooseEvent event = new ArrowLooseEvent(player, scroll, ticksInUse);
		MinecraftForge.EVENT_BUS.post(event);

		if (event.isCanceled()) {
			System.out.println("[SCROLL] Spell must have been interrupted. Damn.");
			return;
		}

		ticksInUse = event.charge;

		if (ticksInUse < ((ItemScroll)scroll.getItem()).getCastTime())
		{
			System.out.println("[SCROLL] Your spell fizzles...");
			return;
		}
		else
		{
			if (world.rand.nextFloat() < SpellUtils.getSuccessChance(player))
				//if (!player.worldObj.isRemote)
			{
				System.out.println("[SCROLL] Spell cast successfully!");
				if (player.capabilities.isCreativeMode || player.inventory.hasItem(scroll.itemID));
				{
					if (((ItemScroll) scroll.getItem()).getRange() == Range.MISSILE) {
						SpellUtils.defaultRangedSpell(scroll, world, player);
					} else {
						SpellUtils.defaultSpell(scroll, world, player, null, false);
					}
				}
			} else {
				// play spell failure sound
				System.out.println("[SCROLL] Armor interfered - spell failed!");
			}
			// Remove scroll even if spell failed from armor interference
			if (!player.capabilities.isCreativeMode) {
				player.inventory.consumeInventoryItem(scroll.itemID);
			}
		}
	}

	@Override
	public void onUsingItemTick(ItemStack stack, EntityPlayer player, int count)
	{
		//if (!player.worldObj.isRemote) { return; }

		ExtendedPlayer props = ExtendedPlayer.get(player);

		if (props.isCasting && !props.wasInterrupted)
		{
			int ticksInUse = getMaxItemUseDuration(stack) - count;

			if (ticksInUse % 4 == 0)
			{
				double posX = player.posX - (double)(MathHelper.cos(player.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
				double posY = player.posY + (double) player.getEyeHeight() - 0.10000000149011612D;
				double posZ = player.posZ - (double)(MathHelper.sin(player.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
				float f = 0.4F;
				double motionX = (double)(-MathHelper.sin(player.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float)Math.PI) * f);
				double motionZ = (double)(MathHelper.cos(player.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float)Math.PI) * f);
				double motionY = (double)(-MathHelper.sin((player.rotationPitch + 2.0F) / 180.0F * (float)Math.PI) * f);

				if (ticksInUse < ((ItemScroll) stack.getItem()).getCastTime()) {
					player.worldObj.spawnParticle("smoke", posX + motionX, posY + motionY, posZ + motionZ, player.motionX, player.motionY + 0.1D, player.motionZ);
				}
				else {
					player.worldObj.spawnParticle("flame", posX + motionX, posY + motionY, posZ + motionZ, player.motionX, player.motionY + 0.1D, player.motionZ);
				}
			}
		}
	}

	@Override
	public MovingObjectPosition getMovingObjectPositionFromPlayer(World world, EntityPlayer player, boolean par3) {
		return super.getMovingObjectPositionFromPlayer(world, player, par3);
	}

	/** Sets status icon index accounting for potential missing items from Config settings */
	private final void setStatusIconIndex()
	{
		this.statusIconIndex = this.itemID - Config.scrollStartIndex();
		if (!Config.enableAuraSpells())
			this.statusIconIndex += NUM_AURA_SPELLS;
		else if (this.statusIconIndex >= NUM_AURA_SPELLS - 1) this.statusIconIndex += 1; // one unused aura slot
		if (!Config.enableImprisonSpells() && this.statusIconIndex > NUM_AURA_SPELLS)
			this.statusIconIndex += NUM_IMPRISON_SPELLS;
		if (!Config.enableTickingBlocks() && this.statusIconIndex > NUM_AURA_SPELLS + NUM_IMPRISON_SPELLS)
			this.statusIconIndex += NUM_TICKING_BLOCK_SPELLS;
		// System.out.println("[SCROLL] Item ID: " + this.itemID + ", Status Icon Index: " + this.statusIconIndex);
	}
}
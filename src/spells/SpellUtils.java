package coolalias.arcanelegacy.spells;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockStem;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import coolalias.arcanelegacy.Config;
import coolalias.arcanelegacy.blocks.ALBlocks;
import coolalias.arcanelegacy.blocks.BlockMagicDarkness;
import coolalias.arcanelegacy.blocks.BlockMagicLight;
import coolalias.arcanelegacy.entity.ExtendedLiving;
import coolalias.arcanelegacy.entity.projectile.EntitySpell;
import coolalias.arcanelegacy.entity.summons.ISummonedCreature;
import coolalias.arcanelegacy.entity.summons.SummonPig;
import coolalias.arcanelegacy.entity.summons.SummonSkeleton;
import coolalias.arcanelegacy.item.ALItems;
import coolalias.arcanelegacy.item.ItemScroll;
import coolalias.arcanelegacy.item.ItemScroll.Range;
import coolalias.arcanelegacy.item.ItemScroll.SpellType;

/**
 * Class containing helper methods for processing spell effects
 */
public class SpellUtils
{
	/**
	 * Checks if Scroll's spell effect is ready to be applied this tick.
	 * Currently only for aura effects.
	 * @param: dur is duration remaining of original aura spell
	 */
	public static final boolean isReady(ItemScroll scroll, int dur)
	{
		if (!scroll.isAura()) { return false; }

		int k = scroll.getDuration() - dur;
		boolean isready = false;
		// System.out.println("[SCROLL] isReady debug; k = " + k);

		if (scroll.itemID == ALItems.scrollHealAuraI.itemID || scroll.itemID == ALItems.scrollHarmAuraI.itemID) {
			isready = k % 50 == 0;	// every x ticks
		} else if (scroll.itemID == ALItems.scrollFireAura.itemID || scroll.itemID == ALItems.scrollFreezeAuraI.itemID) {
			isready = k % 15 == 0;
		} else {
			isready = k % 10 == 0;
		}

		return isready;
	}
	
	/**
	 * Performs effect of ticking spell centered on player (mostly for auras)
	 */
	public static final void performEffect(ItemScroll scroll, EntityLivingBase player)
	{
		System.out.println("[SCROLL] Performing ticking effect.");
		defaultSpell(new ItemStack(scroll), player.worldObj, player, null, true);
	}
	
	/**
	 * Returns the chance of successfully casting a spell, modified by any armor worn by parameter 'entity'
	 */
	public static final float getSuccessChance(EntityLivingBase entity)
	{
		float chance = 1.0F;
		// Exclude held item and check all pieces of armor:
		for (int i = 1; i < 5; ++i)
		{
			if (entity.getCurrentItemOrArmor(i) != null)
			{
				// NEW METHOD: Compare armor material's enchantability to Wizard Armor then subtract accordingly:
				// Wizard Armor = 100%, Gold = 80%, Leather = 40%, Diamond = 20% and Iron = 16%
				chance -= 0.01F * (float)(30 - ((ItemArmor) entity.getCurrentItemOrArmor(i).getItem()).getArmorMaterial().getEnchantability());
				// adjust chance of leather to 90%
				chance += ((ItemArmor) entity.getCurrentItemOrArmor(i).getItem()).getArmorMaterial() == EnumArmorMaterial.CLOTH ? 0.125 : 0;

				// new Enchantment "Mastery" gives bonus to success chance

				// Each point of armor reduces chance by 4%, so full diamond armor gives -80% to chance
				//chance -= (float)(0.04F * ((ItemArmor) entity.getCurrentItemOrArmor(i).getItem()).getArmorMaterial().getDamageReductionAmount(i - 1));
				System.out.println("[SCROLL] Reduced casting chance from armor: " + chance);
			}
		}
		
		return chance;
	}
	
	/**
	 * Handles target acquisition for spells. Target is acquired in this order:
	 * 1: From valid MovingObjectPosition
	 * 2: From player - if player's MOB hit a tile, then target will remain null;
	 * 3: If target is still null and the scroll's AoE is 0.0F, player is the target
	 * @param movingobjectposition - Must be 'null' unless passed from a ranged EntitySpell
	 * @param isTickingEvent - If true, will only apply a 1-second version of the spell's effect. Used mainly for aura's.
	 */
	public static final void defaultSpell(ItemStack scrollStack, World world, EntityLivingBase player, MovingObjectPosition movingobjectposition, boolean isTickingEvent)
	{
		EntityLivingBase target = null;
		ItemScroll scroll = ((ItemScroll) scrollStack.getItem());

		// movingobjectposition was passed from ranged EntitySpell, may have acquired target
		if (movingobjectposition != null) {
			target = (movingobjectposition.entityHit instanceof EntityLivingBase ? (EntityLivingBase) movingobjectposition.entityHit : null);
		} else // not from EntitySpell, spell is centered on player or tile hit
		{
			// All ranges except SELF try to acquire target / tile from MOB:
			movingobjectposition = scroll.getRange() != Range.SELF ? scroll.getMovingObjectPositionFromPlayer(world, (EntityPlayer) player, true) : null;

			// Player hit a tile or entity; check for valid target from MOB or set target to null (i.e. center effect on TILE)
			if (movingobjectposition != null) {
				target = (movingobjectposition.entityHit != null && movingobjectposition.typeOfHit != EnumMovingObjectType.TILE ? (EntityLivingBase) movingobjectposition.entityHit : null);
				System.out.println("[SCROLL] Valid MOB from player. Target " + (target != null ? "is valid." : "is null."));
				// if (scroll.getAreaOfEffect() == 0.0F && scroll.effectType != CREATE_BLOCK) {
				/*
				if (scroll.getRange() != TOUCH) {
					// System.out.println("[SCROLL] No AoE. Set MOB to null.");
					System.out.println("[SCROLL] Not a TOUCH spell. Set MOB to null.");
					movingobjectposition = null;
				}
				 */
			} else if (scroll.getRange() == Range.TOUCH) {
				System.out.println("[SCROLL] No valid target for spell's range. Range is: " + scroll.getRange());
				return;
			} else {	// GENERIC, SELF, shaped AoE
				target = player;
			}
		}
		// Aura effects always center on player unless a projectile
		if (scroll.isAura() && !isTickingEvent)
		{
			target = scroll.getRange() == Range.MISSILE ? target : player;
			if (target != null) {
				System.out.println("[SCROLL] Adding potion-aura spell effect to extended properties.");
				ExtendedLiving.get(target).addSpellEffect(new SpellEffect(scroll.getEffectType(), scroll.getEffectID(), scroll.itemID, scroll.getDuration()));
			}
		}
		// NOTES: Target may still be null if a tile was struck and the spell is not an Aura
		// NOTES: MovingObjectPosition will be null if no tile was struck and no ranged entity was spawned

		if (target == null && movingobjectposition == null) {
			System.out.println("[SCROLL] Error in defaultSpell(): Target and MOB are both null.");
			return;
		}

		switch (scroll.getEffectType())
		{
		case DISPEL: // switch behavior it will keep going till it reaches ICE_SPELL and do those
			//affectAreaBlock(scrollStack, world, target, movingobjectposition, isTickingEvent);
			//affectEntity(scrollStack, world, player, target, movingobjectposition, isTickingEvent);
			//break;
		case FIRE_SPELL:
			//affectAreaBlock(scrollStack, world, target, movingobjectposition, isTickingEvent);
			//affectEntity(scrollStack, world, player, target, movingobjectposition, isTickingEvent);
			//break;
		case ICE_SPELL:
			affectAreaBlock(scrollStack, world, target, movingobjectposition, isTickingEvent);
			affectEntity(scrollStack, world, player, target, movingobjectposition, isTickingEvent);
			break;
		case GROWTH:
			if (!scroll.isAura() || isTickingEvent)
				affectAreaBlock(scrollStack, world, player, movingobjectposition, isTickingEvent);
			break;
		case SUMMON:
			summonSpell((ItemScroll) scrollStack.getItem(), world, player);
			break;
		case TELEPORT:
			if (((ItemScroll) scrollStack.getItem()).getDuration() > 1)
				affectEntity(scrollStack, world, player, target, movingobjectposition, isTickingEvent);
			else
				teleportSpell(scrollStack, world, player);// and target for switch location spells
			break;
		case CREATE_BLOCK:
			//affectAreaBlock(scrollStack, world, target, movingobjectposition, isTickingEvent);
			//break;
		case WILT:
			affectAreaBlock(scrollStack, world, target, movingobjectposition, isTickingEvent);
			break;
		default:
			affectEntity(scrollStack, world, player, target, movingobjectposition, isTickingEvent);
			break;
		}
	}

	/**
	 * Handles spawning entities for ranged spells
	 */
	public static final void defaultRangedSpell(ItemStack scroll, World world, EntityPlayer player)
	{
		EntitySpell magic = new EntitySpell(world, player).setScroll(scroll);
		
		if (!world.isRemote) {
			world.spawnEntityInWorld(magic);
		}
	}
	
	/**
	 * Adds a single POTION effect at scroll.effectID[index] to target without checking probability
	 * @param d Value between 0.0 and 1.0 by which to multiply duration.
	 * @param index Index of effectID[] to add
	 * @param isTickingEvent True for aura spells on the update tick. Will set duration based on Potion effect so it will only have one update cycle on target affected.
	 */
	private static final void addPotionEffect(ItemStack scrollStack, EntityLivingBase player, EntityLivingBase target, double d, int index, boolean isTickingEvent)
	{
		ItemScroll scroll = (ItemScroll) scrollStack.getItem();
		if (Potion.potionTypes[scroll.getEffectID(index)].isInstant())
		{
			System.out.println("[SPELL] Potion effect is instant");
			// 4th argument is %damage based on distance
			Potion.potionTypes[scroll.getEffectID(index)].affectEntity(player, target, scroll.getAmplifier(index), d);
		}
		else
		{
			//System.out.println("[SCROLL] Adding potion effect. Duration multipler: " + d + ", effect index: " + index);
			int duration = (int)(d * scroll.getDuration(index));
			// Ticking event duration is capped so that Potion effect will update once:
			// For effects that don't need updating, such as moveSlowdown, duration is unchanged
			if (isTickingEvent && duration > 0)
			{
				if (scroll.getEffectID(index) == Potion.regeneration.id) {
					duration = 50 >> scroll.getAmplifier(index);
				} else if (scroll.getEffectID(index) == Potion.poison.id) {
					duration = 25 >> scroll.getAmplifier(index);
				} else if (scroll.getEffectID(index) == Potion.wither.id) {
					duration = 40 >> scroll.getAmplifier(index);
				} else if (scroll.getEffectID(index) == Potion.hunger.id) {
					duration = 1;
				} else {
					duration = 20;
				}
				if (duration < 0) duration = 0;
			}
			System.out.println("[SPELL] New potion duration: " + duration);

			if (duration > 0)
			{
				target.addPotionEffect(new PotionEffect(scroll.getEffectID(index), duration, scroll.getAmplifier(index), false));
			}
		}
	}

	/**
	 * Handles area of effect spells that target blocks in an area around MovingObjectPostion or target.
	 */
	private static final void affectAreaBlock(ItemStack scroll, World world, EntityLivingBase target, MovingObjectPosition movingobjectposition, boolean isTickingEvent)
	{
		if (movingobjectposition == null && target == null) { System.out.println("[SCROLL] Error. Affect area block no target or position."); return; }
		double radius = ((ItemScroll) scroll.getItem()).getAreaOfEffect();
		double posX = (movingobjectposition != null ? (movingobjectposition.typeOfHit == EnumMovingObjectType.TILE ? movingobjectposition.blockX : movingobjectposition.entityHit.posX) : target.posX);
		double posY = (movingobjectposition != null ? (movingobjectposition.typeOfHit == EnumMovingObjectType.TILE ? movingobjectposition.blockY : movingobjectposition.entityHit.posY) : target.posY + 0.5D);
		double posZ = (movingobjectposition != null ? (movingobjectposition.typeOfHit == EnumMovingObjectType.TILE ? movingobjectposition.blockZ : movingobjectposition.entityHit.posZ) : target.posZ);

		System.out.println("[SCROLL] Affect area block. Radius: " + radius + ", X/Y/Z: " + posX + "/" + posY + "/" + posZ);
		System.out.println("[SCROLL] Affect area block. MOB " + (movingobjectposition != null ? "is valid." : "is null."));

		// AoE too small to operate 'for' loop:
		if (radius < 0.51D)
		{
			affectSingleBlock((ItemScroll) scroll.getItem(), world, (int) posX, (int) posY, (int) posZ, target, isTickingEvent);
			return;
		}

		// Affect all blocks in AoE:
		for (int x = (int)(posX - radius); x < (int)(posX + radius + 1.0D); ++x)
		{
			for (int y = (int)(posY - (radius / 2)); y < (int)(posY + (radius / 2) + 1.0D); ++y)
			{
				for (int z = (int)(posZ - radius); z < (int)(posZ + radius + 1.0D); ++z)
				{
					affectSingleBlock((ItemScroll) scroll.getItem(), world, x, y, z, target, isTickingEvent);
				}
			}
		}
	}

	/**
	 * Affect a single block in world at position x, y, z
	 */
	private static final void affectSingleBlock(ItemScroll scroll, World world, int x, int y, int z, EntityLivingBase target, boolean isTickingEvent)
	{
		int dur = (isTickingEvent && scroll.getDuration() > 0 ? 1 : scroll.getDuration());
		switch (scroll.getEffectType())
		{
		case CREATE_BLOCK:
			if (scroll.getEffectID() == ALBlocks.blockDarkness.blockID) {
				// Try to place in block ABOVE position hit
				if (Block.blocksList[scroll.getEffectID()].canPlaceBlockAt(world, x, y + 1, z)) {
					if (dur > 0 && Config.enableTickingBlocks())
						world.setBlock(x, y + 1, z, ALBlocks.blockDarknessTicking.blockID, dur, 2);
					else
						world.setBlock(x, y + 1, z, ALBlocks.blockDarkness.blockID);
				} else if (((BlockMagicDarkness) ALBlocks.blockDarkness).canDestroyBlockAt(world, x, y, z)) {
					world.destroyBlock(x, y, z, false);
				}
			} else if (scroll.getEffectID() == ALBlocks.blockLight.blockID) {
				if (Block.blocksList[scroll.getEffectID()].canPlaceBlockAt(world, x, y + 1, z)) {
					if (dur > 0 && Config.enableTickingBlocks())
						world.setBlock(x, y + 1, z, ALBlocks.blockLightTicking.blockID, dur, 2);
					else
						world.setBlock(x, y + 1, z, ALBlocks.blockLight.blockID);
				} else if (((BlockMagicLight) ALBlocks.blockLight).canDestroyBlockAt(world, x, y, z)) {
					world.destroyBlock(x, y, z, false);
				}
			} else {
				if (Block.blocksList[scroll.getEffectID()].canPlaceBlockAt(world, x, y + 1, z)
						&& ((Config.enableImprisonSpells() && scroll.itemID == ALItems.scrollImprisonIII.itemID)
								|| (scroll.getEffectID() == (Config.enableTickingBlocks() ? ALBlocks.blockWebTicking.blockID : Block.web.blockID))
								|| world.checkNoEntityCollision(AxisAlignedBB.getBoundingBox(x - 0.75D, y + 1.0D, z - 0.75D, x + 0.75D, y + 2.0D, z + 0.75D)))) {
					if (dur > 0)
						world.setBlock(x, y + 1, z, scroll.getEffectID(), dur, 2);
					else
						world.setBlock(x, y + 1, z, scroll.getEffectID());
				}
			}
			break;
		case DISPEL:
			// Destroys 'Darkness' and 'Light' blocks - use destroyBlock to get particle effects
			if (world.getBlockId(x, y, z) == ALBlocks.blockDarkness.blockID || world.getBlockId(x, y, z) == ALBlocks.blockLight.blockID
			|| (Config.enableTickingBlocks() && (world.getBlockId(x, y, z) == ALBlocks.blockDarknessTicking.blockID
			|| world.getBlockId(x, y, z) == ALBlocks.blockWebTicking.blockID || world.getBlockId(x, y, z) == ALBlocks.blockLightTicking.blockID))) {
				world.destroyBlock(x, y, z, false);
			}
			// Extinguishes fires
			if (world.getBlockId(x, y, z) == Block.fire.blockID) {
				world.setBlockToAir(x, y, z);
			}
			break;
		case FIRE_SPELL:
			if (Block.blocksList[world.getBlockId(x, y, z)] != null && Block.blocksList[world.getBlockId(x, y, z)].blockMaterial.getCanBurn()) {
				world.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "fire.ignite", 1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
				world.setBlock(x, y, z, Block.fire.blockID, 5, 3);
			} else if (world.isAirBlock(x, y + 1, z)) {
				world.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "fire.ignite", 1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
				world.setBlock(x, y + 1, z, Block.fire.blockID, 5, 3);
			}
			break;
		case GROWTH:
			for (int i = 0; i < dur; ++i)
			{
				applyGrowEffect(scroll, world, x, y, z, (EntityPlayer) target);
			}
			break;
		case ICE_SPELL:
			if (world.getBlockMaterial(x, y, z) == Material.water) {
				world.setBlock(x, y, z, Block.ice.blockID);
			} else if (world.getBlockId(x, y, z) == Block.fire.blockID) {
				world.setBlockToAir(x, y, z);
			} // else if (worldgetBlockId(x, y, z) == Block.lava.blockID)
			break;
		case WILT:
			if (Block.blocksList[world.getBlockId(x, y, z)] != null &&
			(Block.blocksList[world.getBlockId(x, y, z)].blockMaterial == Material.cactus
			|| Block.blocksList[world.getBlockId(x, y, z)].blockMaterial == Material.leaves
			|| Block.blocksList[world.getBlockId(x, y, z)].blockMaterial == Material.plants
			|| Block.blocksList[world.getBlockId(x, y, z)].blockMaterial == Material.pumpkin
			|| Block.blocksList[world.getBlockId(x, y, z)].blockMaterial == Material.vine))
			{
				world.destroyBlock(x, y, z, false);
			}
			break;
		default:
			System.out.println("[SCROLL] Error: No affect area block effect defined for type " + scroll.getEffectType());
			break;
		}
	}

	/**
	 * Handles spells that affect entities.
	 * Applies effects if probability check passed. Applies scroll damage and knockback to targets.
	 * @param player - May be center of effect; also needed for POTION effects
	 * @param parTarget - The center of area of effect; if null and no movingobjectposition, player is the center
	 * @param movingobjectposition - Used to determine center of effect; if null, player or target is center
	 * @param isTickingEvent - If true, effect duration will be 0 or 1 second only
	 */
	private static final void affectEntity(ItemStack scrollStack, World world, EntityLivingBase player, EntityLivingBase parTarget, MovingObjectPosition movingobjectposition, boolean isTickingEvent)
	{
		ItemScroll scroll = ((ItemScroll) scrollStack.getItem());
		double radius = scroll.getAreaOfEffect();
		EntityLivingBase dummy = null;
		AxisAlignedBB axisalignedbb;

		if (parTarget != null)
		{
			System.out.println("[SCROLL] Valid target acquired from defaultSpell(). Target " + (parTarget == player ? "is" : "isn't") + " player");
		}
		// No target but valid movingobjectposition: tile was struck
		else if (movingobjectposition != null)
		{
			dummy = new EntityPig(world);
			// DEBUG
			System.out.println("[SCROLL] Center of effect is a TILE. Setting dummy position based on movingobjectposition.");
			dummy.setPosition(movingobjectposition.blockX, movingobjectposition.blockY, movingobjectposition.blockZ);
			// Make box slightly smaller than pig size
			// dummy.boundingBox.setBounds(0.25D, 0.25D, 0.25D, 0.75D, 0.75D, 0.75D);
			parTarget = dummy;
		}
		// No target, no movingobjectposition; what went wrong?
		else
		{
			System.out.println("[SCROLL] Error. Target or MOB should be acquired from defaultSpell().");
			return;
			// parTarget = player;
		}
		// NOTE: This is where AoE types should be dealt with. How do I get direction?
		if (scroll.getRange() == Range.BEAM) {
			// Vec3 lookvector = parTarget.getLookVec();
		}
		axisalignedbb = parTarget.boundingBox.expand(radius, radius / 2, radius);


		// DEBUG
		System.out.println("[SCROLL] axisalignedbb: " + axisalignedbb.toString());
		List nearbyEntities = world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);

		// TEST / DEBUG
		if (nearbyEntities.isEmpty() && parTarget != dummy) { nearbyEntities.add(parTarget);
		System.out.println("[SCROLL] List was empty. Added parTarget."); }

		if (nearbyEntities != null && !nearbyEntities.isEmpty())
		{
			Iterator iterator = nearbyEntities.iterator();

			while (iterator.hasNext())
			{
				EntityLivingBase target = (EntityLivingBase) iterator.next();
				if (!isValidTarget(scrollStack, player, target, dummy, isTickingEvent)) { continue; }
				double d0 = (parTarget.getDistanceSqToEntity(target));
				// DEBUG
				System.out.println("[SCROLL] Distance squared to target: " + d0 + "; radius of effect squared: " + (radius * radius));

				double damage = scroll.getScrollDamage();

				// DEBUG
				if (damage > 0) { System.out.println("[SCROLL] Base scroll damage: " + damage); }

				// Max AoE is set here in case bounding box bounds exceed limit (such as if parTarget has a large bounding box)
				if (d0 <= (radius * radius))
				{
					// Multiplier for scaling probability (d1) and damage (d2) set between 0.0 and 1.0 based on distance
					double d1 = 1.0D - Math.sqrt(d0) / radius, d2 = d1;

					if (target == parTarget || !scroll.isChanceScaled()) { d1 = 1.0D; }
					if (target == parTarget || !scroll.isDamageScaled()) { d2 = 1.0D; }
					damage *= d2;

					if (damage > 0) {
						System.out.println("[SCROLL] Adjusted scroll damage: " + damage);
						target.attackEntityFrom(DamageSource.magic, (float) damage);
					}
					// check knockback effect here
					knockTargetBack(scrollStack, player, target, dummy);

					// Iterate through effects; if target becomes null (from a BANISH spell, for example), stop 
					for (int i = 0; i < scroll.getNumEffects() && target != null; ++i)
					{
						float chance = (float)(scroll.getChance(i) * d1);
						if (scroll.getEffectType() == SpellType.BANISH && target instanceof ISummonedCreature)
							chance = 1.0F;
						System.out.println("[SPELL] Area affect entity, checking effect " + i);
						System.out.println("[SPELL] Chance of success " + chance);

						// Check probability here
						if (player.worldObj.rand.nextFloat() < chance)
						{
							switch(scroll.getEffectType())
							{
							case BANISH:
								banishSpell(scrollStack, world, target);
								break;
							case CHARM:
								if (player instanceof EntityPlayer)
									charmSpell(scrollStack, world, (EntityPlayer) player, target);
								break;
							case DISPEL:
								// ??? and target is NOT player (caster)
								System.out.println("[SCROLL] Clearing active spells from entity");
								ExtendedLiving.get(target).clearActiveSpells();
								// spawn particles
								// play sound
								// remove active potion effects?
								target.extinguish();
								break;
							case EGRESS:
								if (target instanceof EntityPlayer) {
									System.out.println("[SCROLL] Egressing player");
									egress((EntityPlayer) target);
								} else
									System.out.println("[SCROLL] Invalid target type for Egress spell; players only");
								break;
							case FIRE_SPELL:
								world.playSoundEffect((double)target.posX + 0.5D, (double)target.posY + 0.5D, (double)target.posZ + 0.5D, "fire.ignite", 1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
								target.setFire(isTickingEvent ? 1 : scroll.getDuration(i));
								if (i > 0 && target == player) {	// only add fire resistance to caster
									addPotionEffect(scrollStack, player, target, d2, i, isTickingEvent);
								}
								break;
							case ICE_SPELL:
								if (i > 0) {
									addPotionEffect(scrollStack, player, target, d2, i, isTickingEvent);
								}
								break;
							case POTION:
								addPotionEffect(scrollStack, player, target, d2, i, isTickingEvent);
								break;
							default:
								if (!scroll.isAura())
								{
									System.out.println("[SCROLL] Adding default spell effect to entity extended properties. Scroll itemID: " + scroll.itemID);
									ExtendedLiving.get(target).addSpellEffect(new SpellEffect(scroll.getEffectType(), scroll.getEffectID(), scroll.itemID, scroll.getDuration()));
								}
								// apply aura's on tick effects, if any
								else if (isTickingEvent && SpellsMap.instance().hasAuraResult(scroll.itemID))
								{
									System.out.println("[SCROLL] Adding special aura effect to target. Original ID: " + scroll.itemID + ", returned ID: " + SpellsMap.instance().getAuraResult(scroll.itemID));
									ExtendedLiving.get(target).addSpellEffect(new SpellEffect(scroll.getEffectType(), scroll.getEffectID(), SpellsMap.instance().getAuraResult(scroll.itemID), 20));
								}
								break;
							}
						}
						else
						{
							System.out.println("[SPELL] Spell not successful.");
						}
					}
				}
			}
		}
	}

	/**
	 * Applies bonemeal effect from ItemDye
	 */
	private static final boolean applyGrowEffect(ItemScroll scroll, World world, int x, int y, int z, EntityPlayer player)
	{
		int l = world.getBlockId(x, y, z);

		BonemealEvent event = new BonemealEvent(player, world, l, x, y, z);
		if (MinecraftForge.EVENT_BUS.post(event))
		{
			return false;
		}

		if (l == Block.sapling.blockID)
		{
			if (!world.isRemote)
			{
				if ((double)world.rand.nextFloat() < scroll.getChance(0))
				{
					((BlockSapling)Block.sapling).markOrGrowMarked(world, x, y, z, world.rand);
				}
			}

			return true;
		}
		else if (l != Block.mushroomBrown.blockID && l != Block.mushroomRed.blockID)
		{
			if (l != Block.melonStem.blockID && l != Block.pumpkinStem.blockID)
			{
				if (l > 0 && Block.blocksList[l] instanceof BlockCrops)
				{
					if (world.getBlockMetadata(x, y, z) == 7)
					{
						return false;
					}
					else
					{
						if (!world.isRemote)
						{
							((BlockCrops)Block.blocksList[l]).fertilize(world, x, y, z);
						}

						return true;
					}
				}
				else
				{
					int i1;
					int j1;
					int k1;

					if (l == Block.cocoaPlant.blockID)
					{
						i1 = world.getBlockMetadata(x, y, z);
						j1 = BlockDirectional.getDirection(i1);
						k1 = BlockCocoa.func_72219_c(i1);

						if (k1 >= 2)
						{
							return false;
						}
						else
						{
							if (!world.isRemote)
							{
								++k1;
								world.setBlockMetadataWithNotify(x, y, z, k1 << 2 | j1, 2);
							}

							return true;
						}
					}
					else if (l != Block.grass.blockID)
					{
						return false;
					}
					else
					{
						if (!world.isRemote)
						{
							label102:

								for (i1 = 0; i1 < 128; ++i1)
								{
									j1 = x;
									k1 = y + 1;
									int l1 = z;

									for (int i2 = 0; i2 < i1 / 16; ++i2)
									{
										j1 += world.rand.nextInt(3) - 1;
										k1 += (world.rand.nextInt(3) - 1) * world.rand.nextInt(3) / 2;
										l1 += world.rand.nextInt(3) - 1;

										if (world.getBlockId(j1, k1 - 1, l1) != Block.grass.blockID || world.isBlockNormalCube(j1, k1, l1))
										{
											continue label102;
										}
									}

									if (world.getBlockId(j1, k1, l1) == 0)
									{
										if (world.rand.nextInt(10) != 0)
										{
											if (Block.tallGrass.canBlockStay(world, j1, k1, l1))
											{
												world.setBlock(j1, k1, l1, Block.tallGrass.blockID, 1, 3);
											}
										}
										else
										{
											ForgeHooks.plantGrass(world, j1, k1, l1);
										}
									}
								}
						}

						return true;
					}
				}
			}
			else if (world.getBlockMetadata(x, y, z) == 7)
			{
				return false;
			}
			else
			{
				if (!world.isRemote)
				{
					((BlockStem)Block.blocksList[l]).fertilizeStem(world, x, y, z);
				}

				return true;
			}
		}
		else
		{
			if (!world.isRemote)
			{
				if ((double) world.rand.nextFloat() < 0.4D)
				{
					((BlockMushroom)Block.blocksList[l]).fertilizeMushroom(world, x, y, z, world.rand);
				}
			}

			return true;
		}
	}
	
	/**
	 * Affects only a single target at a time without checking probability of success
	 */
	private static final void banishSpell(ItemStack scroll, World world, EntityLivingBase target)
	{
		if (target == null) { System.out.println("[Banish Spell] No target."); }
		float posX = 0, posY = 0, posZ = 0;
		boolean spawnParticles = false;
		String particle = "largesmoke";

		if (target != null && (target instanceof EntityMob || target instanceof EntityAnimal))
		{
			posX = (float) target.posX + (float)(world.rand.nextFloat() * target.width * 2.0F) - (float) target.width;
			posY = (float) target.posY + 1.0F + (float)(world.rand.nextFloat() * target.height);
			posZ = (float) target.posZ + (float)(world.rand.nextFloat() * target.width * 2.0F) - (float) target.width;
			spawnParticles = true;
			// check if spell matches mob ID
			// target.setDead();
			world.removeEntity(target);
		}

		/* For some reason particles only spawn some of the time...
		 * - Always works when impacting tile entity
		 * - Rarely works when removing a target EntityLivingBase
		 * - Tried putting this.setDead() after spawning particles, no change.
		 * - Tried spawning particles before if(!this.worldObj.isRemote), no change.
		 * - Tried spawning particles within if(!this.worldObj.isRemote), but
		 *   then they don't spawn even when hitting tile entities
		 * - Tried changing to 'target.setDead()' same problem
		 */
		for (int i = 0; i < 8 && spawnParticles; ++i) {
			world.spawnParticle(particle, posX, posY, posZ, world.rand.nextGaussian() * 0.4F, world.rand.nextGaussian() * 0.4F + 0.2F, world.rand.nextGaussian() * 0.4F);
		}
		/*
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeShort(particle.length());
			outputStream.writeChars(particle);
			outputStream.writeFloat(posX);
			outputStream.writeFloat(posY);
			outputStream.writeFloat(posZ);
			outputStream.writeFloat(0.2F);
			outputStream.writeFloat(0.2F);
			outputStream.writeFloat(0.2F);
			outputStream.writeFloat(0.2F);
			outputStream.writeInt(8);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = "ALParticle";
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		 */
	}
	/* From EntityVillager - spawns particles around an entity
	private void generateRandomParticles(String par1Str)
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
	 * Affects only a single target at a time without checking probability of success
	 */
	private static final void charmSpell(ItemStack scroll, World world, EntityPlayer player, EntityLivingBase target)
	{
		if (target != null)
		{
			world.setEntityState(target, (byte)7);

			if (target instanceof EntityTameable && !((EntityTameable) target).isTamed())
			{
				((EntityTameable) target).setOwner(player.username);
				((EntityTameable) target).setTamed(true);
				// ((EntityTameable) target).setSitting(true); - this prevents owner from telling pet to stand
				((EntityTameable) target).setPathToEntity((PathEntity) null);
				((EntityTameable) target).setAttackTarget((EntityLivingBase) null);

				// Individual cases:
				if (target instanceof EntityWolf) { target.setHealth(20.0F); }
				else if (target instanceof EntityOcelot) { ((EntityOcelot) target).setTameSkin(1 + world.rand.nextInt(3)); }

				((EntityTameable) target).handleHealthUpdate(Byte.valueOf((byte)7));
			} else if (target instanceof EntityHorse) {
				// ((EntityHorse) target).func_110213_b(((EntityPlayer) player).username);
				((EntityHorse) target).setTamedBy(player);
				// ((EntityHorse) target).setHorseTamed(true); // setTamed function for horses
				((EntityHorse) target).handleHealthUpdate(Byte.valueOf((byte)7));
			}
		}
	}

	/**
	 * Teleports player to last bed slept in; only works in current dimension
	 */
	private static final void egress(EntityPlayer player)
	{
		// ChunkCoordinates cc = player.verifyRespawnCoordinates(player.worldObj, player.getBedLocation(player.dimension), true);
		ChunkCoordinates cc = player.getBedLocation(player.dimension);
		if (cc != null)
		{
			player.setPositionAndUpdate(cc.posX, cc.posY, cc.posZ);
		}
		else {
			System.out.println("[SCROLL] No place to call home! Egress fails.");
		}
	}
	
	/**
	 * Returns true if target is valid for this scroll's effect.
	 */
	private static final boolean isValidTarget(ItemStack scroll, EntityLivingBase player, EntityLivingBase target, EntityLivingBase dummy, boolean isTickingEvent)
	{
		boolean isvalid = true;
		int id = scroll.itemID;
		if (target == dummy) isvalid = false;
		else if (target == player) {
			// if (((ItemScroll)scroll.getItem()).isAura) isvalid = false;
			if ((Config.enableAuraSpells() 
					&& (id == ALItems.scrollRepulsionField.itemID
					|| id == ALItems.scrollHarmAuraI.itemID
					|| id == ALItems.scrollFreezeAuraI.itemID))
					|| id == ALItems.scrollShockwaveI.itemID
					|| id == ALItems.scrollShockwaveII.itemID)
				isvalid = false;

			// } else if (target instanceof EntityPlayer) {
			// Check for magic shielding effects
		} else {
			if (((ItemScroll) scroll.getItem()).isAura() && !isTickingEvent) isvalid = false;
		}
		return isvalid;
	}

	/**
	 * Knocks target away from center of impact or player if dummy is null.
	 */
	private static final void knockTargetBack(ItemStack scroll, EntityLivingBase player, EntityLivingBase target, EntityLivingBase dummy)
	{
		// Get center of impact or player (caster) position
		double centerX = (dummy != null ? dummy.posX : player.posX);
		// double centerY = (movingobjectposition != null ? movingobjectposition.blockY : player.posY);
		double centerZ = (dummy != null ? dummy.posZ : player.posZ);

		int offsetX = target.posX > centerX ? 1 : -1;
		int offsetZ = target.posZ > centerZ ? 1 : -1;

		int amount = ((ItemScroll) scroll.getItem()).getKnockback();
		if (amount > 0)
		{
			// target.addVelocity((double)(-MathHelper.sin(player.rotationYaw * (float)Math.PI / 180.0F) * (float)i * 0.5F), 0.1D, (double)(MathHelper.cos(player.rotationYaw * (float)Math.PI / 180.0F) * (float)i * 0.5F));
			target.addVelocity((double)(offsetX * (float) amount * 0.5F), (0.1D * amount), (double)(offsetZ * (float) amount * 0.5F));
		}
	}
	
	/**
	 * Returns false if no suitable location found for entity to summon
	 */
	public static final boolean placeEntityInWorld(World world, Entity entity, int x, int y, int z)
	{
		if (entity == null) { return false; }
		int i = 0, iMax = (entity.width > 1.0F ? 16 : 4), factor = 1;
		
		entity.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
		
		while (entity.isEntityInsideOpaqueBlock() && i < iMax)
		{
			if (i == 4 && entity.isEntityInsideOpaqueBlock() && entity.width > 1.0F) {
				entity.setLocationAndAngles(x, y, z, 90.0F, 0.0F);
			}
			else if (i == 8 && entity.isEntityInsideOpaqueBlock() && entity.width > 1.0F) {
				entity.setLocationAndAngles(x, y, z, 180.0F, 0.0F);
			}
			else if (i == 12 && entity.isEntityInsideOpaqueBlock() && entity.width > 1.0F) {
				entity.setLocationAndAngles(x, y, z, 270.0F, 0.0F);
			}
			
			switch(i % 4) {
			case 0: entity.setPosition(entity.posX + 0.5D, entity.posY, entity.posZ + 0.5D); break;
			case 1: entity.setPosition(entity.posX, entity.posY, entity.posZ - 1.0D); break;
			case 2: entity.setPosition(entity.posX - 1.0D, entity.posY, entity.posZ); break;
			case 3: entity.setPosition(entity.posX, entity.posY, entity.posZ + 1.0D); break;
			}
			
			++i;
		}
		if (entity.isEntityInsideOpaqueBlock()) {
			System.out.println("Failed to set entity in open space. Returning to default position.");
			entity.setPosition(entity.posX + 0.5D, entity.posY, entity.posZ + 0.5D);
			return false;
		}
		
		return true;
	}
	
	private static final void summonSpell(ItemScroll scroll, World world, EntityLivingBase player)
	{
		MovingObjectPosition movingobjectposition = scroll.getMovingObjectPositionFromPlayer(world, (EntityPlayer)player, true);
		double posX = (movingobjectposition != null ? movingobjectposition.blockX : player.posX);
		double posY = (movingobjectposition != null ? movingobjectposition.blockY : player.posY);
		double posZ = (movingobjectposition != null ? movingobjectposition.blockZ : player.posZ);

		if (movingobjectposition != null && movingobjectposition.typeOfHit == EnumMovingObjectType.TILE)
		{
			for (int i = 0; i < scroll.getNumEffects(); ++i)
			{
				boolean validLocation = false;
				int count = 0;
				int x = movingobjectposition.blockX + i;
				int y = movingobjectposition.blockY + 1;
				int z = movingobjectposition.blockZ + i;
				
				Entity toSummon = null;
				switch(scroll.getEffectID(i)) {
				case ItemScroll.SKELETON:
					toSummon = new SummonSkeleton(world, (EntityPlayer) player, scroll.getDuration(i));
					break;
				default:
					toSummon = new SummonPig(world, scroll.getDuration(i));
					break;
				}
				
				while (toSummon != null && !validLocation && count < 4)
				{
					validLocation = placeEntityInWorld(world, toSummon, x, y, z);
					
					if (!validLocation) {
						if (count % 2 == 0)
							++x;
						else
							++z;
						++count;
					}
				}
				
				if (validLocation && toSummon !=null && !world.isRemote) {
					//toSummon.setLocationAndAngles(x+i, y+1, z+i, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F), 0.0F);
					world.spawnEntityInWorld(toSummon);
				}
				
				/*
				//if (world.getBlockId(x, y + 1, z) == 0 && (world.getBlockId(x, y, z) == Block.grass.blockID || world.getBlockId(x, y, z) == Block.stone.blockID || world.getBlockId(x, y, z) == Block.dirt.blockID || world.getBlockId(x, y, z) == Block.sand.blockID))
				if ((Block.blocksList[world.getBlockId(x, y+1, z)] == null || !Block.blocksList[world.getBlockId(x, y+1, z)].blockMaterial.blocksMovement()) &&
					(Block.blocksList[world.getBlockId(x, y+2, z)] == null || !Block.blocksList[world.getBlockId(x, y+2, z)].blockMaterial.blocksMovement())
					&& world.doesBlockHaveSolidTopSurface(x, y, z))
				{
					Entity toSummon = null;
					switch(scroll.getEffectID(i)) {
					case ItemScroll.SKELETON:
						toSummon = new SummonSkeleton(world, (EntityPlayer) player, scroll.getDuration(i));
						break;
					default:
						toSummon = new SummonPig(world, scroll.getDuration(i));
						break;
					}
					if (toSummon !=null && !world.isRemote) {
						toSummon.setLocationAndAngles(x+i, y+1, z+i, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F), 0.0F);
						world.spawnEntityInWorld(toSummon);
					}
				}
				*/
				if (!validLocation && !world.isRemote)
				{
					System.out.println("[SUMMON] Invalid location. Cannot spawn.");
				}
			}
		}
	}
	
	/**
	 * Returns true if entity teleported successfully
	 */
	private static final boolean teleportSpell(ItemStack scroll, World world, EntityLivingBase player)
	{
		// Pre-teleport check - is teleportation blocked?
		if (MinecraftForge.EVENT_BUS.post(new EnderTeleportEvent(player, player.posX, player.posY, player.posZ, 0))) {
			return false;
		}

		int distance = ((ItemScroll) scroll.getItem()).getAmplifier();
		Vec3 vec3 = player.getLookVec().normalize();
		Vec3 vec3a = vec3.addVector(vec3.xCoord * distance, vec3.yCoord * distance, vec3.zCoord * distance);
		MovingObjectPosition movingobjectposition = world.clip(vec3, vec3a);
		System.out.println("[SCROLL] Teleport vector test.");
		System.out.println("===================================================");
		System.out.println("[SCROLL] MovingObjectPosition: " + (movingobjectposition != null ? "valid" : "null"));
		if (movingobjectposition != null) {
			if (movingobjectposition.typeOfHit != null) {
				// if not null, just teleport to mop position and check for ground level?
				System.out.println("[SCROLL] Type of hit is " + (movingobjectposition.typeOfHit == EnumMovingObjectType.TILE ? "tile" : "entity"));
			} else {
				System.out.println("[SCROLL] No entity was hit.");
			}
		}
		System.out.println("===================================================");

		//Vec3 vec31 = player.getLookVec().normalize();
		//System.out.println("[TELEPORT] Normalized look vector: " + vec31.toString());
		//System.out.println("[TELEPORT] Initial player position: " + player.posX + "/" + player.posY + "/" + player.posZ);
		vec3 = player.getLookVec().normalize();
		double origX = player.posX;
		double origY = player.posY;
		double origZ = player.posZ;

		Block block1, block2;
		boolean hitBlock = false;

		player.posY += player.getEyeHeight() - 0.10000000149011612D;
		// player.posY += 0.5D;

		for (int i = 0; i < ((ItemScroll) scroll.getItem()).getAmplifier() && !hitBlock; ++i)
		{
			player.posX += vec3.xCoord;
			player.posY += vec3.yCoord;
			player.posZ += vec3.zCoord;
			block1 = Block.blocksList[world.getBlockId(MathHelper.floor_double(player.posX+vec3.xCoord),MathHelper.floor_double(player.posY),MathHelper.floor_double(player.posZ+vec3.zCoord))];
			block2 = Block.blocksList[world.getBlockId(MathHelper.floor_double(player.posX+vec3.xCoord),MathHelper.floor_double(player.posY + 1.0D),MathHelper.floor_double(player.posZ+vec3.zCoord))];
			hitBlock = (block1 != null ? block1.blockMaterial.blocksMovement() : false)
					|| (block2 != null ? block2.blockMaterial.blocksMovement() : false);
			/*
        			!world.isAirBlock(MathHelper.floor_double(player.posX),MathHelper.floor_double(player.posY),MathHelper.floor_double(player.posZ))
        			&& !world.isAirBlock(MathHelper.floor_double(player.posX),MathHelper.floor_double(player.posY + 1),MathHelper.floor_double(player.posZ))
        			&& Block.blocksList[world.getBlockId(MathHelper.floor_double(player.posX),MathHelper.floor_double(player.posY),MathHelper.floor_double(player.posZ))].blockMaterial.blocksMovement();
			 */
			// If a block was hit and it's a PHASE spell (can teleport through blocks)
			if (hitBlock && ((ItemScroll) scroll.getItem()).getEffectID() == ItemScroll.PHASE)
			{
				// System.out.println("[TELEPORT] Hit a block");
				int j;
				// Get coordinates of initial position upon impact
				double hitX = player.posX, hitY = player.posY, hitZ = player.posZ;

				// Move until out of obstacle or range of teleport runs out
				for (j = i; j < ((ItemScroll) scroll.getItem()).getAmplifier() && hitBlock; ++j)
				{
					player.posX += vec3.xCoord;
					player.posY += vec3.yCoord;
					player.posZ += vec3.zCoord;
					block1 = Block.blocksList[world.getBlockId(MathHelper.floor_double(player.posX+vec3.xCoord),MathHelper.floor_double(player.posY),MathHelper.floor_double(player.posZ+vec3.zCoord))];
					block2 = Block.blocksList[world.getBlockId(MathHelper.floor_double(player.posX+vec3.xCoord),MathHelper.floor_double(player.posY + 1.0D),MathHelper.floor_double(player.posZ+vec3.zCoord))];
					hitBlock = (block1 != null ? block1.blockMaterial.blocksMovement() : false)
							|| (block2 != null ? block2.blockMaterial.blocksMovement() : false);
				}
				i = j;

				// Didn't make it to valid location - set to last valid location
				if (hitBlock)
				{
					//System.out.println("[TELEPORT] Couldn't clear obstacle");
					player.posX = hitX;
					player.posY = hitY;
					player.posZ = hitZ;
				}// else { System.out.println("[TELEPORT] Cleared obstacle"); }
			}
		}
		if (hitBlock) {
			player.posX -= vec3.xCoord;
			player.posY -= vec3.yCoord;
			player.posZ -= vec3.zCoord;
		}
		//System.out.println("[TELEPORT] Position to teleport to: " + player.posX + "/" + player.posY + "/" + player.posZ);

		// if (toTeleport != null && toTeleport.typeOfHit == EnumMovingObjectType.TILE)
		{

			// System.out.println("[TELEPORT] Position to teleport to: " + posX2 + "/" + posY2 + "/" + posZ2);

			// System.out.println("[TELEPORT] Position to teleport to: " + toTeleport.blockX + "/" + toTeleport.blockY + "/" + toTeleport.blockZ);

			// Shouldn't need to post event again
			/*
			EnderTeleportEvent event = new EnderTeleportEvent(player, player.posX, player.posY, player.posZ, 0);
	        if (MinecraftForge.EVENT_BUS.post(event)) {
	        	//System.out.println("[TELEPORT] Error. Posting event returned false.");
	            return false;
	        }
			 */
			//System.out.println("[TELEPORT] Position from event: " + event.targetX + "/" + event.targetY + "/" + event.targetZ);

			boolean flag = false;

			player.setPosition(player.posX, player.posY, player.posZ);
			/*
            if (player.worldObj.getCollidingBoundingBoxes(player, player.boundingBox).isEmpty())
            if (player.worldObj.isAirBlock((int) player.posX, (int) player.posY, (int) player.posZ)
            	&& player.worldObj.isAirBlock((int) player.posX, (int) (player.posY + 1.0D), (int) player.posZ))
			 */	
			block1 = Block.blocksList[world.getBlockId(MathHelper.floor_double(player.posX),MathHelper.floor_double(player.posY),MathHelper.floor_double(player.posZ))];
			block2 = Block.blocksList[world.getBlockId(MathHelper.floor_double(player.posX),MathHelper.floor_double(player.posY + 1.0D),MathHelper.floor_double(player.posZ))];
			if ((block1 == null || !block1.blockMaterial.blocksMovement()) &&
					(block2 == null || !block2.blockMaterial.blocksMovement()))
			{
				//System.out.println("[TELEPORT] No collisions detected at new position.");
				flag = true;
			}
			if (!flag)
			{
				//System.out.println("[TELEPORT] Collision was detected. Not teleporting.");
				player.setPosition(origX, origY, origZ);
				return false;
			}
			else
			{
				short short1 = 128;

				for (int l = 0; l < short1; ++l)
				{
					double d6 = (double) l / ((double) short1 - 1.0D);
					float f = (player.worldObj.rand.nextFloat() - 0.5F) * 0.2F;
					float f1 = (player.worldObj.rand.nextFloat() - 0.5F) * 0.2F;
					float f2 = (player.worldObj.rand.nextFloat() - 0.5F) * 0.2F;
					double d7 = origX + (player.posX - origX) * d6 + (player.worldObj.rand.nextDouble() - 0.5D) * (double) player.width * 2.0D;
					double d8 = origY + (player.posY - origY) * d6 + player.worldObj.rand.nextDouble() * (double) player.height;
					double d9 = origZ + (player.posZ - origZ) * d6 + (player.worldObj.rand.nextDouble() - 0.5D) * (double) player.width * 2.0D;
					player.worldObj.spawnParticle("portal", d7, d8, d9, (double)f, (double)f1, (double)f2);
				}

				player.worldObj.playSoundEffect(origX, origY, origZ, "mob.endermen.portal", 1.0F, 1.0F);
				player.playSound("mob.endermen.portal", 1.0F, 1.0F);
				return true;
			}
		}
		// return false;
	}
}

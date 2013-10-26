package coolalias.arcanelegacy.handler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import coolalias.arcanelegacy.entity.ExtendedLiving;
import coolalias.arcanelegacy.entity.ExtendedPlayer;
import coolalias.arcanelegacy.item.ALItems;
import coolalias.arcanelegacy.item.ItemRune;
import coolalias.arcanelegacy.item.ItemScroll;
import coolalias.arcanelegacy.network.packet.PacketReduceFallDistance;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ALEventHandler
{
	private static double rand;

	@ForgeSubscribe
	public void onLivingDeathEvent(LivingDeathEvent event)
	{
		ExtendedLiving props = ExtendedLiving.get(event.entityLiving);
		// RESURRECTION
		if (!event.entity.worldObj.isRemote && props.isSpellActive(ItemScroll.SpellType.RESURRECT))
		{
			System.out.println("[EVENT] Entity has died, but resurrection effect is active.");
			event.entityLiving.playSound("damage.hit", 1.0F, (event.entityLiving.worldObj.rand.nextFloat() - event.entityLiving.worldObj.rand.nextFloat()) * 0.2F + 1.0F);
			event.entityLiving.setHealth(0.0F);
			props.setResurrecting(60 >> props.getActiveSpell(ItemScroll.SpellType.RESURRECT).getAmplifier());
			System.out.println("[EXT PROPS] Canceling Living Death Event");
			event.setCanceled(true);
		}
	}

	/**
	 * Called when an entity is attacked by another
	 */
	@ForgeSubscribe
	public void onLivingAttackEvent(LivingAttackEvent event)
	{
		EntityLivingBase entity = event.entityLiving;
		ExtendedLiving props = ExtendedLiving.get(entity);

		// BLINK (i.e. active TELEPORT)
		if (props.isSpellActive(ItemScroll.SpellType.TELEPORT, ItemScroll.BLINK) && event.source instanceof EntityDamageSourceIndirect)
		{
			System.out.println("[EVENT] Entity is being attacked. Blink effect active.");
			for (int i = 0; i < 64; ++i)
			{
				if (teleportRandomly(entity))
				{
					System.out.println("[EVENT] Blink effect successful.");
					event.setCanceled(true);
				}
			}
		}
	}

	@ForgeSubscribe
	public void onLivingHurtEvent(LivingHurtEvent event)
	{
		if (event.entity != null)
		{
			EntityLivingBase entity = event.entityLiving;
			ExtendedLiving props = ExtendedLiving.get(entity);

			// BLINK (i.e. active TELEPORT)
			/*
			if (props.isSpellActive(ItemScroll.TELEPORT) && event.source instanceof EntityDamageSourceIndirect)
            {
				System.out.println("[EVENT] Entity was hurt. Blink effect active.");
                for (int i = 0; i < 64; ++i)
                {
                    if (teleportRandomly(entity))
                    {
                    	System.out.println("[EVENT] Blink effect successful.");
                    	event.ammount = 0.0F;
                        return;
                    }
                }
            }
			 */
			// MAGIC PROTECTION
			// only check for Player so as to not spam 'entity being hurt' messages; turn off DEBUG!
			if (event.ammount > 0 && event.entity instanceof EntityPlayer)
			{
				double reduce = 0.0D;
				System.out.println("[EVENT] Entity is being hurt. Damage: " + event.ammount);
				if (props.isSpellActive(ItemScroll.SpellType.PROTECTION, ItemScroll.PROTECT_ALL))
				{
					reduce += (float) props.getActiveSpell(ItemScroll.SpellType.PROTECTION, ItemScroll.PROTECT_ALL).getAmplifier();
					System.out.println("[EVENT] Entity has magic protection from all sources. Amount to reduce: " + reduce);
				}
				if (props.isSpellActive(ItemScroll.SpellType.PROTECTION, ItemScroll.PROTECT_ARROW) && event.source.isProjectile())
				{
					reduce += (float) props.getActiveSpell(ItemScroll.SpellType.PROTECTION, ItemScroll.PROTECT_ARROW).getAmplifier();
					System.out.println("[EVENT] Entity has magic protection from projectile sources. Amount to reduce: " + reduce);
				}
				if (props.isSpellActive(ItemScroll.SpellType.PROTECTION, ItemScroll.PROTECT_FIRE) && event.source.isFireDamage())
				{
					reduce += (float) props.getActiveSpell(ItemScroll.SpellType.PROTECTION, ItemScroll.PROTECT_FIRE).getAmplifier();
					System.out.println("[EVENT] Entity has magic protection from fire sources. Amount to reduce: " + reduce);
				}
				if (props.isSpellActive(ItemScroll.SpellType.PROTECTION, ItemScroll.PROTECT_MAGIC) && (event.source.isMagicDamage() || event.source.getDamageType() == "wither"))
				{
					reduce += (float) props.getActiveSpell(ItemScroll.SpellType.PROTECTION, ItemScroll.PROTECT_MAGIC).getAmplifier();
					System.out.println("[EVENT] Entity has magic protection from magic sources. Amount to reduce: " + reduce);
				}
				if (reduce > 0)
				{
					event.ammount -= reduce;
					if (event.ammount < 0.0F) event.ammount = 0.0F;
					System.out.println("[EVENT] Magical protection reduced damage to " + event.ammount);
				}
			}
		}
	}

	/**
	 * This is NOT called in Creative Mode
	 */
	@ForgeSubscribe
	public void onLivingFallEvent(LivingFallEvent event)
	{
		if (event.entityLiving instanceof EntityPlayer)
		{
			System.out.println("[EVENT] Living fall event.");
			System.out.println("[EVENT] Entity fall distance " + event.entityLiving.fallDistance + " and event distance " + event.distance);
			boolean debug = (event.entityLiving instanceof EntityPlayer);
			ExtendedLiving props = ExtendedLiving.get(event.entityLiving);

			if (props.getReduceFallDistance() > 0)
			{
				if (debug) System.out.println("[EVENT] Fall distance: " + event.distance);
				if (debug) System.out.println("[EVENT] Reduce fall distance: " + props.getReduceFallDistance());
				event.distance -= props.getReduceFallDistance() < event.distance ? props.getReduceFallDistance() : event.distance;
				if (!props.isSpellActive(ItemScroll.SpellType.JUMP_SPELL) && !props.isSpellActive(ItemScroll.SpellType.SPIDER_CLIMB))
					props.setReduceFallDistance(0);
				if (debug) System.out.println("[EVENT] Adjusted fall distance: " + event.distance);
			}
		}
	}

	@ForgeSubscribe
	public void onLivingJumpEvent(LivingJumpEvent event)
	{
		if (event.entityLiving != null)
		{
			ExtendedLiving props = ExtendedLiving.get(event.entityLiving);

			if (props.isSpellActive(ItemScroll.SpellType.JUMP_SPELL))
			{
				double addY = 0.2D * (props.getActiveSpell(ItemScroll.SpellType.JUMP_SPELL).getAmplifier() + 1);
				event.entity.motionY += addY;
			}
		}
	}

	// This one is called when in Creative Mode
	@ForgeSubscribe
	public void onPlayerFlyableFallEvent(PlayerFlyableFallEvent event) {
		// DEBUG
		// if (event.entity instanceof EntityPlayer) { System.out.println("[EVENT] Falling and CAN fly"); }
	}

	@ForgeSubscribe
	public void onTeleport(EnderTeleportEvent event)
	{
		if (event.entity instanceof EntityPlayer) System.out.println("[EVENT] Entity teleporting!");

		ExtendedLiving props = ExtendedLiving.get((EntityLivingBase) event.entity);
		if (props.isSpellActive(ItemScroll.SpellType.TELEPORT, ItemScroll.NULLIFY))
		{
			if (event.entity instanceof EntityPlayer) System.out.println("[EVENT] Active 'nullify' effect, cannot teleport!");
			event.entity.worldObj.playSoundEffect(event.entity.posX, event.entity.posY, event.entity.posZ, "mob.endermen.hit", 1.0F, 1.0F);
			event.setCanceled(true);
		}
	}

	@ForgeSubscribe
	public void onArrowNockEvent(ArrowNockEvent event)
	{
		if (event.entity != null && event.entity instanceof EntityPlayer)
		{	
			if (event.result != null && event.result.getItem() instanceof ItemScroll)
			{
				ExtendedPlayer props = ExtendedPlayer.get((EntityPlayer) event.entity);

				if (props.wasInterrupted) {
					System.out.println("[EVENT] Spell charge interrupted!");
					props.isCasting = false;
					props.wasInterrupted = false;
					event.setCanceled(true);
				} else {
					props.isCasting = true;
					System.out.println("[EVENT] Spell charging!");
				}
			}
		}
	}

	@ForgeSubscribe
	public void onArrowLooseEvent(ArrowLooseEvent event)
	{
		if (event.entity != null && event.entity instanceof EntityPlayer)
		{
			if (event.bow != null && event.bow.getItem() instanceof ItemScroll)
			{
				ExtendedPlayer props = ExtendedPlayer.get((EntityPlayer) event.entity);

				if (props.wasInterrupted) {
					System.out.println("[EVENT] Spell was interrupted!");
					event.charge = 0;
					event.setCanceled(true);
				} else {
					props.isCasting = false;
					props.wasInterrupted = false;
					System.out.println("[EVENT] Spell cast!");
				}
			}
		}
	}

	/**
	 * Add drops to mobs
	 */
	@ForgeSubscribe
	public void onLivingDropsEvent(LivingDropsEvent event)
	{
		// Just for testing :)
		if (event.source.getDamageType().equals("player"))
		{
			if (event.entityLiving instanceof EntitySheep)
			{
				rand = Math.random();
				if (rand < 0.25D)
				{
					int randRune = MathHelper.clamp_int(((int)(Math.random() * 10)), 0, ItemRune.RUNE_NAME.length - 1);
					event.entityLiving.entityDropItem(new ItemStack(ALItems.runeBasic,2,randRune), 0.0F);
				}
			}
		}
	}

	@ForgeSubscribe
	public void onEntityConstructing(EntityConstructing event)
	{
		if (event.entity instanceof EntityPlayer)
		{
			if (ExtendedLiving.get((EntityLivingBase) event.entity) == null) {
				//System.out.println("[EXT PROPS] Registering extended EntityLivingBase properties for player.");
				ExtendedLiving.register((EntityLivingBase) event.entity);
			}
			if (ExtendedPlayer.get((EntityPlayer) event.entity) == null) {
				//System.out.println("[EXT PROPS] Registering extended EntityPlayer properties.");
				ExtendedPlayer.register((EntityPlayer) event.entity);
			}
		}
		else if (event.entity instanceof EntityLivingBase && event.entity.getExtendedProperties(ExtendedLiving.EXT_PROP_NAME) == null)
		{
			// System.out.println("[EXT PROPS] Registering extended Entity properties.");
			ExtendedLiving.register((EntityLivingBase) event.entity);
		}
	}

	@ForgeSubscribe
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		// Only player's need to sync as other entities are stored on server
		if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer) {
			System.out.println("[EXT PROPS] Syncing properties on Player join world.");
			ExtendedPlayer.get((EntityPlayer) event.entity).sync();
			ExtendedLiving.get((EntityLivingBase) event.entity).sync();
		}
	}

	@ForgeSubscribe
	public void onLivingUpdateEvent(LivingUpdateEvent event)
	{
		ExtendedLiving props = ExtendedLiving.get(event.entityLiving);

		if (props.getResurrectingTime() > 0 && props.isSpellActive(ItemScroll.SpellType.RESURRECT))
		{
			if (!event.entity.worldObj.isRemote)
			{
				System.out.println("[EXT PROPS] Entity is in the process of resurrecting! Time remaining: " + props.getResurrectingTime());
				if (props.updateResurrecting())
				{
					System.out.println("[EXT PROPS] Entity resurrected.");
					event.entityLiving.setHealth((float) (5 << props.getActiveSpell(ItemScroll.SpellType.RESURRECT).getAmplifier()));
					props.removeSpellEffect(ItemScroll.SpellType.RESURRECT, 0);
				}
			}
			// don't continue to update entity
			event.setCanceled(true);
			return;
		}

		props.updateSpellEffects();

		// set event.entity.fallDistance here because LivingFallEvent only called when hitting the ground
		if (event.entity.fallDistance > 0)
		{
			// Reset entity's fall distance to 0 if any of these spells are active
			if (props.isSpellActive(ItemScroll.SpellType.FEATHER_FALL) || props.isSpellActive(ItemScroll.SpellType.FLY_SPELL))
			{
				// Adjust for server/client discrepancy of 0.2F in fallDistance
				event.entity.fallDistance = event.entity.worldObj.isRemote ? 0.0F : -0.2F;
			}
			// Adjust entity's downward motion for Feather Fall spell
			if (props.isSpellActive(ItemScroll.SpellType.FEATHER_FALL))
			{
				event.entity.motionY = -0.15D;
			}
		}

		// SPIDER CLIMBING
		if (props.isSpellActive(ItemScroll.SpellType.SPIDER_CLIMB) && event.entity.isCollidedHorizontally)
		{
			if (event.entity.isSneaking())
			{
				event.entity.motionY = -0.1F;
				props.addReduceFallDistance(0.1F);
			}
			else
			{
				event.entity.motionY = 0.1F;
				if (props.getReduceFallDistance() > 0) props.addReduceFallDistance(-0.1F);
			}
			//props.reduceFallDistance += event.entity.motionY;
			event.entity.fallDistance = 0.0F;
		}
		
		// Update PLAYER specific variables
		if (event.entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) event.entity;
			ExtendedPlayer props2 = ExtendedPlayer.get(player);

			if (props2.isCasting && player.hurtTime > 0) { props2.wasInterrupted = true; }
			if (props2.wasInterrupted && !props2.isCasting && player.hurtTime == 0) { props2.wasInterrupted = false; }
			
			// SIDED HORIZONTAL COLLISION TEST
			if (!event.entity.worldObj.isRemote && event.entity.isCollidedHorizontally)
				System.out.println("[EVENT] Colliding horizontally server side: fall distance: " + event.entity.fallDistance);
			if (event.entity.worldObj.isRemote && event.entity.isCollidedHorizontally)
				System.out.println("[EVENT] Colliding horizontally client side: fall distance: " + event.entity.fallDistance);
			
			// WATER WALKING
			/*
			if (player.worldObj.getBlockId((int) player.posX, (int) player.posY - 1, (int) player.posZ) == Block.waterStill.blockID)
			{
				player.motionY = 0;
			}
			 */
			/*
			// set player.fallDistance here because LivingFallEvent only called when hitting the ground
			if (player.fallDistance > 0)
			{
				// Reset player's fall distance to 0 if any of these spells are active
				if (props.isSpellActive(ItemScroll.FEATHER_FALL) || props.isSpellActive(ItemScroll.FLY_SPELL))
				{
					// Adjust for server/client discrepancy of 0.2F in fallDistance
					player.fallDistance = player.worldObj.isRemote ? 0.0F : -0.2F;
				}
				// Adjust player's downward motion for Feather Fall spell
				if (props.isSpellActive(ItemScroll.FEATHER_FALL))
				{
					player.motionY = -0.15D;
				}
			}
			 */
			/*
			// Holding wings of flying)
			ItemStack heldItem = player.getHeldItem();
			if (heldItem != null && heldItem.itemID == MagicMod.spellBasic.itemID) {
				player.capabilities.allowFlying = true;
			}
			else {
				player.capabilities.allowFlying = false;
			}
			 */
		}
	}

	/**
	 * PRIVATE METHODS
	 */
	/**
	 * Returns true if entity moved to a new random position
	 */
	private final boolean teleportRandomly(EntityLivingBase entity)
	{
		double rand1 = entity.worldObj.rand.nextDouble() - 0.5D, rand2 = entity.worldObj.rand.nextDouble() - 0.5D;
		rand1 = rand1 > 0 && rand1 < 0.25D ? 0.25D : rand1 < 0 && rand1 > -0.25D ? -0.25D : rand1;
		rand2 = rand2 > 0 && rand2 < 0.25D ? 0.25D : rand2 < 0 && rand2 > -0.25D ? -0.25D : rand2;
		System.out.println("[TELEPORT] Modified: rand1: " + rand1 + ", rand2: " + rand2);
		double d0 = entity.posX + rand1 * 32.0D;
		double d1 = entity.posY + (double)(entity.worldObj.rand.nextInt(32) - 16);
		double d2 = entity.posZ + rand2 * 32.0D;
		return teleportTo(entity, d0, d1, d2);
	}

	/**
	 * Teleport the player to position x, y, z
	 */
	private final boolean teleportTo(EntityLivingBase entity, double par1, double par3, double par5)
	{
		EnderTeleportEvent event = new EnderTeleportEvent(entity, par1, par3, par5, 0);
		if (MinecraftForge.EVENT_BUS.post(event)) {
			return false;
		}

		double d3 = entity.posX;
		double d4 = entity.posY;
		double d5 = entity.posZ;
		entity.posX = event.targetX;
		entity.posY = event.targetY;
		entity.posZ = event.targetZ;
		boolean flag = false;
		int i = MathHelper.floor_double(entity.posX);
		int j = MathHelper.floor_double(entity.posY);
		int k = MathHelper.floor_double(entity.posZ);
		int l;

		if (entity.worldObj.blockExists(i, j, k))
		{
			boolean flag1 = false;

			while (!flag1 && j > 0)
			{
				l = entity.worldObj.getBlockId(i, j - 1, k);

				if (l != 0 && Block.blocksList[l].blockMaterial.blocksMovement())
				{
					flag1 = true;
				}
				else
				{
					--entity.posY;
					--j;
				}
			}

			if (flag1)
			{
				entity.setPosition(entity.posX, entity.posY, entity.posZ);

				Block block1 = Block.blocksList[entity.worldObj.getBlockId(MathHelper.floor_double(entity.posX),MathHelper.floor_double(entity.posY),MathHelper.floor_double(entity.posZ))];
				Block block2 = Block.blocksList[entity.worldObj.getBlockId(MathHelper.floor_double(entity.posX),MathHelper.floor_double(entity.posY + 1.0D),MathHelper.floor_double(entity.posZ))];
				if ((block1 == null || !block1.blockMaterial.blocksMovement()) &&
						(block2 == null || !block2.blockMaterial.blocksMovement()))

					//if (entity.worldObj.isAirBlock((int) entity.posX, (int) entity.posY, (int) entity.posZ)
					//		&& entity.worldObj.isAirBlock((int) entity.posX, (int) (entity.posY + 1.0D), (int) entity.posZ))
				{
					flag = true;
				}
			}
		}

		if (!flag)
		{
			entity.setPosition(d3, d4, d5);
			return false;
		}
		else
		{
			short short1 = 128;

			for (l = 0; l < short1; ++l)
			{
				double d6 = (double)l / ((double)short1 - 1.0D);
				float f = (entity.worldObj.rand.nextFloat() - 0.5F) * 0.2F;
				float f1 = (entity.worldObj.rand.nextFloat() - 0.5F) * 0.2F;
				float f2 = (entity.worldObj.rand.nextFloat() - 0.5F) * 0.2F;
				double d7 = d3 + (entity.posX - d3) * d6 + (entity.worldObj.rand.nextDouble() - 0.5D) * (double) entity.width * 2.0D;
				double d8 = d4 + (entity.posY - d4) * d6 + entity.worldObj.rand.nextDouble() * (double) entity.height;
				double d9 = d5 + (entity.posZ - d5) * d6 + (entity.worldObj.rand.nextDouble() - 0.5D) * (double) entity.width * 2.0D;
				entity.worldObj.spawnParticle("portal", d7, d8, d9, (double)f, (double)f1, (double)f2);
			}

			entity.worldObj.playSoundEffect(d3, d4, d5, "mob.endermen.portal", 1.0F, 1.0F);
			entity.playSound("mob.endermen.portal", 1.0F, 1.0F);
			return true;
		}
	}
}

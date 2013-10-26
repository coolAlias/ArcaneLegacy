package coolalias.arcanelegacy.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class ExtendedPlayer implements IExtendedEntityProperties
{
	/** The name of these extended properties */
	public final static String EXT_PROP_NAME = "ALExtendedPlayer";

	/** The player to whom these extended properties belong. */
	private final EntityPlayer player;

	/** Used to handle interrupting of spell casting in EventHandler. */
	public boolean isCasting, wasInterrupted;

	public ExtendedPlayer(EntityPlayer player) {
		this.player = player;
	}

	/**
	 * Used to register these extended properties for EntityPlayer during EntityConstructing event
	 */
	public static void register(EntityPlayer player)
	{
		player.registerExtendedProperties(EXT_PROP_NAME, new ExtendedPlayer(player));
	}

	/**
	 * Returns ExtendedPlayer properties for EntityPlayer
	 */
	public static ExtendedPlayer get(EntityPlayer player)
	{
		return (ExtendedPlayer) player.getExtendedProperties(EXT_PROP_NAME);
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
		compound.setTag(EXT_PROP_NAME, properties);
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
		// NBTTagCompound properties = (NBTTagCompound) compound.getTag(EXT_PROP_NAME);
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
		// Each active spell effect contains 4 integers at 4 bytes each = 16 bytes per effect
		/*
		ByteArrayOutputStream bos = new ByteArrayOutputStream(12+(this.activeSpellEffects.size()*16));
		DataOutputStream outputStream = new DataOutputStream(bos);
		System.out.println("[EXT PROPS] active spell effects size: " + this.activeSpellEffects.size());
		System.out.println("[EXT PROPS] Server side player step height: " + player.stepHeight);
		try {
			outputStream.writeInt(this.activeSpellEffects.size());
			Iterator iterator = this.activeSpellEffects.values().iterator();
			while (iterator.hasNext())
			{
				SpellEffect spelleffect = (SpellEffect)iterator.next();
				outputStream.writeInt(spelleffect.getEffectType());
				outputStream.writeInt(spelleffect.getEffectID());
				outputStream.writeInt(spelleffect.getScrollID());
				outputStream.writeInt(spelleffect.getDuration());
			}

			outputStream.writeFloat(this.reduceFallDistance);
			// Hard-coded workaround for problem of onEntityJoinWorld server side initially has stepHeight = 0;
			// If ever add spells to reduce stepHeight, need to check if active here
			outputStream.writeFloat(player.stepHeight < 0.5F ? 0.5F : player.stepHeight);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = ModInfo.CHANNEL_EXT_PROPS;
		packet.data = bos.toByteArray();
		packet.length = bos.size();

		Side side = FMLCommonHandler.instance().getEffectiveSide();

		if (side == Side.SERVER) {
			EntityPlayerMP player1 = (EntityPlayerMP) player;
			PacketDispatcher.sendPacketToPlayer(packet, (Player)player1);
		} else if (side == Side.CLIENT) {
			//EntityClientPlayerMP player1 = (EntityClientPlayerMP) player;
			//player1.sendQueue.addToSendQueue(packet);
		} else {
			// We are on the Bukkit server.
		}
		 */
	}
}

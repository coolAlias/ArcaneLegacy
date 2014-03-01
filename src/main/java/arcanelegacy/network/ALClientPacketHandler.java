package arcanelegacy.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import arcanelegacy.ModInfo;
import arcanelegacy.entity.ExtendedLiving;
import arcanelegacy.item.ItemScroll;
import arcanelegacy.spells.SpellEffect;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ALClientPacketHandler implements IPacketHandler
{
	/** Define client packet types */
	public final static byte PACKET_REDUCE_FALL = 1, PACKET_ACTIVE_SPELL = 2, PACKET_RES_TIMER = 3;
	
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
	{
		System.out.println("[CLIENT] Received server packet.");

		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		byte packetType;

		try {
			packetType = inputStream.readByte();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		if (packet.channel.equals(ModInfo.CHANNEL_MAIN))
		{
			switch (packetType) {
			default: System.out.println("[CLIENT][WARNING] Unknown packet type " + packetType);
			}

		} else if (packet.channel.equals(ModInfo.CHANNEL_EXT_PROPS))
		{
			switch (packetType) {
			case PACKET_ACTIVE_SPELL: handleSpellEffects(packet, (EntityPlayer) player, inputStream); break;
			case PACKET_REDUCE_FALL: handleReduceFall(packet, (EntityPlayer) player, inputStream); break;
			case PACKET_RES_TIMER: handleResurrectionTimer(packet, (EntityPlayer) player, inputStream); break;
			default: System.out.println("[CLIENT][WARNING] Unknown packet type " + packetType);
			}
		}
	}
	
	private void handleReduceFall(Packet250CustomPayload packet, EntityPlayer player, DataInputStream inputStream)
	{
		System.out.println("[CLIENT] Handling ReduceFall packet.");

		float reduceBy;

		try {
			reduceBy = inputStream.readFloat();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		ExtendedLiving.get(player).setReduceFallDistance(reduceBy);
	}

	/**
	 * Syncs active spell effect information for ExtendedLiving
	 */
	private void handleSpellEffects(Packet250CustomPayload packet, EntityPlayer player, DataInputStream inputStream)
	{
		System.out.println("[CLIENT] Handling ExtendedProperties packet.");

		ExtendedLiving props = ExtendedLiving.get((EntityLivingBase) player);
		props.clearActiveSpells();

		int numSpellEffects;

		try {
			numSpellEffects = inputStream.readInt();
			for (int i = 0; i < numSpellEffects; ++i) {
				props.addSpellEffect(SpellEffect.readFromStream(inputStream));
			}
			props.setReduceFallDistance(inputStream.readFloat());
			player.stepHeight = inputStream.readFloat();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		System.out.println("[PACKET] Player step height: " + player.stepHeight);
		player.capabilities.allowFlying = (player.capabilities.isCreativeMode || props.isSpellActive(ItemScroll.SpellType.FLY_SPELL));
		if (player.capabilities.isFlying) { player.capabilities.isFlying = player.capabilities.allowFlying; }
	}

	private void handleResurrectionTimer(Packet250CustomPayload packet, EntityPlayer player, DataInputStream inputStream)
	{
		System.out.println("[CLIENT] Handling Resurrection Timer packet.");

		int time;

		try {
			time = inputStream.readInt();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		ExtendedLiving.get((EntityLivingBase) player).setResurrecting(time);
	}
}

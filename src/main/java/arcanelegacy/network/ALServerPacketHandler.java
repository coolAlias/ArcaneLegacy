package arcanelegacy.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import arcanelegacy.ArcaneLegacy;
import arcanelegacy.ModInfo;
import arcanelegacy.inventory.InventoryWand;
import arcanelegacy.item.ItemWand;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class ALServerPacketHandler implements IPacketHandler
{
	/** Defining packet ids allow for subtypes of Packet250CustomPayload all on single channel */
	public static final byte OPEN_SERVER_GUI = 1, NEXT_ACTIVE_SLOT = 2, PACKET_REDUCE_FALL = 3;
	
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
	{
		System.out.println("[SERVER] Received client packet.");
		
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		byte packetType;
		
		try {
			packetType = inputStream.readByte();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		if (packet.channel.equals(ModInfo.CHANNEL_MAIN)) {
			switch(packetType) {
			case OPEN_SERVER_GUI: handleOpenServerGui(packet, (EntityPlayer) player, inputStream); break;
			case NEXT_ACTIVE_SLOT: handleNextActiveSlot(packet, (EntityPlayer) player, inputStream); break;
			default: System.out.println("[SERVER][WARNING] Unknown packet type " + packetType);
			}
		}
	}
	
	private void handleOpenServerGui(Packet250CustomPayload packet, EntityPlayer player, DataInputStream inputStream)
	{
		int guiID;
		
		try {
			guiID = inputStream.readInt();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		player.openGui(ArcaneLegacy.instance, guiID, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
	}
	
	private void handleNextActiveSlot(Packet250CustomPayload packet, EntityPlayer player, DataInputStream inputStream)
	{
		byte dir;

		try {
			dir = inputStream.readByte();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		if (player.getHeldItem() == null && !(player.getHeldItem().getItem() instanceof ItemWand))
		{
			System.out.println("[SEVERE][PACKET] Unable to process next active slot packet when not holding a Wand");
			return;
		}
		if (dir == 0) new InventoryWand(player.getHeldItem()).nextActiveSlot();
		else if (dir == 1) new InventoryWand(player.getHeldItem()).prevActiveSlot();
	}
	
	/**
	 * Sends a packet to change active slot either from CONTAINER or INVENTORY
	 */
	public static final void sendNextActiveSlotPacket(byte dir)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream outputStream = new DataOutputStream(bos);
		
		try {
			outputStream.writeByte(NEXT_ACTIVE_SLOT);
			outputStream.writeByte(dir);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		PacketDispatcher.sendPacketToServer(PacketDispatcher.getPacket(ModInfo.CHANNEL_MAIN, bos.toByteArray()));
	}
	
	/**
	 * Sends a packet to the server telling it to open gui for player
	 */
	public static final void sendOpenGuiPacket(int guiId)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream outputStream = new DataOutputStream(bos);
		
		try {
			outputStream.writeByte(OPEN_SERVER_GUI);
			outputStream.writeInt(guiId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		PacketDispatcher.sendPacketToServer(PacketDispatcher.getPacket(ModInfo.CHANNEL_MAIN, bos.toByteArray()));
	}
}

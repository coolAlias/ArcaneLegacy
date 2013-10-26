package coolalias.arcanelegacy.network.packet;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.network.packet.Packet250CustomPayload;
import coolalias.arcanelegacy.ModInfo;
import coolalias.arcanelegacy.network.ALServerPacketHandler;

public class PacketReduceFallDistance
{
	/**
	 * Returns a packet containing amount to reduce fall distance
	 */
	public static Packet250CustomPayload getPacket(float reduceBy)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream(5);
		DataOutputStream outputStream = new DataOutputStream(bos);
		
		try {
			outputStream.writeByte(ALServerPacketHandler.PACKET_REDUCE_FALL);
			outputStream.writeFloat(reduceBy);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return new Packet250CustomPayload(ModInfo.CHANNEL_EXT_PROPS, bos.toByteArray());
	}
}

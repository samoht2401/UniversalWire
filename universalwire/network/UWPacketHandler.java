package samoht2401.universalwire.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import samoht2401.universalwire.UniversalWire;
import samoht2401.universalwire.render.RenderInfoSystem;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class UWPacketHandler implements IPacketHandler {

	public final static String CHANNEL = "UWGeneralPacket";

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data));
		try {
			int packetID = data.read();
			switch (packetID) {
			case PacketIDs.SYSTEM_UPDATE: {
				PacketSystem p = new PacketSystem();
				p.readData(data);
				if (RenderInfoSystem.infos != null && RenderInfoSystem.infos.contains(p.info))
					RenderInfoSystem.get(p.info.id).buffer.replaceBy(p.info.buffer);
				else
					RenderInfoSystem.infos.add(p.info);
				break;
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

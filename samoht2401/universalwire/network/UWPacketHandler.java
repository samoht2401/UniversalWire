package samoht2401.universalwire.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import samoht2401.universalwire.UniversalWire;
import samoht2401.universalwire.render.RenderInfoCable;
import samoht2401.universalwire.render.RenderInfoSystem;
import samoht2401.universalwire.render.RenderInfoTank;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class UWPacketHandler implements IPacketHandler {

	public final static String CHANNEL = "UWGeneralPacket";

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data));
		try {
			EntityPlayer entity = (EntityPlayer) player;
			int packetID = data.read();
			switch (packetID) {
				case PacketIDs.SYSTEM_UPDATE: {
					PacketSerializableInfo p = new PacketSerializableInfo(PacketIDs.SYSTEM_UPDATE);
					p.readData(data, new RenderInfoSystem());
					RenderInfoSystem info = (RenderInfoSystem) p.info;
					if (RenderInfoSystem.infos != null && RenderInfoSystem.infos.contains(info))
						RenderInfoSystem.get(info.id).buffer.replaceBy(info.buffer);
					else
						RenderInfoSystem.infos.add(info);
					break;
				}
				case PacketIDs.CABLE_UPDATE: {
					PacketSerializableInfo p = new PacketSerializableInfo(PacketIDs.CABLE_UPDATE);
					p.readData(data, new RenderInfoCable());
					RenderInfoCable info = (RenderInfoCable) p.info;
					World world = entity.worldObj;
					TileEntity te = world.getBlockTileEntity(info.x, info.y, info.z);
					if (te instanceof ISynchronisable) {
						ISynchronisable tileEntity = (ISynchronisable) te;
						tileEntity.updateSynchronisedInfo(info);
					}
					break;
				}
				case PacketIDs.TANK_UPDATE: {
					PacketSerializableInfo p = new PacketSerializableInfo(PacketIDs.TANK_UPDATE);
					p.readData(data, new RenderInfoTank());
					RenderInfoTank info = (RenderInfoTank) p.info;
					World world = entity.worldObj;
					TileEntity te = world.getBlockTileEntity(info.x, info.y, info.z);
					if (te instanceof ISynchronisable) {
						ISynchronisable tileEntity = (ISynchronisable) te;
						tileEntity.updateSynchronisedInfo(info);
					}
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

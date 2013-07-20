package samoht2401.universalwire.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import samoht2401.universalwire.render.RenderInfoSystem;

public class PacketSystem extends UWPacket {

	RenderInfoSystem info;

	public PacketSystem() {
	}

	public PacketSystem(RenderInfoSystem info) {
		this.info = info;
	}

	@Override
	public int getID() {
		return PacketIDs.SYSTEM_UPDATE;
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		info = new RenderInfoSystem();
		info.readData(data);
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		info.writeData(data);
	}

}

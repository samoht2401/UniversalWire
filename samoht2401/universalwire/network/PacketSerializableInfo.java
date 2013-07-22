package samoht2401.universalwire.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import samoht2401.universalwire.render.RenderInfoSystem;

public class PacketSerializableInfo extends UWPacket {

	public ISerializable info;
	private int id;

	public PacketSerializableInfo(int id) {
		this.id = id;
	}

	public PacketSerializableInfo(int id, ISerializable info) {
		this.id = id;
		this.info = info;
	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	public void readData(DataInputStream data, Object instance)
			throws IOException {
		if (!(instance instanceof ISerializable))
			return;
		info = (ISerializable)instance;
		info.readData(data);
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		info.writeData(data);
	}

}

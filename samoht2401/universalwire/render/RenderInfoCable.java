package samoht2401.universalwire.render;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import buildcraft.core.network.IClientState;

import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;

public class RenderInfoCable implements IClientState {
	public int systemId;
	public Icon[] textures;
	public ArrayList<ForgeDirection> connections;

	@Override
	public RenderInfoCable clone() {
		RenderInfoCable result = new RenderInfoCable();
		result.systemId = systemId;
		result.textures = this.textures;
		result.connections = new ArrayList<ForgeDirection>();
		return result;
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		data.write(systemId);
		data.writeInt(connections.size());
		for (ForgeDirection dir : connections) {
			data.writeInt(dir.ordinal());
		}
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		systemId = data.read();
		int coSize = data.readInt();
		connections = new ArrayList<ForgeDirection>();
		for (int i = 0; i < coSize; i++) {
			connections.add(ForgeDirection.getOrientation(data.readInt()));
		}
	}
}

package samoht2401.universalwire.render;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import samoht2401.universalwire.network.ISerializable;
import samoht2401.universalwire.util.FacadeMatrix;

import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;

public class RenderInfoCable implements ISerializable {

	public int x;
	public int y;
	public int z;
	public int systemId;
	public Icon[] textures;
	public ArrayList<ForgeDirection> connections;
	public FacadeMatrix facades;

	@Override
	public RenderInfoCable clone() {
		RenderInfoCable result = new RenderInfoCable();
		result.x = x;
		result.y = y;
		result.z = z;
		result.systemId = systemId;
		result.textures = this.textures;
		result.connections = new ArrayList<ForgeDirection>();
		if (result.facades == null)
			result.facades = new FacadeMatrix();
		else
			result.facades = facades.clone();
		return result;
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		data.writeInt(x);
		data.writeInt(y);
		data.writeInt(z);
		data.writeInt(systemId);
		data.writeInt(connections.size());
		for (ForgeDirection dir : connections) {
			data.writeInt(dir.ordinal());
		}
		facades.writeData(data);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		x = data.readInt();
		y = data.readInt();
		z = data.readInt();
		systemId = data.readInt();
		int coSize = data.readInt();
		connections = new ArrayList<ForgeDirection>();
		for (int i = 0; i < coSize; i++) {
			connections.add(ForgeDirection.getOrientation(data.readInt()));
		}
		facades = new FacadeMatrix();
		facades.readData(data);
	}
}

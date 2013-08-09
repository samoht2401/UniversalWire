package samoht2401.universalwire.render;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import samoht2401.universalwire.network.ISerializable;

public class RenderInfoTank implements ISerializable {

	public int x;
	public int y;
	public int z;
	public int fluidId;
	public int amount;
	public boolean[] fluidFaceFlowing;
	public boolean fluidFromTop;

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		data.writeInt(x);
		data.writeInt(y);
		data.writeInt(z);
		data.writeInt(fluidId);
		data.writeInt(amount);
		data.writeInt(fluidFaceFlowing.length);
		for (int i = 0; i < fluidFaceFlowing.length; i++) {
			data.writeBoolean(fluidFaceFlowing[i]);
		}
		data.writeBoolean(fluidFromTop);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		x = data.readInt();
		y = data.readInt();
		z = data.readInt();
		fluidId = data.readInt();
		amount = data.readInt();
		int length = data.readInt();
		fluidFaceFlowing = new boolean[length];
		for (int i = 0; i < length; i++) {
			fluidFaceFlowing[i] = data.readBoolean();
		}
		fluidFromTop = data.readBoolean();
	}
}

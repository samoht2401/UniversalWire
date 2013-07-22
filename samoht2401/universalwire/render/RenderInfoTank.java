package samoht2401.universalwire.render;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import samoht2401.universalwire.network.ISerializable;


public class RenderInfoTank implements ISerializable {

	public int x;
	public int y;
	public int z;
	public int fluidId;
	public int amount;

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		data.writeInt(x);
		data.writeInt(y);
		data.writeInt(z);
		data.writeInt(fluidId);
		data.writeInt(amount);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		x = data.readInt();
		y = data.readInt();
		z = data.readInt();
		fluidId = data.readInt();
		amount = data.readInt();
	}
}

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
	public int[] fluidFromFace;
	public boolean isLiquidGoDown;
	public int[] isLiquidCommingTop;

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		data.writeInt(x);
		data.writeInt(y);
		data.writeInt(z);
		data.writeInt(fluidId);
		data.writeInt(amount);
		data.writeInt(fluidFromFace.length);
		for (int i = 0; i < fluidFromFace.length; i++) {
			data.writeInt(fluidFromFace[i]);
		}
		data.writeBoolean(isLiquidGoDown);
		data.writeInt(isLiquidCommingTop.length);
		for (int i = 0; i < isLiquidCommingTop.length; i++) {
			data.writeInt(isLiquidCommingTop[i]);
		}
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		x = data.readInt();
		y = data.readInt();
		z = data.readInt();
		fluidId = data.readInt();
		amount = data.readInt();
		int length = data.readInt();
		fluidFromFace = new int[length];
		for (int i = 0; i < length; i++) {
			fluidFromFace[i] = data.readInt();
		}
		isLiquidGoDown = data.readBoolean();
		length = data.readInt();
		isLiquidCommingTop = new int[length];
		for (int i = 0; i < length; i++) {
			isLiquidCommingTop[i] = data.readInt();
		}
	}
}

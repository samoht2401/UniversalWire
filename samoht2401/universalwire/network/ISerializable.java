package samoht2401.universalwire.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraftforge.common.ForgeDirection;

public interface ISerializable {

	public void writeData(DataOutputStream data) throws IOException;

	public void readData(DataInputStream data) throws IOException;
}

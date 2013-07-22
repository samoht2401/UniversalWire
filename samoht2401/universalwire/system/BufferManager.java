package samoht2401.universalwire.system;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import samoht2401.universalwire.network.ISerializable;

import net.minecraft.nbt.NBTTagCompound;

public class BufferManager implements ISerializable {

	private ArrayList<IEnergyBuffer> buffers;
	private int energyBuffer;
	private int maxBuffer;

	public BufferManager() {
		buffers = new ArrayList<IEnergyBuffer>();
		energyBuffer = 0;
		maxBuffer = 0;
	}

	public int getMaxBuffer() {
		return maxBuffer;
	}

	public void addBuffer(IEnergyBuffer buffer) {
		buffers.add(buffer);
		maxBuffer += buffer.getBufferSize();
	}

	public int getBuffer() {
		return energyBuffer;
	}

	public int pushToBuffer(int val) {
		energyBuffer += val;
		if (energyBuffer > maxBuffer) {
			int surplus = energyBuffer - maxBuffer;
			energyBuffer = maxBuffer;
			return surplus;
		}
		return 0;
	}

	public int pullFromBuffer(int val) {
		energyBuffer -= val;
		if (energyBuffer < 0) {
			int sousplus = energyBuffer;
			energyBuffer = 0;
			return val + sousplus;
		}
		return val;
	}

	public int emptyBuffer() {
		int val = energyBuffer;
		energyBuffer = 0;
		return val;
	}

	public float getFillRatio(IEnergyBuffer buffer) {
		return energyBuffer * 1f / maxBuffer;
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		data.writeInt(energyBuffer);
		data.writeInt(maxBuffer);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		energyBuffer = data.readInt();
		maxBuffer = data.readInt();
	}
	
	public void writeToNBT(NBTTagCompound comp) {
		comp.setInteger("energy", energyBuffer);
		comp.setInteger("max", maxBuffer);
	}
	
	public void readFromNBT(NBTTagCompound comp) {
		energyBuffer = comp.getInteger("energy");
		maxBuffer = comp.getInteger("max");
	}

	@Override
	public BufferManager clone() {
		BufferManager result = new BufferManager();
		result.energyBuffer = energyBuffer;
		result.maxBuffer = maxBuffer;
		return result;
	}
	
	public void replaceBy(BufferManager other){
		this.energyBuffer = other.energyBuffer;
		this.maxBuffer = other.maxBuffer;
	}
}

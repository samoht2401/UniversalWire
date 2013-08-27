package samoht2401.universalwire.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;

public class FacadeMatrix {

	private int[] facadeId = new int[6];
	private int[] facadeMeta = new int[6];
	private boolean dirty = false;

	public boolean hasFacade(ForgeDirection dir) {
		if (facadeId[dir.ordinal()] != 0)
			return true;
		return false;
	}

	public int getFacadeId(ForgeDirection dir) {
		return facadeId[dir.ordinal()];
	}

	public int getFacadeMeta(ForgeDirection dir) {
		return facadeMeta[dir.ordinal()];
	}

	public void setFacade(ForgeDirection dir, int blockId, int blockMeta) {
		if (facadeId[dir.ordinal()] != blockId || facadeMeta[dir.ordinal()] != blockMeta) {
			facadeId[dir.ordinal()] = blockId;
			facadeMeta[dir.ordinal()] = blockMeta;
			dirty = true;
		}
	}

	public boolean isDirty() {
		return dirty;
	}

	public void clean() {
		dirty = false;
	}

	public FacadeMatrix clone() {
		FacadeMatrix result = new FacadeMatrix();
		result.facadeId = facadeId.clone();
		result.facadeMeta = facadeMeta.clone();
		result.dirty = dirty;
		return result;
	}

	public void readData(DataInputStream data) throws IOException {
		for (int i = 0; i < 6; i++) {
			facadeId[i] = data.readShort();
			facadeMeta[i] = data.readByte();
		}
	}

	public void writeData(DataOutputStream data) throws IOException {
		for (int i = 0; i < 6; i++) {
			data.writeShort(facadeId[i]);
			data.writeByte(facadeMeta[i]);
		}
	}

	public void writeToNBT(NBTTagCompound comp) {
		for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++) {
			comp.setInteger("facadeBlocks[" + i + "]", facadeId[i]);
			comp.setInteger("facadeMeta[" + i + "]", facadeMeta[i]);
		}
	}

	public void readFromNBT(NBTTagCompound comp) {
		for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++) {
			facadeId[i] = comp.getInteger("facadeBlocks[" + i + "]");
			facadeMeta[i] = comp.getInteger("facadeMeta[" + i + "]");
		}
	}
}

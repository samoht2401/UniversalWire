package samoht2401.universalwire.render;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;

import samoht2401.universalwire.network.ISerializable;
import samoht2401.universalwire.system.BufferManager;
import samoht2401.universalwire.system.System;

public class RenderInfoSystem implements ISerializable {

	public static ArrayList<RenderInfoSystem> infos;

	public static RenderInfoSystem get(int id) {
		if (infos == null)
			infos = new ArrayList<RenderInfoSystem>();
		for (RenderInfoSystem ris : infos)
			if (ris.id == id)
				return ris;
		return null;
	}

	public int id;
	public BufferManager buffer;
	public boolean isAlreadySaved;

	public RenderInfoSystem() {
		if (infos == null)
			infos = new ArrayList<RenderInfoSystem>();
		buffer = new BufferManager();
		isAlreadySaved = false;
	}

	public RenderInfoSystem(System s, BufferManager buffer) {
		id = s.id;
		this.buffer = buffer;
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		data.writeInt(id);
		buffer.writeData(data);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		id = data.readInt();
		buffer = new BufferManager();
		buffer.readData(data);
	}

	public void writeToNBT(NBTTagCompound comp) {
		comp.setInteger("id", id);
		NBTTagCompound tag = new NBTTagCompound();
		buffer.writeToNBT(tag);
		comp.setCompoundTag("buffer", tag);
	}

	public void readFromNBT(NBTTagCompound comp) {
		id = comp.getInteger("id");
		buffer = new BufferManager();
		buffer.readFromNBT(comp.getCompoundTag("buffer"));
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof RenderInfoSystem)
			return ((RenderInfoSystem) o).id == id;
		return false;
	}
}

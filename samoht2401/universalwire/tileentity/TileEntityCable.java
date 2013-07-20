package samoht2401.universalwire.tileentity;

import java.util.ArrayList;

import samoht2401.universalwire.UniversalWire;
import samoht2401.universalwire.render.RenderInfoCable;
import samoht2401.universalwire.render.RenderInfoSystem;
import samoht2401.universalwire.system.BufferManager;
import samoht2401.universalwire.system.IEnergyBuffer;
import samoht2401.universalwire.system.ItemType;
import samoht2401.universalwire.system.System;

import buildcraft.api.power.IPowerProvider;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerFramework;
import buildcraft.core.network.IClientState;
import buildcraft.core.network.ISyncedTile;
import buildcraft.core.network.PacketTileState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityCable extends TileEntity implements ISyncedTile, IEnergyBuffer, IPowerReceptor {

	private RenderInfoCable renderInfo;
	private RenderInfoSystem renderInfoSystem;
	private int currentTexIndex;
	private boolean hasBeenAddedToSystem;
	private BufferManager buffer;
	private float oldColorRatio;

	public TileEntityCable() {
		super();
		renderInfo = UniversalWire.blockCable.genericRenderInfo.clone();
		renderInfoSystem = null;
		currentTexIndex = 0;
		hasBeenAddedToSystem = false;
		oldColorRatio = 0f;
		provider = PowerFramework.currentFramework.createPowerProvider();
		provider.configure(0, 1, 500, 1, 500);
	}

	public void setCurrentTexture(int index) {
		currentTexIndex = index;
	}

	public Icon getCurrentTexture() {
		return renderInfo.textures[currentTexIndex];
	}

	public void updatePowerDispo(World w, int x, int y, int z, ForgeDirection dir) {
		int x1, y1, z1;
		x1 = x + dir.offsetX;
		y1 = y + dir.offsetY;
		z1 = z + dir.offsetZ;
		TileEntity tile = w.getBlockTileEntity(x1, y1, z1);
		if (tile instanceof TileEntityCable) {
			TileEntityCable cable = (TileEntityCable) tile;
			cable.getPowerTotalDispoTo(dir.OPPOSITES);
		}
	}

	private void getPowerTotalDispoTo(int[] opposites) {

	}

	public float getColorRatio() {
		RenderInfoSystem ris = RenderInfoSystem.get(renderInfo.systemId);
		if (ris != null)
			return oldColorRatio = ris.buffer.getFillRatio(this);
		return 0f;
	}

	public int getSystemId() {
		return renderInfo.systemId;
	}

	public void setSystemId(int val) {
		renderInfo.systemId = val;
	}

	public ArrayList<ForgeDirection> getConnections() {
		return renderInfo.connections;
	}

	public void onAdded() {
		if (worldObj instanceof WorldClient)
			return;
		UniversalWire.systemManager.addItem(worldObj, xCoord, yCoord, zCoord, this);
		checkConnections();
	}

	public void onNeighborBlockChange(World world, int x, int y, int z, int id) {
		if (worldObj instanceof WorldClient)
			return;
		UniversalWire.systemManager.onNeighbotBlockChange(world, x, y, z, id);
		checkConnections();
	}

	@Override
	public void updateEntity() {
		if (renderInfoSystem != null)
			if (UniversalWire.systemManager.setRenderInfoSystem(this, renderInfoSystem))
				renderInfoSystem = null;
		RenderInfoSystem ris = RenderInfoSystem.get(renderInfo.systemId);
		// updateRender();
		if (ris != null && oldColorRatio != ris.buffer.getFillRatio(this))
			updateRender();
		if (worldObj instanceof WorldClient || !checkValidity())
			return;
		if (!hasBeenAddedToSystem) {
			UniversalWire.systemManager.addItem(worldObj, xCoord, yCoord, zCoord, this);
			hasBeenAddedToSystem = true;
		}
		provider.update(this);
	}

	public void checkConnections() {
		if (worldObj instanceof WorldClient || !checkValidity())
			return;
		renderInfo.connections = UniversalWire.systemManager.getConnections(worldObj, xCoord, yCoord, zCoord);
		// renderInfo.systemId =
		// UniversalWire.systemManager.getSystemId(worldObj, xCoord, yCoord,
		// zCoord);
		updateData();
	}

	public boolean checkValidity() {
		if (worldObj == null || UniversalWire.blockCable == null)
			return true;
		if (this.tileEntityInvalid || worldObj.getBlockId(xCoord, yCoord, zCoord) != UniversalWire.blockCable.blockID) {
			invalidate();
			return false;
		}
		return true;
	}

	@Override
	public Packet getDescriptionPacket() {
		PacketTileState packet = new PacketTileState(this.xCoord, this.yCoord, this.zCoord);
		packet.addStateForSerialization((byte) 0, renderInfo);
		return packet.getPacket();
	}

	@Override
	public void writeToNBT(NBTTagCompound comp) {
		renderInfo.connections = UniversalWire.systemManager.getConnections(worldObj, xCoord, yCoord, zCoord);

		super.writeToNBT(comp);
		int[] connect = new int[renderInfo.connections.size()];
		for (int i = 0; i < connect.length; i++)
			connect[i] = renderInfo.connections.get(i).ordinal();
		comp.setIntArray("Connections", connect);
		provider.writeToNBT(comp);

		RenderInfoSystem ris = RenderInfoSystem.get(UniversalWire.systemManager.getSystemId(this));
		if (ris != null) {
			NBTTagCompound tag = new NBTTagCompound();
			ris.writeToNBT(tag);
			comp.setCompoundTag("renderInfoSystem", tag);
			ris.isAlreadySaved = true;
		} else if (comp.hasKey("renderInfoSystem"))
			comp.removeTag("renderInfoSystem");
	}

	@Override
	public void readFromNBT(NBTTagCompound comp) {
		super.readFromNBT(comp);

		renderInfo.connections = new ArrayList<ForgeDirection>();
		int[] connect = comp.getIntArray("Connections");
		for (int i = 0; i < connect.length; i++)
			renderInfo.connections.add(ForgeDirection.getOrientation(connect[i]));
		provider.readFromNBT(comp);

		if (comp.hasKey("renderInfoSystem")) {
			NBTTagCompound tag = comp.getCompoundTag("renderInfoSystem");
			renderInfoSystem = new RenderInfoSystem();
			renderInfoSystem.readFromNBT(tag);
		}
	}

	@Override
	public IClientState getStateInstance(byte stateId) {
		switch (stateId) {
		case 0:
			return renderInfo;
		}
		throw new RuntimeException("Unknown state requested: " + stateId + " this is a bug!");
	}

	@Override
	public void afterStateUpdated(byte stateId) {
		updateRender();
	}

	@Override
	public int getBufferSize() {
		return 32 * System.RATIO_EU_ENERGY;
	}

	public void updateData() {
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public void updateRender() {
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}

	// Just to be able to get power from buildcraft
	IPowerProvider provider;

	@Override
	public void setPowerProvider(IPowerProvider provider) {
		this.provider = provider;
		provider.configure(0, 1, 500, 1, 500);
	}

	@Override
	public IPowerProvider getPowerProvider() {
		return provider;
	}

	@Override
	public void doWork() {
		int surplus = UniversalWire.systemManager.sourceEvent(worldObj, this, (int) provider.getEnergyStored());
		int conso = (int) provider.getEnergyStored() - surplus;
		provider.useEnergy(conso, conso, true);
	}

	@Override
	public int powerRequest(ForgeDirection from) {
		return 500;
	}
}
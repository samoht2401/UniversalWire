package samoht2401.universalwire.tileentity;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import samoht2401.universalwire.UniversalWire;
import samoht2401.universalwire.network.ISerializable;
import samoht2401.universalwire.network.ISynchronisable;
import samoht2401.universalwire.network.PacketIDs;
import samoht2401.universalwire.network.PacketSerializableInfo;
import samoht2401.universalwire.render.RenderInfoCable;
import samoht2401.universalwire.render.RenderInfoSystem;
import samoht2401.universalwire.system.BufferManager;
import samoht2401.universalwire.system.IEnergyBuffer;
import samoht2401.universalwire.system.System;
import samoht2401.universalwire.util.FacadeMatrix;
import samoht2401.universalwire.util.ItemUtil;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import buildcraft.transport.ItemFacade;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityCable extends TileEntity implements ISynchronisable, IEnergyBuffer, IPowerReceptor {

	private RenderInfoCable renderInfo;
	private RenderInfoSystem renderInfoSystem;
	private int currentTexIndex;
	private boolean hasBeenAddedToSystem;
	private BufferManager buffer;
	private float oldColorRatio;
	private boolean firstUpdate;

	public TileEntityCable() {
		super();
		renderInfo = UniversalWire.blockCable.genericRenderInfo.clone();
		renderInfoSystem = null;
		currentTexIndex = 0;
		hasBeenAddedToSystem = false;
		oldColorRatio = 0f;
		firstUpdate = true;
		powerHandler = new PowerHandler(this, Type.PIPE);
		powerHandler.configure(0, 1000, 1, 1000);
		receiver = powerHandler.getPowerReceiver();
	}

	public void setCurrentTexture(int index) {
		currentTexIndex = index;
	}

	@SideOnly(Side.CLIENT)
	public Icon getCurrentTexture() {
		if (renderInfo.textures != null)
			return renderInfo.textures[currentTexIndex];
		return null;
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
		if (worldObj.isRemote)
			return;
		UniversalWire.systemManager.addItem(worldObj, xCoord, yCoord, zCoord, this);
		renderInfo.x = xCoord;
		renderInfo.y = yCoord;
		renderInfo.z = zCoord;
		checkConnections();
	}

	public void onNeighborBlockChange(World world, int x, int y, int z, int id) {
		if (worldObj.isRemote)
			return;
		UniversalWire.systemManager.onNeighbotBlockChange(world, x, y, z, id);
		checkConnections();
	}

	@Override
	public void updateEntity() {
		if (firstUpdate) {
			renderInfo.x = xCoord;
			renderInfo.y = yCoord;
			renderInfo.z = zCoord;
		}
		if (renderInfoSystem != null)
			if (UniversalWire.systemManager.setRenderInfoSystem(this, renderInfoSystem))
				renderInfoSystem = null;
		RenderInfoSystem ris = RenderInfoSystem.get(renderInfo.systemId);
		// updateRender();
		if (ris != null && oldColorRatio != ris.buffer.getFillRatio(this))
			updateRender();
		if (worldObj.isRemote || !checkValidity())
			return;
		if (!hasBeenAddedToSystem) {
			UniversalWire.systemManager.addItem(worldObj, xCoord, yCoord, zCoord, this);
			hasBeenAddedToSystem = true;
		}
		// provider.update(this);
	}

	public void checkConnections() {
		if (worldObj.isRemote || !checkValidity())
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
		PacketSerializableInfo packet = new PacketSerializableInfo(PacketIDs.CABLE_UPDATE);
		packet.info = renderInfo;
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
		renderInfo.facades.writeToNBT(comp);
		// provider.writeToNBT(comp);

		RenderInfoSystem ris = RenderInfoSystem.get(UniversalWire.systemManager.getSystemId(this));
		if (ris != null) {
			NBTTagCompound tag = new NBTTagCompound();
			ris.writeToNBT(tag);
			comp.setCompoundTag("renderInfoSystem", tag);
			ris.isAlreadySaved = true;
		}
		else if (comp.hasKey("renderInfoSystem"))
			comp.removeTag("renderInfoSystem");
	}

	@Override
	public void readFromNBT(NBTTagCompound comp) {
		super.readFromNBT(comp);

		renderInfo.connections = new ArrayList<ForgeDirection>();
		int[] connect = comp.getIntArray("Connections");
		for (int i = 0; i < connect.length; i++)
			renderInfo.connections.add(ForgeDirection.getOrientation(connect[i]));
		renderInfo.facades.readFromNBT(comp);
		// provider.readFromNBT(comp);

		if (comp.hasKey("renderInfoSystem")) {
			NBTTagCompound tag = comp.getCompoundTag("renderInfoSystem");
			renderInfoSystem = new RenderInfoSystem();
			renderInfoSystem.readFromNBT(tag);
		}
	}

	@Override
	public void updateSynchronisedInfo(ISerializable info) {
		if (info instanceof RenderInfoCable) {
			((RenderInfoCable) info).textures = renderInfo.textures;
			renderInfo = (RenderInfoCable) info;
			updateRender();
		}
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
	PowerReceiver receiver;
	PowerHandler powerHandler;

	@Override
	public void doWork(PowerHandler handler) {
		int surplus = UniversalWire.systemManager.sourceEvent(worldObj, this, (int) handler.getEnergyStored());
		int conso = (int) handler.getEnergyStored() - surplus;
		handler.useEnergy(conso, conso, true);
	}

	@Override
	public PowerReceiver getPowerReceiver(ForgeDirection arg0) {
		return receiver;
	}

	@Override
	public World getWorld() {
		return worldObj;
	}

	public boolean hasFacade(ForgeDirection dir) {
		return renderInfo.facades.hasFacade(dir);
	}

	public FacadeMatrix getFacadeMatrix() {
		return renderInfo.facades;
	}

	public boolean addFacade(ForgeDirection dir, int blockid, int meta) {
		if (this.worldObj.isRemote)
			return false;
		if (renderInfo.facades.getFacadeId(dir) == blockid)
			return false;

		if (hasFacade(dir))
			ItemUtil.dropItems(worldObj,
					ItemFacade.getStack(renderInfo.facades.getFacadeId(dir), renderInfo.facades.getFacadeMeta(dir)),
					this.xCoord, this.yCoord, this.zCoord);

		renderInfo.facades.setFacade(dir, blockid, meta);
		worldObj.notifyBlockChange(this.xCoord, this.yCoord, this.zCoord, UniversalWire.blockCable.blockID);
		updateData();
		return true;
	}

	public void dropFacade(ForgeDirection dir) {
		if (this.worldObj.isRemote)
			return;
		if (!hasFacade(dir))
			return;
		ItemUtil.dropItems(worldObj,
				ItemFacade.getStack(renderInfo.facades.getFacadeId(dir), renderInfo.facades.getFacadeMeta(dir)),
				this.xCoord, this.yCoord, this.zCoord);
		renderInfo.facades.setFacade(dir, 0, 0);
		worldObj.notifyBlockChange(this.xCoord, this.yCoord, this.zCoord, UniversalWire.blockCable.blockID);
		updateData();
	}

	public boolean isConnected(ForgeDirection dir) {
		if (renderInfo.connections == null)
			return false;
		return renderInfo.connections.contains(dir);
	}

}
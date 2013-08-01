package samoht2401.universalwire.system;

import ic2.api.Direction;
import ic2.api.energy.tile.IEnergySink;
import java.util.ArrayList;
import java.util.HashMap;

import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;

import samoht2401.universalwire.UniversalWire;
import samoht2401.universalwire.network.PacketIDs;
import samoht2401.universalwire.network.PacketSerializableInfo;
import samoht2401.universalwire.render.RenderInfoSystem;
import samoht2401.universalwire.tileentity.TileEntityCable;
import samoht2401.universalwire.util.Coordinate;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeDirection;

public class System {

	public class EqualableChunk {

		public Chunk instance;

		public EqualableChunk(Chunk c) {
			instance = c;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof EqualableChunk) {
				EqualableChunk other = (EqualableChunk) o;
				return instance.worldObj == other.instance.worldObj && instance.xPosition == other.instance.xPosition
						&& instance.zPosition == other.instance.zPosition;
			}
			return false;
		}
	}

	public int id;
	public RenderInfoSystem renderInfo;
	private World world;
	private HashMap<Coordinate, TileEntity> items;
	private ArrayList<IEnergySink> euSinks;
	private ArrayList<IPowerReceptor> mjSinks;
	private ArrayList<EqualableChunk> bound;
	private BufferManager buffer;
	boolean isValidSystem;

	// public final static int MAX_ENERGY_BUFFER_SIZE = 1024;
	public final static int RATIO_EU_ENERGY = 2;
	public final static int RATIO_MJ_ENERGY = 5;

	public System(int id, World w, System... from) {
		this.id = id;
		world = w;
		items = new HashMap<Coordinate, TileEntity>();
		euSinks = new ArrayList<IEnergySink>();
		mjSinks = new ArrayList<IPowerReceptor>();
		bound = new ArrayList<EqualableChunk>();
		buffer = new BufferManager();
		renderInfo = new RenderInfoSystem(this, buffer);
		isValidSystem = false;

		if (from != null && from.length > 0) {
			for (System s : from) {
				addItems(s.items);
				buffer.pushToBuffer(s.buffer.getBuffer());
			}
		}
		if (!isValidSystem)
			buffer.emptyBuffer();

		sendUpdate();
	}

	public World getWorld() {
		return world;
	}

	public HashMap<Coordinate, TileEntity> getItems() {
		return items;
	}

	public TileEntity getItem(Coordinate coord) {
		return items.get(coord);
	}

	public ArrayList<EqualableChunk> getBound() {
		return bound;
	}

	public void addItem(Coordinate coord, TileEntity type) {
		items.put(coord, type);
		addChunkToBound(new EqualableChunk(world.getChunkFromBlockCoords((int) coord.x, (int) coord.z)));
		if ((int) (coord.x) % 16 == 0)
			addChunkToBound(new EqualableChunk(world.getChunkFromBlockCoords((int) coord.x - 1, (int) coord.z)));
		if ((int) (coord.x + 1) % 16 == 0)
			addChunkToBound(new EqualableChunk(world.getChunkFromBlockCoords((int) coord.x + 1, (int) coord.z)));
		if ((int) (coord.z) % 16 == 0)
			addChunkToBound(new EqualableChunk(world.getChunkFromBlockCoords((int) coord.x, (int) coord.z - 1)));
		if ((int) (coord.z + 1) % 16 == 0)
			addChunkToBound(new EqualableChunk(world.getChunkFromBlockCoords((int) coord.x, (int) coord.z + 1)));
		if (type instanceof IEnergyBuffer)
			buffer.addBuffer((IEnergyBuffer) type);
		if (type instanceof TileEntityCable) {
			isValidSystem = true;
			((TileEntityCable) type).setSystemId(id);
			((TileEntityCable) type).updateData();
		}
		if (type instanceof IEnergySink)
			euSinks.add((IEnergySink) type);
		if (type instanceof IPowerReceptor && !(type instanceof TileEntityCable))
			mjSinks.add((IPowerReceptor) type);
		sendUpdate();
	}

	public void addItems(HashMap<Coordinate, TileEntity> list) {
		for (Coordinate coord : list.keySet()) {
			addItem(coord, list.get(coord));
		}
	}

	private void addChunkToBound(EqualableChunk c) {
		if (!bound.contains(c))
			bound.add(c);
	}

	public boolean isInBound(Coordinate coord) {
		return bound.contains(new EqualableChunk(world.getChunkFromBlockCoords((int) coord.x, (int) coord.z)));
	}

	public boolean isTouching(Coordinate coord) {
		if (!isInBound(coord))
			return false;
		for (Coordinate c : items.keySet()) {
			if (c.isTouching(coord))
				return true;
		}
		return false;
	}

	public boolean isContaining(Coordinate coord) {
		if (!isInBound(coord))
			return false;
		for (Coordinate c : items.keySet()) {
			if (c.equals(coord))
				return true;
		}
		return false;
	}

	public boolean isContaining(TileEntity tileEntity) {
		if (!isInBound(new Coordinate(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord)))
			return false;
		for (TileEntity e : items.values()) {
			if (e.equals(tileEntity))
				return true;
		}
		return false;
	}

	public ArrayList<System> removeItem(Coordinate coord) {
		ArrayList<System> result = new ArrayList<System>();
		int nbValidSystem = 0;
		items.remove(coord);
		for (int i = 0; i < 6; i++) {
			Coordinate c = coord.getTouching(ForgeDirection.getOrientation(i));
			if (items.containsKey(c)) {
				System ns = rebuildSystem(this, new System(UniversalWire.systemManager.getNextId(), world), c);
				if (ns.isValidSystem)
					nbValidSystem++;
				result.add(ns);
			}
		}
		for (System s : result)
			if (s.isValidSystem) {
				s.buffer.pushToBuffer(buffer.getBuffer() / nbValidSystem);
				s.sendUpdate();
			}
		return result;
	}

	public static System rebuildSystem(System startingSystem, System resultSystem, Coordinate startPoint) {
		if (!startingSystem.isContaining(startPoint))
			return resultSystem;
		// Add to the new system and remove from the old one to prevent the
		// infinite recursion
		resultSystem.addItem(startPoint, startingSystem.getItem(startPoint));
		startingSystem.items.remove(startPoint);
		// Look for all touching possible cube
		for (int i = 0; i < 6; i++) {
			rebuildSystem(startingSystem, resultSystem, startPoint.getTouching(ForgeDirection.getOrientation(i)));
		}
		return resultSystem;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof System)
			return equals((System) other);
		return false;
	}

	public boolean equals(System other) {
		return world == other.world && items.equals(other.items);
	}

	public int pushEnergy(int amount, int ratio) {
		int result;
		int oldBuffer = buffer.getBuffer();
		if (!isValidSystem)
			result = amount;
		else
			result = buffer.pushToBuffer(amount * ratio) / ratio;
		if (oldBuffer != buffer.getBuffer())
			sendUpdate();
		return result;
	}

	public void updateEnergy() {
		if (!isValidSystem)
			return;
		int neededEnergy = 0;
		int oldBuffer = buffer.getBuffer();
		HashMap<IEnergySink, Integer> demandsEu = new HashMap<IEnergySink, Integer>();
		HashMap<PowerReceiver, Integer> demandsMj = new HashMap<PowerReceiver, Integer>();
		for (IEnergySink sink : euSinks) {
			int demand = sink.demandsEnergy();
			if (demand > sink.getMaxSafeInput())
				demand = sink.getMaxSafeInput();
			demand *= RATIO_EU_ENERGY;
			if (demand > 0) {
				neededEnergy += demand;
				demandsEu.put(sink, demand);
			}
		}
		for (IPowerReceptor sink : mjSinks) {
			if(sink instanceof IPowerEmitter)
				continue;
			PowerReceiver rec = sink.getPowerReceiver(ForgeDirection.DOWN);
			if(rec == null)
				continue;
			int demand = (int) rec.powerRequest();
			if (demand > rec.getMaxEnergyReceived())
				demand = (int) rec.getMaxEnergyReceived();
			if (demand + rec.getEnergyStored() > rec.getMaxEnergyStored())
				demand = (int) (rec.getMaxEnergyStored() - rec.getEnergyStored());
			demand *= RATIO_MJ_ENERGY;
			if (demand > 0) {
				neededEnergy += demand;
				demandsMj.put(rec, demand);
			}
		}
		if (buffer.getBuffer() >= neededEnergy) {
			for (IEnergySink sink : demandsEu.keySet())
				sink.injectEnergy(Direction.YP, buffer.pullFromBuffer(demandsEu.get(sink)) / RATIO_EU_ENERGY);
			for (PowerReceiver sink : demandsMj.keySet())
				sink.receiveEnergy(PowerHandler.Type.STORAGE, buffer.pullFromBuffer(demandsMj.get(sink))
						/ RATIO_EU_ENERGY, ForgeDirection.DOWN);
		}
		else {
			double ratio = neededEnergy / (buffer.getBuffer() * 1D);
			if (ratio < 1)
				return;
			for (IEnergySink sink : demandsEu.keySet())
				sink.injectEnergy(Direction.YP,
						buffer.pullFromBuffer((int) Math.floor(demandsEu.get(sink) / ratio / RATIO_EU_ENERGY)));
			for (PowerReceiver sink : demandsMj.keySet())
				sink.receiveEnergy(PowerHandler.Type.STORAGE,
						buffer.pullFromBuffer((int) Math.floor(demandsMj.get(sink) / ratio / RATIO_MJ_ENERGY)),
						ForgeDirection.DOWN);
		}
		if (oldBuffer != buffer.getBuffer())
			sendUpdate();
	}

	private void sendUpdate() {
		PacketSerializableInfo p = new PacketSerializableInfo(PacketIDs.SYSTEM_UPDATE, renderInfo);
		UniversalWire.proxy.sendToPlayers(p.getPacket(), world, 0, 0, 0, Integer.MAX_VALUE);
	}

	public void setRenderInfo(RenderInfoSystem info) {
		renderInfo.id = id;
		renderInfo.buffer.replaceBy(info.buffer);
		sendUpdate();
	}

	public void playerJoin(EntityPlayer player) {
		PacketSerializableInfo p = new PacketSerializableInfo(PacketIDs.SYSTEM_UPDATE, renderInfo);
		UniversalWire.proxy.sendToPlayer(player, p.getPacket());
	}
}

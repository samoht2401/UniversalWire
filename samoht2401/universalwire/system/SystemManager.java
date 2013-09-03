package samoht2401.universalwire.system;

import ic2.api.energy.tile.IEnergyConductor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergyTile;
import ic2.core.block.wiring.TileEntityElectricBlock;
import ic2.core.block.wiring.TileEntityTransformer;

import java.util.ArrayList;
import buildcraft.api.power.IPowerReceptor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.src.ModLoader;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

import samoht2401.universalwire.render.RenderInfoSystem;
import samoht2401.universalwire.tileentity.TileEntityCable;
import samoht2401.universalwire.util.Coordinate;

public class SystemManager {

	private int nextId;
	private ArrayList<System> systems;

	public SystemManager() {
		systems = new ArrayList<System>();
		nextId = 0;
	}

	public int getNextId() {
		return nextId++;
	}

	public ArrayList<ForgeDirection> getCableConnections(World w, Coordinate coord) {
		ArrayList<ForgeDirection> result = new ArrayList<ForgeDirection>();
		if (w.isRemote)
			return result;
		System s = getSystem(w, coord);
		if (s == null)
			return result;
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.getOrientation(i);
			if (s.isContaining(coord.getTouching(dir)) && s.getItem(coord.getTouching(dir)) instanceof TileEntityCable)
				result.add(dir);
		}
		return result;
	}

	public ArrayList<ForgeDirection> getConnections(World w, Coordinate coord) {
		ArrayList<ForgeDirection> result = new ArrayList<ForgeDirection>();
		if (w.isRemote)
			return result;
		System s = getSystem(w, coord);
		if (s == null)
			return result;
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.getOrientation(i);
			if (s.isContaining(coord.getTouching(dir)))
				result.add(dir);
		}
		return result;
	}

	public ArrayList<ForgeDirection> getConnections(World w, int x, int y, int z) {
		return getConnections(w, new Coordinate(x, y, z));
	}

	public ArrayList<ForgeDirection> getConnections(World w, TileEntity tileEntity) {
		return getConnections(w, new Coordinate(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord));
	}

	public int getSystemId(World w, Coordinate coord) {
		System s = getSystem(w, coord);
		if (s != null)
			return s.id;
		return -1;
	}

	public int getSystemId(World w, int x, int y, int z) {
		return getSystemId(w, new Coordinate(x, y, z));
	}

	public int getSystemId(TileEntity tileEntity) {
		return getSystemId(tileEntity.worldObj, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
	}

	private System getSystem(World w, Coordinate coord) {
		if (w.isRemote)
			return null;
		for (System system : systems) {
			if (system.getWorld() != w)
				continue;
			if (system.isContaining(coord)) {
				return system;
			}
		}
		return null;
	}

	private System getSystem(int systemId) {
		for (System s : systems)
			if (s.id == systemId)
				return s;
		return null;
	}

	private System getSystem(World w, TileEntity tileEntity) {
		if (w.isRemote)
			return null;
		for (System system : systems) {
			if (system.getWorld() != w)
				continue;
			if (system.isContaining(tileEntity)) {
				return system;
			}
		}
		return null;
	}

	private boolean isAnyContaining(World w, Coordinate coord) {
		return getSystem(w, coord) != null;
	}

	public void addItem(World w, Coordinate coord, TileEntity tileEntity) {
		if (w.isRemote)
			return;
		if (ModLoader.isModLoaded("IC2")
				&& (tileEntity instanceof TileEntityTransformer || tileEntity instanceof IEnergyConductor))
			return;
		if (isAnyContaining(w, coord))
			return;
		ArrayList<System> systemTouching = new ArrayList<System>();
		for (System system : systems) {
			if (system.getWorld() != w)
				continue;
			if (system.isTouching(coord))
				systemTouching.add(system);
		}
		if (systemTouching.size() == 0) // Create a new system
		{
			System ns = new System(getNextId(), w);
			ns.addItem(coord, tileEntity);
			systems.add(ns);
		}
		else if (systemTouching.size() == 1) {
			TileEntity te = systemTouching.get(0).touchingBy(coord);
			if (!ModLoader.isModLoaded("IC2") || !(te instanceof TileEntityElectricBlock))
				systemTouching.get(0).addItem(coord, tileEntity);
			else {
				System ns = new System(getNextId(), w);
				ns.addItem(new Coordinate(te.xCoord, te.yCoord, te.zCoord), te);
				ns.addItem(coord, tileEntity);
				systems.add(ns);
			}

		}
		else if (systemTouching.size() > 1) {
			ArrayList<System> systemToMerge = new ArrayList<System>();
			for (System s : systemTouching) {
				TileEntity te = s.touchingBy(coord);
				if (ModLoader.isModLoaded("IC2") && tileEntity instanceof TileEntityElectricBlock)
					s.addItem(coord, tileEntity);
				else if (!ModLoader.isModLoaded("IC2") || !(te instanceof TileEntityElectricBlock))
					systemToMerge.add(s);
				else {
					System ns = new System(getNextId(), w);
					ns.addItem(new Coordinate(te.xCoord, te.yCoord, te.zCoord), te);
					systemToMerge.add(ns);
				}

			}
			System rs = new System(getNextId(), w, systemToMerge.toArray(new System[systemToMerge.size()]));
			rs.addItem(coord, tileEntity);
			systems.add(rs);
			for (System s : systemToMerge)
				systems.remove(s);
		}
		checkForAdjacentBlock(w, coord);
	}

	public void addItem(World w, int x, int y, int z, TileEntity tileEntity) {
		addItem(w, new Coordinate(x, y, z), tileEntity);
	}

	public void addItem(World w, TileEntity tileEntity) {
		addItem(w, new Coordinate(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord), tileEntity);
	}

	private boolean isForbiddenIntraLink(System systemTouching, Coordinate coord, TileEntity tileEntity) {
		if (tileEntity instanceof TileEntityElectricBlock)
			return true;
		if (systemTouching.getItem(coord.getTouching(ForgeDirection.DOWN)) instanceof TileEntityElectricBlock)
			return true;
		if (systemTouching.getItem(coord.getTouching(ForgeDirection.UP)) instanceof TileEntityElectricBlock)
			return true;
		if (systemTouching.getItem(coord.getTouching(ForgeDirection.WEST)) instanceof TileEntityElectricBlock)
			return true;
		if (systemTouching.getItem(coord.getTouching(ForgeDirection.EAST)) instanceof TileEntityElectricBlock)
			return true;
		if (systemTouching.getItem(coord.getTouching(ForgeDirection.NORTH)) instanceof TileEntityElectricBlock)
			return true;
		if (systemTouching.getItem(coord.getTouching(ForgeDirection.SOUTH)) instanceof TileEntityElectricBlock)
			return true;
		return false;
	}

	private void checkForAdjacentBlock(World w, Coordinate coord) {
		if (w.isRemote)
			return;
		for (int i = 0; i < 6; i++) {
			Coordinate c = coord.getTouching(ForgeDirection.getOrientation(i));
			TileEntity te = w.getBlockTileEntity((int) c.x, (int) c.y, (int) c.z);
			if (te == null)
				continue;
			if (te instanceof IEnergyEmitter)
				addItem(w, c, te);
			if (te instanceof IEnergySink)
				addItem(w, c, te);
			if (te instanceof IPowerReceptor)
				addItem(w, c, te);
		}
	}

	private void checkForFantomAdjacentBlock(World w, Coordinate coord) {
		if (w.isRemote)
			return;
		for (int i = 0; i < 6; i++) {
			Coordinate c = coord.getTouching(ForgeDirection.getOrientation(i));
			System s = getSystem(w, c);
			if (s == null)
				continue;
			TileEntity te = w.getBlockTileEntity((int) c.x, (int) c.y, (int) c.z);
			if (te == null
					|| (!(te instanceof TileEntityCable) && !(te instanceof IEnergyEmitter)
							&& !(te instanceof IEnergySink) && !(te instanceof IPowerReceptor)))
				removeItem(w, c);
			else if (!(te instanceof TileEntityCable) && !(te instanceof IEnergyEmitter)
					&& !(te instanceof IEnergySink)) {
				int nbCo = getCableConnections(w, c).size();
				if (nbCo == 0)
					removeItem(w, c);
			}
		}
	}

	public void removeItem(World w, Coordinate coord) {
		if (w.isRemote)
			return;
		System s = getSystem(w, coord);
		if (s == null)
			return;
		systems.remove(s);
		ArrayList<System> resultList = s.removeItem(coord);
		systems.addAll(resultList);

		for (System sy : resultList)
			checkForFantomAdjacentBlock(w, coord);
	}

	public void removeItem(World w, int x, int y, int z) {
		removeItem(w, new Coordinate(x, y, z));
	}

	public void removeItem(World w, TileEntity tileEntity) {
		removeItem(w, new Coordinate(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord));
	}

	public void onNeighbotBlockChange(World w, int x, int y, int z, int id) {
		Coordinate coord = new Coordinate(x, y, z);
		checkForAdjacentBlock(w, coord);
		checkForFantomAdjacentBlock(w, coord);
	}

	public void unloadWorld(World world) {
		if (world.isRemote)
			return;
		ArrayList<System> toRemove = new ArrayList<System>();
		for (System s : systems)
			if (s.getWorld() == world)
				toRemove.add(s);
		systems.removeAll(toRemove);
	}

	public int sourceEvent(World world, TileEntity energyTile, int amount) {
		System s = getSystem(world, energyTile);
		if (s != null && energyTile instanceof IEnergyTile)
			return s.pushEnergy(amount, System.RATIO_EU_ENERGY);
		if (s != null && energyTile instanceof IPowerReceptor)
			return s.pushEnergy(amount, System.RATIO_MJ_ENERGY);
		return amount;
	}

	public void update() {
		for (int i = 0; i < systems.size(); i++) {
			systems.get(i).updateEnergy();
		}
	}

	public boolean setRenderInfoSystem(TileEntityCable tileEntity, RenderInfoSystem info) {
		System s = getSystem(tileEntity.worldObj, tileEntity);
		if (s != null) {
			s.setRenderInfo(info);
			return true;
		}
		return false;
	}

	public void playerJoin(World w, EntityPlayer player) {
		for (System s : systems)
			if (s.getWorld() == w)
				s.playerJoin(player);
	}
}

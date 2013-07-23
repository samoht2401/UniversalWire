package samoht2401.universalwire.tileentity;

import java.util.ArrayList;

import samoht2401.universalwire.UniversalWire;
import samoht2401.universalwire.blocks.BlockTank;
import samoht2401.universalwire.network.ISerializable;
import samoht2401.universalwire.network.ISynchronisable;
import samoht2401.universalwire.network.PacketIDs;
import samoht2401.universalwire.network.PacketSerializableInfo;
import samoht2401.universalwire.render.RenderInfoCable;
import samoht2401.universalwire.render.RenderInfoTank;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityTank extends TileEntity implements IFluidHandler, ISynchronisable {

	private FluidTank tank;

	public TileEntityTank() {
		tank = new FluidTank(BlockTank.TANK_CAPACITY);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagCompound tankTag = new NBTTagCompound();
		tank.writeToNBT(tankTag);
		tag.setCompoundTag("tank", tankTag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey("tank")) {
			tank.readFromNBT(tag.getCompoundTag("tank"));
		}
	}

	@Override
	public void updateEntity() {
		if (worldObj.isRemote) {
			return;
		}

		// Have liquid flow down into tanks below if any.
		if (tank.getFluid() != null) {
			moveFluidBelow();
		}
		// Have liquid flow into adjacent tanks if any.
		if (tank.getFluid() != null) {
			moveFluidToAdjacent();
		}
	}

	public TileEntityTank getBottomTank() {

		TileEntityTank lastTank = this;

		while (true) {
			TileEntityTank below = getTankBelow(lastTank);
			if (below != null) {
				lastTank = below;
			}
			else {
				break;
			}
		}

		return lastTank;
	}

	public TileEntityTank getTopTank() {

		TileEntityTank lastTank = this;

		while (true) {
			TileEntityTank above = getTankAbove(lastTank);
			if (above != null) {
				lastTank = above;
			}
			else {
				break;
			}
		}

		return lastTank;
	}

	public static TileEntityTank getTankBelow(TileEntityTank tile) {
		TileEntity below = tile.worldObj.getBlockTileEntity(tile.xCoord, tile.yCoord - 1, tile.zCoord);
		if (below instanceof TileEntityTank) {
			return (TileEntityTank) below;
		}
		else {
			return null;
		}
	}

	public static TileEntityTank getTankAbove(TileEntityTank tile) {
		TileEntity above = tile.worldObj.getBlockTileEntity(tile.xCoord, tile.yCoord + 1, tile.zCoord);
		if (above instanceof TileEntityTank) {
			return (TileEntityTank) above;
		}
		else {
			return null;
		}
	}

	public void moveFluidBelow() {
		TileEntityTank below = getTankBelow(this);
		if (below == null) {
			return;
		}

		int used = below.tank.fill(tank.getFluid(), true);
		if (used > 0)
			tank.drain(used, true);
		updateData();
		below.updateData();
	}

	public static ArrayList<TileEntityTank> getAdjacentTanks(TileEntityTank tile) {
		ArrayList<TileEntityTank> adjacents = new ArrayList<TileEntityTank>();
		fillWithAdjacentTanks(tile, adjacents, tile.tank.getFluid());
		return adjacents;
	}

	/**
	 * Recursive function, must only be called by getAdjacentTanks(TileTank
	 * tile)
	 */
	private static void fillWithAdjacentTanks(TileEntityTank tile, ArrayList<TileEntityTank> result, FluidStack fluid) {
		result.add(tile);
		for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			if (direction == ForgeDirection.DOWN || direction == ForgeDirection.UP)
				continue;
			TileEntity other = tile.worldObj.getBlockTileEntity(tile.xCoord + direction.offsetX, tile.yCoord,
					tile.zCoord + direction.offsetZ);
			if (other instanceof TileEntityTank
					&& !result.contains(other)
					&& (((TileEntityTank) other).tank.getFluidAmount() == 0
							|| ((TileEntityTank) other).tank.getFluid().isFluidEqual(fluid) || fluid == null)) {
				if (fluid == null)
					fluid = ((TileEntityTank) other).tank.getFluid();
				fillWithAdjacentTanks((TileEntityTank) other, result, fluid);
			}
		}
	}

	public void moveFluidToAdjacent() {
		FluidStack fluid = tank.getFluid();
		if (fluid == null)
			return;
		ArrayList<TileEntityTank> adjacents = getAdjacentTanks(this);
		if (adjacents.size() <= 1) // Note that this tank will be contained in
									// the list too
			return;

		int totalAmount = 0;
		for (TileEntityTank other : adjacents)
			totalAmount += other.tank.getFluidAmount();

		int splitAmount = totalAmount / adjacents.size();
		int balance = 0; // Prevent creation or destruction of fluid cause of
							// Euclidean division
		for (TileEntityTank other : adjacents) {
			if (other.tank.getFluidAmount() < splitAmount)
				balance += other.tank.fill(new FluidStack(fluid, splitAmount - other.tank.getFluidAmount()), true);
			else if (other.tank.getFluidAmount() > splitAmount)
				balance -= other.tank.drain(other.tank.getFluidAmount() - splitAmount, true).amount;
			else
				continue;
			other.updateData();
		}
		if (balance > 0)
			tank.drain(balance, true);
		else if (balance < 0)
			tank.fill(new FluidStack(fluid, -balance), true);
		else
			return;
	}

	public FluidStack getFluid() {
		return tank.getFluid();
	}

	public boolean onBlockActivated(World world, EntityPlayer player, Block block, int x, int y, int z) {
		ItemStack is = player.inventory.getCurrentItem();
		FluidStack fs = FluidContainerRegistry.getFluidForFilledItem(is);
		if (fs != null && fs.isFluidEqual(fs)) {
			int filled = fill(ForgeDirection.DOWN, fs, true);
			if (filled != 0) {
				if (!player.capabilities.isCreativeMode) {
					player.inventory.consumeInventoryItem(is.itemID);
					if (is.getItem().hasContainerItem()) {
						ItemStack container = new ItemStack(is.getItem().getContainerItem(), 1);
						if (!player.inventory.addItemStackToInventory(container))
							player.dropPlayerItemWithRandomChoice(container, false);
					}
				}
				return true;
			}
		}
		if (is != null) {
			int dispo = tank.getFluidAmount();
			ItemStack filled = FluidContainerRegistry.fillFluidContainer(tank.getFluid(), is);
			fs = FluidContainerRegistry.getFluidForFilledItem(filled);
			if (fs != null) {
				if (!player.capabilities.isCreativeMode) {
					if (is.stackSize > 1) {
						if (!player.inventory.addItemStackToInventory(filled))
							return false;
						else {
							player.inventory.consumeInventoryItem(is.itemID);
						}
					}
					else {
						player.inventory.consumeInventoryItem(is.itemID);
						player.inventory.setInventorySlotContents(player.inventory.currentItem, filled);
					}
				}
				drain(ForgeDirection.DOWN, fs.amount, true);
				return true;
			}
		}
		return false;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack ressource, boolean doFill) {
		if (ressource == null)
			return 0;

		ressource = ressource.copy();
		int totalUsed = 0;
		TileEntityTank tankToFill = getBottomTank();

		FluidStack liquid = tankToFill.tank.getFluid();
		if (liquid != null && liquid.amount > 0 && !liquid.isFluidEqual(ressource))
			return 0;

		while (tankToFill != null && ressource.amount > 0) {
			ArrayList<TileEntityTank> adjacents = getAdjacentTanks(tankToFill);

			int toFill = ressource.amount / adjacents.size();
			if (toFill == 0)
				break;
			for (TileEntityTank other : adjacents) {
				int used = other.tank.fill(new FluidStack(ressource.fluidID, toFill), doFill);
				ressource.amount -= used;
				totalUsed += used;
				if (used != 0)
					other.updateData();
			}
			tankToFill = getTankAbove(tankToFill);
		}
		return totalUsed;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxEmpty, boolean doDrain) {
		int totalDrained = 0;
		FluidStack lastDrained = null;
		TileEntityTank tankToDrain = getTopTank();

		while (tankToDrain != null && tankToDrain.yCoord >= yCoord && maxEmpty > 0) {
			ArrayList<TileEntityTank> adjacents = getAdjacentTanks(tankToDrain);

			int toDrain = maxEmpty / adjacents.size();
			if (toDrain == 0)
				break;
			for (TileEntityTank other : adjacents) {
				lastDrained = other.tank.drain(toDrain, doDrain);
				if (lastDrained == null)
					continue;
				maxEmpty -= lastDrained.amount;
				totalDrained += lastDrained.amount;
				if (lastDrained.amount != 0)
					other.updateData();
			}
			tankToDrain = getTankBelow(tankToDrain);
		}

		if (lastDrained == null)
			return null;
		return new FluidStack(lastDrained.fluidID, totalDrained);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (resource != null && !resource.isFluidEqual(tank.getFluid()))
			return null;
		return drain(from, resource.amount, doDrain);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection direction) {
		FluidTank compositeTank = new FluidTank(tank.getCapacity());

		TileEntityTank tile = getBottomTank();

		int capacity = tank.getCapacity();

		if (tile != null && tile.tank.getFluid() != null) {
			compositeTank.setFluid(tile.tank.getFluid().copy());
		}
		else {
			return new FluidTankInfo[] { compositeTank.getInfo() };
		}

		tile = getTankAbove(tile);

		while (tile != null) {

			FluidStack liquid = tile.tank.getFluid();
			if (liquid == null || liquid.amount == 0) {
				// NOOP
			}
			else if (!compositeTank.getFluid().isFluidEqual(liquid)) {
				break;
			}
			else {
				compositeTank.getFluid().amount += liquid.amount;
			}

			capacity += tile.tank.getCapacity();
			tile = getTankAbove(tile);
		}

		compositeTank.setCapacity(capacity);
		return new FluidTankInfo[] { compositeTank.getInfo() };
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public Packet getDescriptionPacket() {
		PacketSerializableInfo packet = new PacketSerializableInfo(PacketIDs.TANK_UPDATE);
		RenderInfoTank info = new RenderInfoTank();
		info.x = xCoord;
		info.y = yCoord;
		info.z = zCoord;
		if (tank.getFluid() != null) {
			info.fluidId = tank.getFluid().fluidID;
			info.amount = tank.getFluid().amount;
		}
		packet.info = info;
		return packet.getPacket();
	}

	@Override
	public void updateSynchronisedInfo(ISerializable info) {
		if (info instanceof RenderInfoTank) {
			if (((RenderInfoTank) info).fluidId != 0)
				tank.setFluid(new FluidStack(((RenderInfoTank) info).fluidId, ((RenderInfoTank) info).amount));
			else
				tank.setFluid(null);
			updateRender();
		}
	}

	public void updateData() {
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public void updateRender() {
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}
}

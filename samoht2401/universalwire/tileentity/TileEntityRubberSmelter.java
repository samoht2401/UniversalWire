package samoht2401.universalwire.tileentity;

import samoht2401.universalwire.UniversalWire;
import ic2.core.Ic2Items;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.oredict.OreDictionary;

public class TileEntityRubberSmelter extends TileEntity implements IInventory, IFluidTank {

	private ItemStack[] inv;
	private FluidTank rubberTank;

	public int currentCookTime;
	public final int TOTAL_COOK_TIME = 400;
	public int currentFuelLevel;
	public int startFuelLevel;

	private final int MAX_LIQUID = 1000 * 10;

	public TileEntityRubberSmelter() {
		inv = new ItemStack[2];
		rubberTank = new FluidTank(MAX_LIQUID);
	}

	public int getScaledLiquid(int i) {
		return rubberTank.getFluid() != null ? (int) (((float) rubberTank.getFluidAmount() / (float) (MAX_LIQUID)) * i)
				: 0;
	}

	public int getScaledCookTime(int i) {
		return (int) ((float) currentCookTime / (float) TOTAL_COOK_TIME * i);
	}

	public int getScaledFuelLevel(int i) {
		return (int) ((float) currentFuelLevel / (float) startFuelLevel * i);
	}

	public void updateFluidId(int val) {
		if (rubberTank.getFluid() != null)
			rubberTank.setFluid(new FluidStack(val, rubberTank.getFluidAmount()));
		else
			rubberTank.setFluid(new FluidStack(val, 0));
	}

	public void updateFluidAmount(int val) {
		if (rubberTank.getFluid() != null)
			rubberTank.setFluid(new FluidStack(rubberTank.getFluid(), val));
	}

	@Override
	public int getSizeInventory() {
		return inv.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inv[slot];
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inv[slot] = stack;
		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			if (stack.stackSize <= amt) {
				setInventorySlotContents(slot, null);
			}
			else {
				stack = stack.splitStack(amt);
				if (stack.stackSize == 0) {
					setInventorySlotContents(slot, null);
				}
			}
		}
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			setInventorySlotContents(slot, null);
		}
		return stack;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) == this
				&& player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) < 64;
	}

	@Override
	public void openChest() {
	}

	@Override
	public void closeChest() {
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);

		NBTTagList tagList = tagCompound.getTagList("Inventory");
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) tagList.tagAt(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < inv.length) {
				inv[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}

		if (tagCompound.hasKey("rubberTank"))
			rubberTank.setFluid(FluidStack.loadFluidStackFromNBT(tagCompound.getCompoundTag("rubberTank")));

		currentCookTime = tagCompound.getInteger("cookTime");
		currentFuelLevel = tagCompound.getInteger("fuelLevel");
		startFuelLevel = tagCompound.getInteger("startFuelLevel");
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);

		NBTTagList itemList = new NBTTagList();
		for (int i = 0; i < inv.length; i++) {
			ItemStack stack = inv[i];
			if (stack != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte) i);
				stack.writeToNBT(tag);
				itemList.appendTag(tag);
			}
		}
		tagCompound.setTag("Inventory", itemList);

		if (rubberTank.getFluid() != null) {
			NBTTagCompound rubberTag = new NBTTagCompound();
			rubberTank.getFluid().writeToNBT(rubberTag);
			tagCompound.setCompoundTag("rubberTank", rubberTag);
		}

		tagCompound.setInteger("cookTime", currentCookTime);
		tagCompound.setInteger("fuelLevel", currentFuelLevel);
		tagCompound.setInteger("startFuelLevel", startFuelLevel);
	}

	@Override
	public String getInvName() {
		return "tileentityRubberSmelter";
	}

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public void updateEntity() {

		int prod = getRubberProduction(inv[0]);
		if (prod > 0
				&& (rubberTank.getFluid() == null || rubberTank.getFluidAmount() + prod <= rubberTank.getCapacity())) {
			if (currentCookTime >= TOTAL_COOK_TIME) {
				currentCookTime = 0;
				decrStackSize(0, 1);
				rubberTank.fill(new FluidStack(UniversalWire.fluidRubber, prod), true);
			}
			else if (currentFuelLevel > 0 || refuel()) {
				currentCookTime += 1;
				currentFuelLevel -= 1;
			}
			else
				currentCookTime = 0;
		}
		else
			currentCookTime = 0;
	}

	private boolean refuel() {
		if (inv[1] == null || !TileEntityFurnace.isItemFuel(inv[1]))
			return false;
		startFuelLevel = currentFuelLevel = TileEntityFurnace.getItemBurnTime(inv[1]);
		inv[1].stackSize -= 1;
		if (inv[1].stackSize <= 0) {
			inv[1] = inv[1].getItem().getContainerItemStack(inv[1]);
		}
		return true;
	}

	public int getRubberProduction(ItemStack itemStack) {
		if (itemStack == null)
			return 0;
		if (itemStack.itemID == Ic2Items.rubber.itemID)
			return 500;
		if (itemStack.itemID == Ic2Items.rubberWood.itemID)
			return 200;
		return 0;
	}

	@Override
	public FluidStack getFluid() {
		return rubberTank.getFluid();
	}

	@Override
	public int getFluidAmount() {
		return rubberTank.getFluidAmount();
	}

	@Override
	public int getCapacity() {
		return rubberTank.getCapacity();
	}

	@Override
	public FluidTankInfo getInfo() {
		return rubberTank.getInfo();
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if (resource.isFluidEqual(rubberTank.getFluid()))
			return rubberTank.fill(resource, doFill);
		return 0;
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		return rubberTank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (i == 0)
			return OreDictionary.itemMatches(itemstack, Ic2Items.rubber, true)
					|| OreDictionary.itemMatches(itemstack, Ic2Items.rubberWood, true);
		if (i == 1)
			return TileEntityFurnace.isItemFuel(itemstack);
		return false;
	}
}

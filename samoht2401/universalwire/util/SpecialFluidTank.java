package samoht2401.universalwire.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;


public class SpecialFluidTank implements IFluidTank {

    protected FluidStack fluid;
    protected int capacity;
    protected TileEntity tile;

    public SpecialFluidTank(int capacity) {

        this(null, capacity);
    }

    public SpecialFluidTank(FluidStack stack, int capacity) {

        this.fluid = stack;
        this.capacity = capacity;
    }

    public SpecialFluidTank(Fluid fluid, int amount, int capacity) {

        this(new FluidStack(fluid, amount), capacity);
    }

    public SpecialFluidTank readFromNBT(NBTTagCompound nbt) {

        if (!nbt.hasKey("Empty")) {
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);

            if (fluid != null) {
                setFluid(fluid);
            }
        }
        return this;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

        if (fluid != null) {
            fluid.writeToNBT(nbt);
        } else {
            nbt.setString("Empty", "");
        }
        return nbt;
    }

    public void setFluid(FluidStack fluid) {

        this.fluid = fluid;
    }

    public void setCapacity(int capacity) {

        this.capacity = capacity;
    }

    /* IFluidTank */
    @Override
    public FluidStack getFluid() {

        return fluid;
    }

    @Override
    public int getFluidAmount() {

        if (fluid == null) {
            return 0;
        }
        return fluid.amount;
    }

    @Override
    public int getCapacity() {

        return capacity;
    }

    @Override
    public FluidTankInfo getInfo() {

        return new FluidTankInfo(this);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {

        if (resource == null) {
            return 0;
        }
        if (!doFill) {
            if (fluid == null) {
                return Math.min(capacity, resource.amount);
            }
            if (!fluid.isFluidEqual(resource)) {
                return 0;
            }
            return Math.min(capacity - fluid.amount, resource.amount);
        }
        if (fluid == null) {
            fluid = new FluidStack(resource, Math.min(capacity, resource.amount));

            if (tile != null) {
                FluidEvent.fireEvent(new FluidEvent.FluidFillingEvent(fluid, tile.worldObj, tile.xCoord, tile.yCoord, tile.zCoord, this));
            }
            return fluid.amount;
        }
        if (!fluid.isFluidEqual(resource)) {
            return 0;
        }
        int filled = capacity - fluid.amount;

        if (resource.amount < filled) {
            fluid.amount += resource.amount;
            filled = resource.amount;
        } else {
            fluid.amount = capacity;
        }
        if (tile != null) {
            FluidEvent.fireEvent(new FluidEvent.FluidFillingEvent(fluid, tile.worldObj, tile.xCoord, tile.yCoord, tile.zCoord, this));
        }
        return filled;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {

        if (fluid == null) {
            return null;
        }
        int drained = maxDrain;

        if (fluid.amount < drained) {
            drained = fluid.amount;
        }
        FluidStack stack = new FluidStack(fluid, drained);

        if (doDrain) {
            fluid.amount -= drained;

            if (fluid.amount <= 0) {
                fluid.amount = 0;
            }
            if (tile != null) {
                FluidEvent.fireEvent(new FluidEvent.FluidDrainingEvent(fluid, tile.worldObj, tile.xCoord, tile.yCoord, tile.zCoord, this));
            }
        }
        return stack;
    }
}

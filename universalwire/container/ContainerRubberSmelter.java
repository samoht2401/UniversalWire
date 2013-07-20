package samoht2401.universalwire.container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.Ic2Items;
import samoht2401.universalwire.gui.slots.SlotBurningFuel;
import samoht2401.universalwire.gui.slots.SlotTyped;
import samoht2401.universalwire.tileentity.TileEntityRubberSmelter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.LiquidStack;

public class ContainerRubberSmelter extends Container {

	protected TileEntityRubberSmelter tileEntity;

	public ContainerRubberSmelter(InventoryPlayer inventoryPlayer, TileEntityRubberSmelter te) {
		super();
		tileEntity = te;

		// the Slot constructor takes the IInventory and the slot number in that
		// it binds to
		// and the x-y coordinates it resides on-screen
		addSlotToContainer(new SlotTyped(tileEntity, 0, 56, 17).addToValidId(Ic2Items.rubber).addToValidId(Ic2Items.rubberWood));
		addSlotToContainer(new SlotBurningFuel(tileEntity, 1, 56, 53));

		// commonly used vanilla code that adds the player's inventory
		bindPlayerInventory(inventoryPlayer);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		LiquidStack l = tileEntity.getLiquidStack();
		if (l == null)
			return;
		for (int i = 0; i < this.crafters.size(); ++i) {
			ICrafting icrafting = (ICrafting) this.crafters.get(i);

			icrafting.sendProgressBarUpdate(this, 0, l.itemID);
			icrafting.sendProgressBarUpdate(this, 1, l.amount);
			icrafting.sendProgressBarUpdate(this, 2, tileEntity.currentCookTime);
			icrafting.sendProgressBarUpdate(this, 3, tileEntity.currentFuelLevel);
			icrafting.sendProgressBarUpdate(this, 4, tileEntity.startFuelLevel);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int i, int val) {

		LiquidStack l = tileEntity.getLiquidStack();
		switch (i) {
		case 0: {
			tileEntity.updateLiquidId(val);
			break;
		}
		case 1: {
			tileEntity.updateLiquidAmount(val);
			break;
		}
		case 2: {
			tileEntity.currentCookTime = val;
			break;
		}
		case 3: {
			tileEntity.currentFuelLevel = val;
			break;
		}
		case 4: {
			tileEntity.startFuelLevel = val;
			break;
		}
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tileEntity.isUseableByPlayer(player);
	}

	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
		ItemStack stack = null;
		Slot slotObject = (Slot) inventorySlots.get(slot);

		// null checks and checks if the item can be stacked (maxStackSize > 1)
		if (slotObject != null && slotObject.getHasStack()) {
			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();

			// merges the item into player inventory since its in the tileEntity
			if (slot < 2) {
				if (!this.mergeItemStack(stackInSlot, 2, 38, true)) {
					return null;
				}
			}
			// places it into the tileEntity is possible since its in the player
			// inventory
			else {
				boolean merged = false;
				if (getSlot(0).isItemValid(stackInSlot)) {
					if (this.mergeItemStack(stackInSlot, 0, 1, false))
						merged = true;
				}
				if (!merged && getSlot(1).isItemValid(stackInSlot)) {
					if (!this.mergeItemStack(stackInSlot, 1, 2, false)) {
						return null;
					}
				}
			}

			if (stackInSlot.stackSize == 0) {
				slotObject.putStack(null);
			} else {
				slotObject.onSlotChanged();
			}

			if (stackInSlot.stackSize == stack.stackSize) {
				return null;
			}
			slotObject.onPickupFromSlot(player, stackInSlot);
		}
		return stack;
	}
}

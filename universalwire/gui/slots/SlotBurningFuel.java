package samoht2401.universalwire.gui.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class SlotBurningFuel extends Slot {

	public SlotBurningFuel(IInventory inventory, int id, int x, int y) {
		super(inventory, id, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack itemStack) {
		return TileEntityFurnace.isItemFuel(itemStack);
	}
}

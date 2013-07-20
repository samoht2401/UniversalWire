package samoht2401.universalwire.gui.slots;

import java.util.ArrayList;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class SlotTyped extends Slot {

	private ArrayList<Integer> validId;
	
	public SlotTyped(IInventory inventory, int id, int x, int y) {
		super(inventory, id, x, y);
		validId = new ArrayList<Integer>();
	}
	
	public SlotTyped addToValidId(ItemStack itemStack)
	{
		validId.add(itemStack.itemID);
		return this;
	}
	
	@Override
	public boolean isItemValid(ItemStack itemStack) {
		return validId.contains(itemStack.itemID);
	}

}

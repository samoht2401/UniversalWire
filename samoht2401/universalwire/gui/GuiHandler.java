package samoht2401.universalwire.gui;

import samoht2401.universalwire.container.ContainerRubberSmelter;
import samoht2401.universalwire.tileentity.TileEntityRubberSmelter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	// returns an instance of the Container you made earlier
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityRubberSmelter) {
			return new ContainerRubberSmelter(player.inventory,
					(TileEntityRubberSmelter) tileEntity);
		}
		return null;
	}

	// returns an instance of the Gui you made earlier
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityRubberSmelter) {
			return new GuiRubberSmelter(player.inventory,
					(TileEntityRubberSmelter) tileEntity);
		}
		return null;

	}
	
}

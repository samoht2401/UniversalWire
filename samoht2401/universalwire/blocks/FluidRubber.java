package samoht2401.universalwire.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraftforge.fluids.Fluid;

public class FluidRubber extends Fluid {
	
	public FluidRubber(int blockId) {
		super("rubber");
		setBlockID(blockID);
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		stillIcon = iconRegister.registerIcon("UniversalWire:rubber");
		flowingIcon = iconRegister.registerIcon("UniversalWire:rubber_flow");
	}
}

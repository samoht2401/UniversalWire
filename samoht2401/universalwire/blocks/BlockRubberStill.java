package samoht2401.universalwire.blocks;

import samoht2401.universalwire.UniversalWire;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidFinite;

public class BlockRubberStill extends BlockFluidFinite {

	public BlockRubberStill(int i, Material material) {
		super(i, UniversalWire.fluidRubber, material);

		// setHardness(100F);
		// setLightOpacity(3);
		setCreativeTab(CreativeTabs.tabBlock);
	}

	/*
	 * @Override public int getRenderType() { return
	 * Block.waterStill.getRenderType(); }
	 * 
	 * @Override public boolean isBlockReplaceable(World world, int i, int j,
	 * int k) { return true; }
	 */

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		UniversalWire.fluidRubber.registerIcons(iconRegister);
	}

	@Override
	public Icon getIcon(int side, int metadata) {
		if (side == 1)
			return UniversalWire.fluidRubber.getStillIcon();
		else
			return UniversalWire.fluidRubber.getFlowingIcon();
	}

	/*
	 * @Override public int getFireSpreadSpeed(World world, int x, int y, int z,
	 * int metadata, ForgeDirection face) { return 0; }
	 * 
	 * @Override public int getFlammability(IBlockAccess world, int x, int y,
	 * int z, int metadata, ForgeDirection face) { return 0; }
	 */

	@Override
	public boolean isFlammable(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face) {
		return false;
	}

	@Override
	public boolean isFireSource(World world, int x, int y, int z, int metadata, ForgeDirection side) {
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess world, int x, int y, int z)
	{
		return 0;
	}
}

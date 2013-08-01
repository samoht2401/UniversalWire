package samoht2401.universalwire.blocks;

import samoht2401.universalwire.UniversalWire;
import samoht2401.universalwire.tileentity.TileEntityTank;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;

public class BlockTank extends BlockContainer {

	public static Icon colorIcon;
	public static Icon glassIcon;
	public static Icon currentIcon;

	public final static float TANK_MIN = 0f;
	public final static float TANK_MAX = 1f;
	public final static int TANK_CAPACITY = FluidContainerRegistry.BUCKET_VOLUME * 16;

	public BlockTank(int id, Material mat) {
		super(id, mat);
		setHardness(2.0F);
		setResistance(5.0F);
		setCreativeTab(CreativeTabs.tabBlock);
		setBlockBounds(TANK_MIN, TANK_MIN, TANK_MIN, TANK_MAX, TANK_MAX, TANK_MAX);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public Icon getIcon(int face, int meta) {
		return currentIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		colorIcon = par1IconRegister.registerIcon("UniversalWire:tankColor");
		glassIcon = par1IconRegister.registerIcon("UniversalWire:tankGlass");
	}

	@Override
	public int getRenderType() {
		return UniversalWire.blockRenderId;
	}

	@Override
	public boolean hasTileEntity() {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityTank();
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int n, float i, float j, float k)
    {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te instanceof TileEntityTank)
			return ((TileEntityTank)te).onBlockActivated(world, player, this, x, y, z);
		return false;
    }

	public void resetBlockBound(IBlockAccess world, int x, int y, int z) {
		float minX, minY, minZ;
		minX = minY = minZ = TANK_MIN;
		float maxX, maxY, maxZ;
		maxX = maxY = maxZ = TANK_MAX;
		setBlockBounds(minX, 0f, minZ, maxX, 1f, maxZ);
	}
}

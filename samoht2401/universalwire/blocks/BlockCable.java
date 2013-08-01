package samoht2401.universalwire.blocks;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import samoht2401.universalwire.UniversalWire;
import samoht2401.universalwire.render.RenderInfoCable;
import samoht2401.universalwire.tileentity.TileEntityCable;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockCable extends BlockContainer {

	public RenderInfoCable genericRenderInfo;
	public final static int CABLE_TEX_INDEX = 0;
	public final static int CABLEB_TEX_INDEX = 1;
	public final static int CABLEI_TEX_INDEX = 2;
	public final static int REDL_TEX_INDEX = 3;
	public final static float CABLE_MIN_SIZE = 5f * (1f / 16f);
	public final static float CABLE_MAX_SIZE = 11f * (1f / 16f);
	public final static float LIQUID_OFFSET = 0.5f * (1f / 16f);

	public BlockCable(int id, Material mat) {
		super(id, mat);
		setHardness(2.0F);
		setResistance(5.0F);
		setCreativeTab(CreativeTabs.tabBlock);
		genericRenderInfo = new RenderInfoCable();
		resetBlockBound();
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
	public Icon getIcon(int face, int meta){
		return genericRenderInfo.textures[CABLE_TEX_INDEX];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		genericRenderInfo.textures = new Icon[4];
		genericRenderInfo.textures[CABLE_TEX_INDEX] = par1IconRegister.registerIcon("universalwire:cable");
		genericRenderInfo.textures[CABLEB_TEX_INDEX] = par1IconRegister.registerIcon("universalwire:cableBranche");
		genericRenderInfo.textures[CABLEI_TEX_INDEX] = par1IconRegister.registerIcon("universalwire:cableItem");
		genericRenderInfo.textures[REDL_TEX_INDEX] = par1IconRegister.registerIcon("universalwire:redstoneLiquid");
		genericRenderInfo.connections = new ArrayList<ForgeDirection>();

		for (int i = 0; i < 7; i++) {

		}
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
		return new TileEntityCable();
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
		//UniversalWire.systemManager.addItem(world, x, y, z, ItemType.cable);
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof TileEntityCable)
			((TileEntityCable) te).onAdded();
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int meta) {
		super.breakBlock(world, x, y, z, id, meta);
		if (id != UniversalWire.blockCable.blockID)
			return;
		UniversalWire.systemManager.removeItem(world, x, y, z);
		world.removeBlockTileEntity(x, y, z);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int id) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof TileEntityCable)
			((TileEntityCable) te).onNeighborBlockChange(world, x, y, z, id);
	}

	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB axisalignedbb, List arraylist, Entity entity) {
		resetBlockBound();
		super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, arraylist, entity);

		float min = CABLE_MIN_SIZE;
		float max = CABLE_MAX_SIZE;

		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityCable) {
			TileEntityCable te = (TileEntityCable) tileEntity;

			if (te.getConnections().contains(ForgeDirection.NORTH)) {
				setBlockBounds(min, min, 0f, max, max, min);
				super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, arraylist, entity);
			}
			if (te.getConnections().contains(ForgeDirection.SOUTH)) {
				setBlockBounds(min, min, max, max, max, 1f);
				super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, arraylist, entity);
			}
			if (te.getConnections().contains(ForgeDirection.WEST)) {
				setBlockBounds(0f, min, min, min, max, max);
				super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, arraylist, entity);
			}
			if (te.getConnections().contains(ForgeDirection.EAST)) {
				setBlockBounds(max, min, min, 1f, max, max);
				super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, arraylist, entity);
			}
			if (te.getConnections().contains(ForgeDirection.UP)) {
				setBlockBounds(min, max, min, max, 1f, max);
				super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, arraylist, entity);
			}
			if (te.getConnections().contains(ForgeDirection.DOWN)) {
				setBlockBounds(min, 0f, min, max, min, max);
				super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, arraylist, entity);
			}
		}

		resetBlockBound();
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		float xMin, xMax, yMin, yMax, zMin, zMax;
		xMin = yMin = zMin = CABLE_MIN_SIZE;
		xMax = yMax = zMax = CABLE_MAX_SIZE;

		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityCable) {
			TileEntityCable te = (TileEntityCable) tileEntity;

			if (te.getConnections().contains(ForgeDirection.NORTH))
				zMin = 0f;
			if (te.getConnections().contains(ForgeDirection.SOUTH))
				zMax = 1f;
			if (te.getConnections().contains(ForgeDirection.WEST))
				xMin = 0f;
			if (te.getConnections().contains(ForgeDirection.EAST))
				xMax = 1f;
			if (te.getConnections().contains(ForgeDirection.UP))
				yMax = 1f;
			if (te.getConnections().contains(ForgeDirection.DOWN))
				yMin = 0f;
		}
		return AxisAlignedBB.getBoundingBox((double) x + xMin, (double) y + yMin, (double) z + zMin, (double) x + xMax, (double) y + yMax,
				(double) z + zMax);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getBlockTexture(IBlockAccess iblockaccess, int x, int y, int z, int meta) {

		TileEntity tile = iblockaccess.getBlockTileEntity(x, y, z);
		if (!(tile instanceof TileEntityCable))
			return genericRenderInfo.textures[0];
		return ((TileEntityCable) tile).getCurrentTexture();
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 origin, Vec3 direction) {
		float xMin, xMax, yMin, yMax, zMin, zMax;
		xMin = yMin = zMin = CABLE_MIN_SIZE;
		xMax = yMax = zMax = CABLE_MAX_SIZE;

		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (!(tileEntity instanceof TileEntityCable))
			return null;
		TileEntityCable te = (TileEntityCable) tileEntity;

		MovingObjectPosition[] hits = new MovingObjectPosition[] { null, null, null };
		boolean needAxisCheck = false;
		boolean needCenterCheck = true;

		// check along the x axis

		if (te.getConnections().contains(ForgeDirection.WEST)) {
			xMin = 0.0F;
			needAxisCheck = true;
		}

		if (te.getConnections().contains(ForgeDirection.EAST)) {
			xMax = 1.0F;
			needAxisCheck = true;
		}

		if (needAxisCheck) {
			setBlockBounds(xMin, yMin, zMin, xMax, yMax, zMax);

			hits[0] = super.collisionRayTrace(world, x, y, z, origin, direction);
			xMin = CABLE_MIN_SIZE;
			xMax = CABLE_MAX_SIZE;
			needAxisCheck = false;
			needCenterCheck = false; // center already checked through this axis
		}

		// check along the y axis

		if (te.getConnections().contains(ForgeDirection.DOWN)) {
			yMin = 0.0F;
			needAxisCheck = true;
		}

		if (te.getConnections().contains(ForgeDirection.UP)) {
			yMax = 1.0F;
			needAxisCheck = true;
		}

		if (needAxisCheck) {
			setBlockBounds(xMin, yMin, zMin, xMax, yMax, zMax);

			hits[1] = super.collisionRayTrace(world, x, y, z, origin, direction);
			yMin = CABLE_MIN_SIZE;
			yMax = CABLE_MAX_SIZE;
			needAxisCheck = false;
			needCenterCheck = false; // center already checked through this axis
		}

		// check along the z axis

		if (te.getConnections().contains(ForgeDirection.NORTH)) {
			zMin = 0.0F;
			needAxisCheck = true;
		}

		if (te.getConnections().contains(ForgeDirection.SOUTH)) {
			zMax = 1.0F;
			needAxisCheck = true;
		}

		if (needAxisCheck) {
			setBlockBounds(xMin, yMin, zMin, xMax, yMax, zMax);

			hits[2] = super.collisionRayTrace(world, x, y, z, origin, direction);
			zMin = CABLE_MIN_SIZE;
			zMax = CABLE_MAX_SIZE;
			needAxisCheck = false;
			needCenterCheck = false; // center already checked through this axis
		}

		// check center (only if no axis were checked/the pipe has no
		// connections)

		if (needCenterCheck) {
			setBlockBounds(xMin, yMin, zMin, xMax, yMax, zMax);

			hits[0] = super.collisionRayTrace(world, x, y, z, origin, direction);
		}

		// get closest hit
		double minLengthSquared = Double.POSITIVE_INFINITY;
		int minIndex = -1;

		for (int i = 0; i < hits.length; i++) {
			MovingObjectPosition hit = hits[i];
			if (hit == null)
				continue;

			double lengthSquared = hit.hitVec.squareDistanceTo(origin);

			if (lengthSquared < minLengthSquared) {
				minLengthSquared = lengthSquared;
				minIndex = i;
			}
		}

		// reset bounds
		setBlockBounds(0, 0, 0, 1, 1, 1);

		if (minIndex == -1)
			return null;
		else
			return hits[minIndex];
	}

	public void resetBlockBound() {
		setBlockBounds(0.313f, 0.313f, 0.313f, 0.688f, 0.688f, 0.688f);
	}
}

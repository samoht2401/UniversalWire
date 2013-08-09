package samoht2401.universalwire.render;

import java.util.HashMap;

import org.lwjgl.opengl.GL11;

import samoht2401.universalwire.blocks.BlockTank;
import samoht2401.universalwire.render.RenderHelper.BlockInterface;
import samoht2401.universalwire.tileentity.TileEntityTank;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class TankSpecialRenderer extends TileEntitySpecialRenderer {

	final static private int LIQUID_STAGES = 100;

	private class DisplayFluidList {

		public int[][] stillLiquid = new int[64 /* 2^6 */][LIQUID_STAGES];
		public int[][] faceFlowing = new int[8 /* 2^3 */][LIQUID_STAGES - 1];
		public int centerFlowing = -1;
	}

	private int getSideIndex(boolean west, boolean east, boolean down, boolean up, boolean north, boolean south) {
		int result = 0;
		if (west)
			result += 1;
		if (east)
			result += 2;
		if (down)
			result += 4;
		if (up)
			result += 8;
		if (north)
			result += 16;
		if (south)
			result += 32;
		return result;
	}

	private final HashMap<Integer, DisplayFluidList> displayFluidLists = new HashMap<Integer, DisplayFluidList>();

	private DisplayFluidList getFluidList(int liquidId, World world) {
		if (displayFluidLists.containsKey(liquidId)) {
			return displayFluidLists.get(liquidId);
		}
		DisplayFluidList d = new DisplayFluidList();
		displayFluidLists.put(liquidId, d);

		BlockInterface block = new BlockInterface();

		Fluid fluid = FluidRegistry.getFluid(liquidId);
		if (fluid.getBlockID() > 0) {
			block.baseBlock = Block.blocksList[fluid.getBlockID()];
		}
		else {
			block.baseBlock = Block.waterStill;
		}
		block.texture = fluid.getStillIcon();

		float boardOffset = 1f / 16f;
		for (int s = 0; s < LIQUID_STAGES; ++s) {
			float ratio = (float) s / (float) (LIQUID_STAGES - 1);

			// StillLiquid
			for (int index = 0; index < 64; index++) {
				d.stillLiquid[index][s] = GLAllocation.generateDisplayLists(1);
				GL11.glNewList(d.stillLiquid[index][s], 4864 /* GL_COMPILE */);

				block.minX = (index & 1) != 0 ? 0f : boardOffset;
				block.minY = (index & 4) != 0 ? 0f : boardOffset;
				block.minZ = (index & 16) != 0 ? 0f : boardOffset;

				block.maxX = (index & 2) != 0 ? 1f : 1 - boardOffset;
				block.maxY = s == LIQUID_STAGES - 1 && (index & 8) != 0 ? 1f : ratio * (1 - 2 * boardOffset)
						+ boardOffset;
				block.maxZ = (index & 32) != 0 ? 1f : 1 - boardOffset;

				RenderHelper.renderXMin = block.minX != 0;
				RenderHelper.renderXMax = block.maxX != 1;
				RenderHelper.renderYMin = block.minY != 0;
				RenderHelper.renderYMax = block.maxY != 1;
				RenderHelper.renderZMin = block.minZ != 0;
				RenderHelper.renderZMax = block.maxZ != 1;

				RenderHelper.renderBlockInterface(block, world, 0, 0, 0, false, false, true);

				GL11.glEndList();
			}

			// FaceFlowing
			if (s < LIQUID_STAGES - 1) {
				RenderHelper.renderXMin = true;
				RenderHelper.renderXMax = true;
				RenderHelper.renderYMin = false;
				for (int index = 0; index < 8; index++) {
					d.faceFlowing[index][s] = GLAllocation.generateDisplayLists(1);
					GL11.glNewList(d.faceFlowing[index][s], 4864 /* GL_COMPILE */);

					block.minX = boardOffset;
					block.minY = ratio * (1 - 2 * boardOffset);
					block.minZ = (index & 1) != 0 ? 0f : boardOffset;

					block.maxX = block.minX + boardOffset;
					block.maxY = (index & 4) != 0 ? 1f : 1 + boardOffset * 1.2f;
					block.maxZ = (index & 2) != 0 ? 1f : 1 - boardOffset;

					RenderHelper.renderYMax = block.maxY != 1;
					RenderHelper.renderZMin = block.minZ != 0;
					RenderHelper.renderZMax = block.maxZ != 1;

					RenderHelper.renderBlockInterface(block, world, 0, 0, 0, false, false, true);

					GL11.glEndList();
				}
			}
		}

		return d;
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
		if (!(tileentity instanceof TileEntityTank))
			return;
		TileEntityTank tankEntity = (TileEntityTank) tileentity;

		FluidStack fluid = tankEntity.getFluid();
		if (fluid == null || fluid.fluidID == 0)
			return;

		DisplayFluidList d = getFluidList(fluid.fluidID, tankEntity.worldObj);
		if (d == null)
			return;

		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);

		int stage = (int) ((float) fluid.amount / (float) (tankEntity.getCapacity()) * (LIQUID_STAGES - 1));

		if (tankEntity.shouldRenderFluidLevel() && fluid.amount > 0) {
			int index = getSideIndex(tankEntity.isConnected(ForgeDirection.WEST),
					tankEntity.isConnected(ForgeDirection.EAST), tankEntity.isConnected(ForgeDirection.DOWN),
					tankEntity.isConnected(ForgeDirection.UP), tankEntity.isConnected(ForgeDirection.NORTH),
					tankEntity.isConnected(ForgeDirection.SOUTH));

			func_110628_a(TextureMap.field_110575_b);
			GL11.glCallList(d.stillLiquid[index][stage]);
		}

		int[] tab = { 1, 2 };
		int[] correspondance = { 2, 1, 3, 0 };
		for (int i = 0; i < 4; i++) {
			if (!tankEntity.fluidFaceFlowing[correspondance[i]] || stage == LIQUID_STAGES - 1)
				continue;
			int index = 0;
			if (i % 2 != 0) {
				index += tankEntity.isConnected(ForgeDirection.WEST) && tankEntity.getTank(ForgeDirection.WEST).fluidFaceFlowing[correspondance[i]] ? tab[(1 + i) % 2] : 0;
				index += tankEntity.isConnected(ForgeDirection.EAST) && tankEntity.getTank(ForgeDirection.EAST).fluidFaceFlowing[correspondance[i]] ? tab[(0 + i) % 2] : 0;
			}
			else {
				index += tankEntity.isConnected(ForgeDirection.NORTH) && tankEntity.getTank(ForgeDirection.NORTH).fluidFaceFlowing[correspondance[i]] ? tab[(1 + i) % 2] : 0;
				index += tankEntity.isConnected(ForgeDirection.SOUTH) && tankEntity.getTank(ForgeDirection.SOUTH).fluidFaceFlowing[correspondance[i]] ? tab[(0 + i) % 2] : 0;
			}
			index += tankEntity.isConnected(ForgeDirection.UP) && tankEntity.getTank(ForgeDirection.UP).fluidFaceFlowing[correspondance[i]] ? 4 : 0;
			
			GL11.glPushMatrix();
			GL11.glTranslated(0.5f, 0, 0.5f);
			GL11.glRotatef(i * 90, 0, 1f, 0);
			GL11.glTranslated(-0.5f, 0, -0.5f);
			func_110628_a(TextureMap.field_110575_b);
			GL11.glCallList(d.faceFlowing[index][stage]);
			GL11.glPopMatrix();
		}

		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}
}

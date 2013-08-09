package samoht2401.universalwire.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;

public abstract class RenderHelper {

	public static RenderBlocks renderBlocks = new RenderBlocks();
	public static boolean renderXMin = true;
	public static boolean renderXMax = true;
	public static boolean renderYMin = true;
	public static boolean renderYMax = true;
	public static boolean renderZMin = true;
	public static boolean renderZMax = true;

	public static class BlockInterface {

		public double minX;
		public double minY;
		public double minZ;
		public double maxX;
		public double maxY;
		public double maxZ;
		public Block baseBlock = Block.sand;
		public Icon texture = null;

		public Icon getBlockTextureFromSide(int i) {
			if (texture == null)
				return baseBlock.getBlockTextureFromSide(i);
			else
				return texture;
		}

		public float getBlockBrightness(IBlockAccess iblockaccess, int i, int j, int k) {
			return baseBlock.getBlockBrightness(iblockaccess, i, j, k);
		}
	}

	public static void renderBlockInterface(BlockInterface block, IBlockAccess blockAccess, int i, int j, int k,
			boolean renderAll, boolean doLight, boolean doTessellating) {
		float f = 0.5F;
		float f1 = 1.0F;
		float f2 = 0.8F;
		float f3 = 0.6F;

		renderBlocks.renderMaxX = block.maxX;
		renderBlocks.renderMinX = block.minX;
		renderBlocks.renderMaxY = block.maxY;
		renderBlocks.renderMinY = block.minY;
		renderBlocks.renderMaxZ = block.maxZ;
		renderBlocks.renderMinZ = block.minZ;
		renderBlocks.enableAO = false;

		Tessellator tessellator = Tessellator.instance;

		if (doTessellating) {
			tessellator.startDrawingQuads();
		}

		float f4 = 0, f5 = 0;

		if (renderAll || renderYMin) {
			if (doLight) {
				f4 = block.getBlockBrightness(blockAccess, i, j, k);
				f5 = block.getBlockBrightness(blockAccess, i, j, k);
				if (f5 < f4) {
					f5 = f4;
				}
				tessellator.setColorOpaque_F(f * f5, f * f5, f * f5);
			}

			renderBlocks.renderFaceYNeg(null, 0, 0, 0, block.getBlockTextureFromSide(0));
		}

		if (renderAll || renderYMax) {
			if (doLight) {
				f5 = block.getBlockBrightness(blockAccess, i, j, k);
				if (f5 < f4) {
					f5 = f4;
				}
				tessellator.setColorOpaque_F(f1 * f5, f1 * f5, f1 * f5);
			}

			renderBlocks.renderFaceYPos(null, 0, 0, 0, block.getBlockTextureFromSide(1));
		}

		if (renderAll || renderZMin) {
			if (doLight) {
				f5 = block.getBlockBrightness(blockAccess, i, j, k);
				if (f5 < f4) {
					f5 = f4;
				}
				tessellator.setColorOpaque_F(f2 * f5, f2 * f5, f2 * f5);
			}

			renderBlocks.renderFaceZNeg(null, 0, 0, 0, block.getBlockTextureFromSide(2));
		}

		if (renderAll || renderZMax) {
			if (doLight) {
				f5 = block.getBlockBrightness(blockAccess, i, j, k);
				if (f5 < f4) {
					f5 = f4;
				}
				tessellator.setColorOpaque_F(f2 * f5, f2 * f5, f2 * f5);
			}

			renderBlocks.renderFaceZPos(null, 0, 0, 0, block.getBlockTextureFromSide(3));
		}

		if (renderAll || renderXMin) {
			if (doLight) {
				f5 = block.getBlockBrightness(blockAccess, i, j, k);
				if (f5 < f4) {
					f5 = f4;
				}
				tessellator.setColorOpaque_F(f3 * f5, f3 * f5, f3 * f5);
			}

			renderBlocks.renderFaceXNeg(null, 0, 0, 0, block.getBlockTextureFromSide(4));
		}

		if (renderAll || renderXMax) {
			if (doLight) {
				f5 = block.getBlockBrightness(blockAccess, i, j, k);
				if (f5 < f4) {
					f5 = f4;
				}
				tessellator.setColorOpaque_F(f3 * f5, f3 * f5, f3 * f5);
			}

			renderBlocks.renderFaceXPos(null, 0, 0, 0, block.getBlockTextureFromSide(5));
		}

		if (doTessellating) {
			tessellator.draw();
		}
	}
}

package samoht2401.universalwire.render;

import org.lwjgl.opengl.GL11;

import buildcraft.BuildCraftTransport;
import buildcraft.core.utils.Utils;
import buildcraft.transport.ItemPipe;
import samoht2401.universalwire.UniversalWire;
import samoht2401.universalwire.blocks.BlockCable;
import samoht2401.universalwire.model.ModelCable;
import samoht2401.universalwire.tileentity.TileEntityCable;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class UniversalRenderingHandler implements ISimpleBlockRenderingHandler {

	private ModelCable modelCable = new ModelCable();

	public UniversalRenderingHandler() {

	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		if (block instanceof BlockCable)
			renderCableItem(renderer, new ItemStack(block), -0.5f, -0.5f, -0.5f);
	}

	private void renderCableItem(RenderBlocks render, ItemStack item, float translateX, float translateY, float translateZ) {

		Tessellator tessellator = Tessellator.instance;

		Block block = UniversalWire.blockCable;
		Icon icon = UniversalWire.blockCable.genericRenderInfo.textures[BlockCable.CABLEI_TEX_INDEX];

		GL11.glTranslatef(translateX, translateY, translateZ);

		float min = BlockCable.CABLE_MIN_SIZE;
		float max = BlockCable.CABLE_MAX_SIZE;
		float liqOffset = BlockCable.LIQUID_OFFSET;
		float boardOffset = 1f / 16f;
		// Frame
		block.setBlockBounds(min, 0.0F, min, max, 1.0F, max);
		render.setRenderBoundsFromBlock(block);
		tessellatCube(tessellator, block, icon, render, 0D, 0D, 0D);

		block.setBlockBounds(min, boardOffset, min, max, 1.0F - boardOffset, max);
		render.setRenderBoundsFromBlock(block);
		tessellatInvertYFace(tessellator, block, icon, render, 0D, 0D, 0D);
		block.setBlockBounds(min, min, min, max, min + boardOffset, max);
		render.setRenderBoundsFromBlock(block);
		tessellatInvertYFace(tessellator, block, icon, render, 0D, 0D, 0D);
		block.setBlockBounds(min, max, min, max, max - boardOffset, max);
		render.setRenderBoundsFromBlock(block);
		tessellatInvertYFace(tessellator, block, icon, render, 0D, 0D, 0D);
		block.setBlockBounds(min + boardOffset, 0.0f, min, max - boardOffset, 1.0F, max);
		render.setRenderBoundsFromBlock(block);
		tessellatInvertXFace(tessellator, block, icon, render, 0D, 0D, 0D);
		block.setBlockBounds(min, 0.0f, min + boardOffset, max, 1.0F, max - boardOffset);
		render.setRenderBoundsFromBlock(block);
		tessellatInvertZFace(tessellator, block, icon, render, 0D, 0D, 0D);

		// Liquid
		icon = UniversalWire.blockCable.genericRenderInfo.textures[BlockCable.REDL_TEX_INDEX];
		min += liqOffset;
		max -= liqOffset;
		block.setBlockBounds(min, liqOffset, min, max, 1.0F - liqOffset, max);
		render.setRenderBoundsFromBlock(block);
		tessellatCube(tessellator, block, icon, render, 0D, 0D, 0D);

		GL11.glTranslatef(-translateX, -translateY, -translateZ);
		block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		if (block instanceof BlockCable)
			renderCable(world, x, y, z, (BlockCable) block, modelId, renderer);
		return true;
	}

	public void renderCable(IBlockAccess world, int x, int y, int z, BlockCable block, int modelId, RenderBlocks renderer) {
		float min = 0f;
		float max = 1f;
		float boardOffset = 1f / 16f;
		Icon texBody = UniversalWire.blockCable.genericRenderInfo.textures[BlockCable.CABLE_TEX_INDEX];
		Icon texArm = UniversalWire.blockCable.genericRenderInfo.textures[BlockCable.CABLEB_TEX_INDEX];
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityCable) {
			TileEntityCable te = (TileEntityCable) tileEntity;
			float colorRatio = te.getColorRatio() * 0.8f + 0.2f;
			// Center
			min = BlockCable.CABLE_MIN_SIZE;
			max = BlockCable.CABLE_MAX_SIZE;
			te.setCurrentTexture(BlockCable.CABLE_TEX_INDEX);
			block.setBlockBounds(min, min, min, max, max, max);
			renderer.setRenderBoundsFromBlock(block);
			renderer.renderStandardBlock(block, x, y, z);
			// Y
			block.setBlockBounds(min, min, min, max, min + boardOffset, max);
			renderer.setRenderBoundsFromBlock(block);
			renderer.renderFaceYPos(block, x, y, z, texBody);
			block.setBlockBounds(min, max - boardOffset, min, max, max, max);
			renderer.setRenderBoundsFromBlock(block);
			renderer.renderFaceYNeg(block, x, y, z, texBody);
			// X
			block.setBlockBounds(min, min, min, min + boardOffset, max, max);
			renderer.setRenderBoundsFromBlock(block);
			renderer.renderFaceXPos(block, x, y, z, texBody);
			block.setBlockBounds(max - boardOffset, min, min, max, max, max);
			renderer.setRenderBoundsFromBlock(block);
			renderer.renderFaceXNeg(block, x, y, z, texBody);
			// Z
			block.setBlockBounds(min, min, min, max, max, min + boardOffset);
			renderer.setRenderBoundsFromBlock(block);
			renderer.renderFaceZPos(block, x, y, z, texBody);
			block.setBlockBounds(min, min, max - boardOffset, max, max, max);
			renderer.setRenderBoundsFromBlock(block);
			renderer.renderFaceZNeg(block, x, y, z, texBody);
			// Liquid
			min += BlockCable.LIQUID_OFFSET;
			max -= BlockCable.LIQUID_OFFSET;
			te.setCurrentTexture(BlockCable.REDL_TEX_INDEX);
			block.setBlockBounds(min, min, min, max, max, max);
			renderer.setRenderBoundsFromBlock(block);
			renderer.renderStandardBlockWithColorMultiplier(block, x, y, z, colorRatio, colorRatio, colorRatio);

			// North
			if (te.getConnections().contains(ForgeDirection.NORTH)) {
				min = BlockCable.CABLE_MIN_SIZE;
				max = BlockCable.CABLE_MAX_SIZE;
				((TileEntityCable) te).setCurrentTexture(BlockCable.CABLEB_TEX_INDEX);
				block.setBlockBounds(min, min, 0f, max, max, min);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderStandardBlock(block, x, y, z);
				// Y
				block.setBlockBounds(min, min, 0f, max, min + boardOffset, min);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderFaceYPos(block, x, y, z, texArm);
				block.setBlockBounds(min, max - boardOffset, 0f, max, max, min);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderFaceYNeg(block, x, y, z, texArm);
				// X
				block.setBlockBounds(min, min, 0f, min + boardOffset, max, min);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderFaceXPos(block, x, y, z, texBody);
				block.setBlockBounds(max - boardOffset, min, 0f, max, max, min);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderFaceXNeg(block, x, y, z, texBody);
				// Liquid
				min += BlockCable.LIQUID_OFFSET;
				max -= BlockCable.LIQUID_OFFSET;
				te.setCurrentTexture(BlockCable.REDL_TEX_INDEX);
				block.setBlockBounds(min, min, 0f, max, max, min);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderStandardBlockWithColorMultiplier(block, x, y, z, colorRatio, colorRatio, colorRatio);

			}
			// South
			if (te.getConnections().contains(ForgeDirection.SOUTH)) {
				min = BlockCable.CABLE_MIN_SIZE;
				max = BlockCable.CABLE_MAX_SIZE;
				((TileEntityCable) te).setCurrentTexture(BlockCable.CABLEB_TEX_INDEX);
				block.setBlockBounds(min, min, max, max, max, 1.0f);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderStandardBlock(block, x, y, z);
				// Y
				block.setBlockBounds(min, min, max, max, min + boardOffset, 1f);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderFaceYPos(block, x, y, z, texArm);
				block.setBlockBounds(min, max - boardOffset, max, max, max, 1f);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderFaceYNeg(block, x, y, z, texArm);
				// X
				block.setBlockBounds(min, min, max, min + boardOffset, max, 1f);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderFaceXPos(block, x, y, z, texBody);
				block.setBlockBounds(max - boardOffset, min, max, max, max, 1f);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderFaceXNeg(block, x, y, z, texBody);
				// Liquid
				min += BlockCable.LIQUID_OFFSET;
				max -= BlockCable.LIQUID_OFFSET;
				te.setCurrentTexture(BlockCable.REDL_TEX_INDEX);
				block.setBlockBounds(min, min, max, max, max, 1f);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderStandardBlockWithColorMultiplier(block, x, y, z, colorRatio, colorRatio, colorRatio);
			}
			// West
			if (te.getConnections().contains(ForgeDirection.WEST)) {
				min = BlockCable.CABLE_MIN_SIZE;
				max = BlockCable.CABLE_MAX_SIZE;
				((TileEntityCable) te).setCurrentTexture(BlockCable.CABLEB_TEX_INDEX);
				block.setBlockBounds(0f, min, min, min, max, max);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderStandardBlock(block, x, y, z);
				// Y
				block.setBlockBounds(0f, min, min, min, min + boardOffset, max);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderFaceYPos(block, x, y, z, texArm);
				block.setBlockBounds(0f, max - boardOffset, min, min, max, max);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderFaceYNeg(block, x, y, z, texArm);
				// Z
				block.setBlockBounds(0f, min, min, min, max, min + boardOffset);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderFaceZPos(block, x, y, z, texBody);
				block.setBlockBounds(0f, min, max - boardOffset, min, max, max);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderFaceZNeg(block, x, y, z, texBody);
				// Liquid
				min += BlockCable.LIQUID_OFFSET;
				max -= BlockCable.LIQUID_OFFSET;
				te.setCurrentTexture(BlockCable.REDL_TEX_INDEX);
				block.setBlockBounds(0f, min, min, min, max, max);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderStandardBlockWithColorMultiplier(block, x, y, z, colorRatio, colorRatio, colorRatio);
			}
			// East
			if (te.getConnections().contains(ForgeDirection.EAST)) {
				min = BlockCable.CABLE_MIN_SIZE;
				max = BlockCable.CABLE_MAX_SIZE;
				((TileEntityCable) te).setCurrentTexture(BlockCable.CABLEB_TEX_INDEX);
				block.setBlockBounds(max, min, min, 1f, max, max);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderStandardBlock(block, x, y, z);
				// Y
				block.setBlockBounds(max, min, min, 1f, min + boardOffset, max);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderFaceYPos(block, x, y, z, texArm);
				block.setBlockBounds(max, max - boardOffset, min, 1f, max, max);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderFaceYNeg(block, x, y, z, texArm);
				// Z
				block.setBlockBounds(max, min, min, 1f, max, min + boardOffset);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderFaceZPos(block, x, y, z, texBody);
				block.setBlockBounds(max, min, max - boardOffset, 1f, max, max);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderFaceZNeg(block, x, y, z, texBody);
				// Liquid
				min += BlockCable.LIQUID_OFFSET;
				max -= BlockCable.LIQUID_OFFSET;
				te.setCurrentTexture(BlockCable.REDL_TEX_INDEX);
				block.setBlockBounds(max, min, min, 1f, max, max);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderStandardBlockWithColorMultiplier(block, x, y, z, colorRatio, colorRatio, colorRatio);
			}
			// Up
			if (te.getConnections().contains(ForgeDirection.UP)) {
				min = BlockCable.CABLE_MIN_SIZE;
				max = BlockCable.CABLE_MAX_SIZE;
				((TileEntityCable) te).setCurrentTexture(BlockCable.CABLEB_TEX_INDEX);
				block.setBlockBounds(min, max, min, max, 1f, max);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderStandardBlock(block, x, y, z);
				// X
				block.setBlockBounds(min, max, min, min + boardOffset, 1f, max);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderFaceXPos(block, x, y, z, texBody);
				block.setBlockBounds(max - boardOffset, max, min, max, 1f, max);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderFaceXNeg(block, x, y, z, texBody);
				// Z
				block.setBlockBounds(min, max, min, max, 1f, min + boardOffset);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderFaceZPos(block, x, y, z, texBody);
				block.setBlockBounds(min, max, max - boardOffset, max, 1f, max);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderFaceZNeg(block, x, y, z, texBody);
				// Liquid
				min += BlockCable.LIQUID_OFFSET;
				max -= BlockCable.LIQUID_OFFSET;
				te.setCurrentTexture(BlockCable.REDL_TEX_INDEX);
				block.setBlockBounds(min, max, min, max, 1f, max);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderStandardBlockWithColorMultiplier(block, x, y, z, colorRatio, colorRatio, colorRatio);
			}
			// Down
			if (te.getConnections().contains(ForgeDirection.DOWN)) {
				min = BlockCable.CABLE_MIN_SIZE;
				max = BlockCable.CABLE_MAX_SIZE;
				((TileEntityCable) te).setCurrentTexture(BlockCable.CABLEB_TEX_INDEX);
				block.setBlockBounds(min, 0f, min, max, min, max);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderStandardBlock(block, x, y, z);
				// X
				block.setBlockBounds(min, 0f, min, min + boardOffset, min, max);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderFaceXPos(block, x, y, z, texBody);
				block.setBlockBounds(max - boardOffset, 0f, min, max, min, max);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderFaceXNeg(block, x, y, z, texBody);
				// Z
				block.setBlockBounds(min, 0f, min, max, min, min + boardOffset);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderFaceZPos(block, x, y, z, texBody);
				block.setBlockBounds(min, 0f, max - boardOffset, max, min, max);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderFaceZNeg(block, x, y, z, texBody);
				// Liquid
				min += BlockCable.LIQUID_OFFSET;
				max -= BlockCable.LIQUID_OFFSET;
				te.setCurrentTexture(BlockCable.REDL_TEX_INDEX);
				block.setBlockBounds(min, 0f, min, max, min, max);
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderStandardBlockWithColorMultiplier(block, x, y, z, colorRatio, colorRatio, colorRatio);
			}

			block.resetBlockBound();
		}
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

	@Override
	public int getRenderId() {
		return UniversalWire.blockRenderId;
	}

	private void tessellatCube(Tessellator tessellator, Block block, Icon icon, RenderBlocks renderer, double x, double y, double z) {
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1F, 0.0F);
		renderer.renderFaceYNeg(block, x, y, z, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, x, y, z, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1F);
		renderer.renderFaceZNeg(block, x, y, z, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceZPos(block, x, y, z, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(block, x, y, z, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(block, x, y, z, icon);
		tessellator.draw();
	}

	private void tessellatInvertYFace(Tessellator tessellator, Block block, Icon icon, RenderBlocks renderer, double x, double y, double z) {
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1F, 0.0F);
		renderer.renderFaceYNeg(block, x, y, z, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		renderer.renderFaceYPos(block, x, y, z, icon);
		tessellator.draw();
	}

	private void tessellatInvertZFace(Tessellator tessellator, Block block, Icon icon, RenderBlocks renderer, double x, double y, double z) {
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1F);
		renderer.renderFaceZNeg(block, x, y, z, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceZPos(block, x, y, z, icon);
		tessellator.draw();
	}

	private void tessellatInvertXFace(Tessellator tessellator, Block block, Icon icon, RenderBlocks renderer, double x, double y, double z) {
		tessellator.startDrawingQuads();
		tessellator.setNormal(1F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(block, x, y, z, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(block, x, y, z, icon);
		tessellator.draw();
	}

	private void tessellatInvertCube(Tessellator tessellator, Block block, Icon icon, RenderBlocks renderer, double x, double y, double z) {
		tessellatInvertYFace(tessellator, block, icon, renderer, x, y, z);
		tessellatInvertZFace(tessellator, block, icon, renderer, x, y, z);
		tessellatInvertXFace(tessellator, block, icon, renderer, x, y, z);
	}
}

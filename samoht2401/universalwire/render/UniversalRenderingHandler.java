package samoht2401.universalwire.render;

import org.lwjgl.opengl.GL11;

import samoht2401.universalwire.UniversalWire;
import samoht2401.universalwire.blocks.BlockCable;
import samoht2401.universalwire.blocks.BlockTank;
import samoht2401.universalwire.tileentity.TileEntityCable;
import samoht2401.universalwire.tileentity.TileEntityTank;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class UniversalRenderingHandler implements ISimpleBlockRenderingHandler {

	private boolean dYPosXNeg;

	public UniversalRenderingHandler() {

	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		if (block instanceof BlockCable)
			renderCableItem(renderer, new ItemStack(block), -0.5f, -0.5f, -0.5f);
		if (block instanceof BlockTank)
			renderTankItem(renderer, new ItemStack(block), -0.5f, -0.5f, -0.5f);
	}

	private void renderCableItem(RenderBlocks render, ItemStack item, float translateX, float translateY,
			float translateZ) {

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

	private void renderTankItem(RenderBlocks renderer, ItemStack item, float translateX, float translateY,
			float translateZ) {

		Tessellator tessellator = Tessellator.instance;

		Block block = UniversalWire.blockTank;

		GL11.glTranslatef(translateX, translateY, translateZ);

		float min = BlockTank.TANK_MIN;
		float max = BlockTank.TANK_MAX;
		float boardOffset = 1f / 16f;
		float boardLiqOffset = 1f / 16f + 0.001f;
		Icon texColor = BlockTank.colorIcon;
		Icon texGlass = BlockTank.glassIcon;

		// Frame
		Icon icon = texColor;
		// Bottom
		block.setBlockBounds(min, 0f, min, min + boardOffset, boardOffset, max);
		renderer.setRenderBoundsFromBlock(block);
		tessellatCube(tessellator, block, icon, renderer, 0D, 0D, 0D);
		block.setBlockBounds(max - boardOffset, 0f, min, max, boardOffset, max);
		renderer.setRenderBoundsFromBlock(block);
		tessellatCube(tessellator, block, icon, renderer, 0D, 0D, 0D);
		block.setBlockBounds(min, 0f, min, max, boardOffset, min + boardOffset);
		renderer.setRenderBoundsFromBlock(block);
		tessellatCube(tessellator, block, icon, renderer, 0D, 0D, 0D);
		block.setBlockBounds(min, 0f, max - boardOffset, max, boardOffset, max);
		renderer.setRenderBoundsFromBlock(block);
		tessellatCube(tessellator, block, icon, renderer, 0D, 0D, 0D);
		// Top
		block.setBlockBounds(min, 1f - boardOffset, min, min + boardOffset, 1f, max);
		renderer.setRenderBoundsFromBlock(block);
		tessellatCube(tessellator, block, icon, renderer, 0D, 0D, 0D);
		block.setBlockBounds(max - boardOffset, 1f - boardOffset, min, max, 1f, max);
		renderer.setRenderBoundsFromBlock(block);
		tessellatCube(tessellator, block, icon, renderer, 0D, 0D, 0D);
		block.setBlockBounds(min, 1f - boardOffset, min, max, 1f, min + boardOffset);
		renderer.setRenderBoundsFromBlock(block);
		tessellatCube(tessellator, block, icon, renderer, 0D, 0D, 0D);
		block.setBlockBounds(min, 1f - boardOffset, max - boardOffset, max, 1f, max);
		renderer.setRenderBoundsFromBlock(block);
		tessellatCube(tessellator, block, icon, renderer, 0D, 0D, 0D);
		// Side
		block.setBlockBounds(min, 0f, min, min + boardOffset, 1f, min + boardOffset);
		renderer.setRenderBoundsFromBlock(block);
		tessellatCube(tessellator, block, icon, renderer, 0D, 0D, 0D);
		block.setBlockBounds(min, 0f, max - boardOffset, min + boardOffset, 1f, max);
		renderer.setRenderBoundsFromBlock(block);
		tessellatCube(tessellator, block, icon, renderer, 0D, 0D, 0D);
		block.setBlockBounds(max - boardOffset, 0f, min, max, 1f, min + boardOffset);
		renderer.setRenderBoundsFromBlock(block);
		tessellatCube(tessellator, block, icon, renderer, 0D, 0D, 0D);
		block.setBlockBounds(max - boardOffset, 0f, max - boardOffset, max, 1f, max);
		renderer.setRenderBoundsFromBlock(block);
		tessellatCube(tessellator, block, icon, renderer, 0D, 0D, 0D);

		icon = texGlass;
		block.setBlockBounds(min, boardOffset, boardOffset, max, 1 - boardOffset, 1 - boardOffset);
		renderer.setRenderBoundsFromBlock(block);
		tessellatXFace(tessellator, block, icon, renderer, 0D, 0D, 0D);
		block.setBlockBounds(boardOffset, boardOffset, min, 1 - boardOffset, 1 - boardOffset, max);
		renderer.setRenderBoundsFromBlock(block);
		tessellatZFace(tessellator, block, icon, renderer, 0D, 0D, 0D);
		block.setBlockBounds(boardOffset, min, boardOffset, 1 - boardOffset, max, 1 - boardOffset);
		renderer.setRenderBoundsFromBlock(block);
		tessellatYFace(tessellator, block, icon, renderer, 0D, 0D, 0D);

		GL11.glTranslatef(-translateX, -translateY, -translateZ);
		block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
			RenderBlocks renderer) {
		if (block instanceof BlockCable)
			renderCable(world, x, y, z, (BlockCable) block, modelId, renderer);
		if (block instanceof BlockTank)
			renderTank(world, x, y, z, (BlockTank) block, modelId, renderer);
		return true;
	}

	public void renderCable(IBlockAccess world, int x, int y, int z, BlockCable block, int modelId,
			RenderBlocks renderer) {
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

	private void renderTank(IBlockAccess world, int x, int y, int z, BlockTank block, int modelId, RenderBlocks renderer) {
		float min = block.TANK_MIN;
		float max = block.TANK_MAX;
		float boardOffset = 1f / 16f;
		float boardLiqOffset = 1f / 16f + 0.001f;
		Icon texColor = block.colorIcon;
		Icon texGlass = block.glassIcon;

		boolean xNeg = world.getBlockId(x - 1, y, z) == block.blockID;
		boolean xPos = world.getBlockId(x + 1, y, z) == block.blockID;
		boolean yNeg = world.getBlockId(x, y - 1, z) == block.blockID;
		boolean yPos = world.getBlockId(x, y + 1, z) == block.blockID;
		boolean zNeg = world.getBlockId(x, y, z - 1) == block.blockID;
		boolean zPos = world.getBlockId(x, y, z + 1) == block.blockID;
		boolean dXNegZNeg = world.getBlockId(x - 1, y, z - 1) == block.blockID;
		boolean dXNegZPos = world.getBlockId(x - 1, y, z + 1) == block.blockID;
		boolean dXPosZNeg = world.getBlockId(x + 1, y, z - 1) == block.blockID;
		boolean dXPosZPos = world.getBlockId(x + 1, y, z + 1) == block.blockID;
		boolean dYPosXNeg = world.getBlockId(x - 1, y + 1, z) == block.blockID;
		boolean dYPosXPos = world.getBlockId(x + 1, y + 1, z) == block.blockID;
		boolean dYPosZNeg = world.getBlockId(x, y + 1, z - 1) == block.blockID;
		boolean dYPosZPos = world.getBlockId(x, y + 1, z + 1) == block.blockID;
		boolean dYNegXNeg = world.getBlockId(x - 1, y - 1, z) == block.blockID;
		boolean dYNegXPos = world.getBlockId(x + 1, y - 1, z) == block.blockID;
		boolean dYNegZNeg = world.getBlockId(x, y - 1, z - 1) == block.blockID;
		boolean dYNegZPos = world.getBlockId(x, y - 1, z + 1) == block.blockID;

		float xMin = xNeg ? 0f : min;
		float xMax = xPos ? 1f : max;
		float yMin = 0f;
		float yMax = 1f;
		float zMin = zNeg ? 0f : min;
		float zMax = zPos ? 1f : max;

		// Frame
		boolean hasFrameXNegZNeg = false;
		boolean hasFrameXNegZPos = false;
		boolean hasFrameXPosZNeg = false;
		boolean hasFrameXPosZPos = false;
		boolean hasFrameYNegXNeg = false;
		boolean hasFrameYNegXPos = false;
		boolean hasFrameYNegZNeg = false;
		boolean hasFrameYNegZPos = false;
		boolean hasFrameYPosXNeg = false;
		boolean hasFrameYPosXPos = false;
		boolean hasFrameYPosZNeg = false;
		boolean hasFrameYPosZPos = false;
		block.currentIcon = texColor;
		// Bottom
		if ((!yNeg && !xNeg) || (yNeg && xNeg && !dYNegXNeg)) {
			block.setBlockBounds(min, 0f, zMin, min + boardOffset, boardOffset, zMax);
			renderer.setRenderBoundsFromBlock(block);
			renderer.renderStandardBlock(block, x, y, z);
			hasFrameYNegXNeg = true;
		}
		if ((!yNeg && !xPos) || (yNeg && xPos && !dYNegXPos)) {
			block.setBlockBounds(max - boardOffset, 0f, zMin, max, boardOffset, zMax);
			renderer.setRenderBoundsFromBlock(block);
			renderer.renderStandardBlock(block, x, y, z);
			hasFrameYNegXPos = true;
		}
		if ((!yNeg && !zNeg) || (yNeg && zNeg && !dYNegZNeg)) {
			block.setBlockBounds(xMin, 0f, min, xMax, boardOffset, min + boardOffset);
			renderer.setRenderBoundsFromBlock(block);
			renderer.renderStandardBlock(block, x, y, z);
			hasFrameYNegZNeg = true;
		}
		if ((!yNeg && !zPos) || (yNeg && zPos && !dYNegZPos)) {
			block.setBlockBounds(xMin, 0f, max - boardOffset, xMax, boardOffset, max);
			renderer.setRenderBoundsFromBlock(block);
			renderer.renderStandardBlock(block, x, y, z);
			hasFrameYNegZPos = true;
		}
		// Top
		if ((!yPos && !xNeg) || (yPos && xNeg && !dYPosXNeg)) {
			block.setBlockBounds(min, 1f - boardOffset, zMin, min + boardOffset, 1f, zMax);
			renderer.setRenderBoundsFromBlock(block);
			renderer.renderStandardBlock(block, x, y, z);
			hasFrameYPosXNeg = true;
		}
		if ((!yPos && !xPos) || (yPos && xPos && !dYPosXPos)) {
			block.setBlockBounds(max - boardOffset, 1f - boardOffset, zMin, max, 1f, zMax);
			renderer.setRenderBoundsFromBlock(block);
			renderer.renderStandardBlock(block, x, y, z);
			hasFrameYPosXPos = true;
		}
		if ((!yPos && !zNeg) || (yPos && zNeg && !dYPosZNeg)) {
			block.setBlockBounds(xMin, 1f - boardOffset, min, xMax, 1f, min + boardOffset);
			renderer.setRenderBoundsFromBlock(block);
			renderer.renderStandardBlock(block, x, y, z);
			hasFrameYPosZNeg = true;
		}
		if ((!yPos && !zPos) || (yPos && zPos && !dYPosZPos)) {
			block.setBlockBounds(xMin, 1f - boardOffset, max - boardOffset, xMax, 1f, max);
			renderer.setRenderBoundsFromBlock(block);
			renderer.renderStandardBlock(block, x, y, z);
			hasFrameYPosZPos = true;
		}
		// Side
		if ((!xNeg && !zNeg) || (xNeg && zNeg && !dXNegZNeg)) {
			block.setBlockBounds(min, 0f, min, min + boardOffset, 1f, min + boardOffset);
			renderer.setRenderBoundsFromBlock(block);
			renderer.renderStandardBlock(block, x, y, z);
			hasFrameXNegZNeg = true;
		}
		if ((!xNeg && !zPos) || (xNeg && zPos && !dXNegZPos)) {
			block.setBlockBounds(min, 0f, max - boardOffset, min + boardOffset, 1f, max);
			renderer.setRenderBoundsFromBlock(block);
			renderer.renderStandardBlock(block, x, y, z);
			hasFrameXNegZPos = true;
		}
		if ((!xPos && !zNeg) || (xPos && zNeg && !dXPosZNeg)) {
			block.setBlockBounds(max - boardOffset, 0f, min, max, 1f, min + boardOffset);
			renderer.setRenderBoundsFromBlock(block);
			renderer.renderStandardBlock(block, x, y, z);
			hasFrameXPosZNeg = true;
		}
		if ((!xPos && !zPos) || (xPos && zPos && !dXPosZPos)) {
			block.setBlockBounds(max - boardOffset, 0f, max - boardOffset, max, 1f, max);
			renderer.setRenderBoundsFromBlock(block);
			renderer.renderStandardBlock(block, x, y, z);
			hasFrameXPosZPos = true;
		}

		// Glass only on none touching faces
		boolean hasGlassXNeg = false;
		boolean hasGlassXPos = false;
		boolean hasGlassYNeg = false;
		boolean hasGlassYPos = false;
		boolean hasGlassZNeg = false;
		boolean hasGlassZPos = false;
		block.currentIcon = texGlass;
		if (!xNeg) {
			yMin = 0f;
			yMax = 1f;
			zMin = zNeg ? 0f : min;
			zMax = zPos ? 1f : max;
			yMin += hasFrameYNegXNeg ? boardOffset : 0;
			yMax -= hasFrameYPosXNeg ? boardOffset : 0;
			zMin += hasFrameXNegZNeg ? boardOffset : 0;
			zMax -= hasFrameXNegZPos ? boardOffset : 0;
			block.setBlockBounds(min, yMin, zMin, min + boardOffset, yMax, zMax);
			renderer.setRenderBoundsFromBlock(block);
			renderAllFaceExeptAxe(renderer, block, block.currentIcon, x, y, z, 'y', 'z', false);
			hasGlassXNeg = true;
		}
		if (!xPos) {
			yMin = 0f;
			yMax = 1f;
			zMin = zNeg ? 0f : min;
			zMax = zPos ? 1f : max;
			yMin += hasFrameYNegXPos ? boardOffset : 0;
			yMax -= hasFrameYPosXPos ? boardOffset : 0;
			zMin += hasFrameXPosZNeg ? boardOffset : 0;
			zMax -= hasFrameXPosZPos ? boardOffset : 0;
			block.setBlockBounds(max - boardOffset, yMin, zMin, max, yMax, zMax);
			renderer.setRenderBoundsFromBlock(block);
			renderAllFaceExeptAxe(renderer, block, block.currentIcon, x, y, z, 'y', 'z', false);
			hasGlassXPos = true;
		}
		if (!zNeg) {
			xMin = xNeg ? 0f : min;
			xMax = xPos ? 1f : max;
			yMin = 0f;
			yMax = 1f;
			xMin += hasFrameXNegZNeg ? boardOffset : 0;
			xMax -= hasFrameXPosZNeg ? boardOffset : 0;
			yMin += hasFrameYNegZNeg ? boardOffset : 0;
			yMax -= hasFrameYPosZNeg ? boardOffset : 0;
			block.setBlockBounds(xMin, yMin, min, xMax, yMax, min + boardOffset);
			renderer.setRenderBoundsFromBlock(block);
			renderAllFaceExeptAxe(renderer, block, block.currentIcon, x, y, z, 'x', 'y', false);
			hasGlassZNeg = true;
		}
		if (!zPos) {
			xMin = xNeg ? 0f : min;
			xMax = xPos ? 1f : max;
			yMin = 0f;
			yMax = 1f;
			xMin += hasFrameXNegZPos ? boardOffset : 0;
			xMax -= hasFrameXPosZPos ? boardOffset : 0;
			yMin += hasFrameYNegZPos ? boardOffset : 0;
			yMax -= hasFrameYPosZPos ? boardOffset : 0;
			block.setBlockBounds(xMin, yMin, max - boardOffset, xMax, yMax, max);
			renderer.setRenderBoundsFromBlock(block);
			renderAllFaceExeptAxe(renderer, block, block.currentIcon, x, y, z, 'x', 'y', false);
			hasGlassZPos = true;
		}
		if (!yNeg) {
			xMin = xNeg ? 0f : min;
			xMax = xPos ? 1f : max;
			zMin = zNeg ? 0f : min;
			zMax = zPos ? 1f : max;
			xMin += hasFrameYNegXNeg ? boardOffset : 0;
			xMax -= hasFrameYNegXPos ? boardOffset : 0;
			zMin += hasFrameYNegZNeg ? boardOffset : 0;
			zMax -= hasFrameYNegZPos ? boardOffset : 0;
			block.setBlockBounds(xMin, 0f, zMin, xMax, boardOffset, zMax);
			renderer.setRenderBoundsFromBlock(block);
			renderAllFaceExeptAxe(renderer, block, block.currentIcon, x, y, z, 'x', 'z', false);
			hasGlassYNeg = true;
		}
		if (!yPos) {
			xMin = xNeg ? 0f : min;
			xMax = xPos ? 1f : max;
			zMin = zNeg ? 0f : min;
			zMax = zPos ? 1f : max;
			xMin += hasFrameYPosXNeg ? boardOffset : 0;
			xMax -= hasFrameYPosXPos ? boardOffset : 0;
			zMin += hasFrameYPosZNeg ? boardOffset : 0;
			zMax -= hasFrameYPosZPos ? boardOffset : 0;
			block.setBlockBounds(xMin, 1f - boardOffset, zMin, xMax, 1f, zMax);
			renderer.setRenderBoundsFromBlock(block);
			renderAllFaceExeptAxe(renderer, block, block.currentIcon, x, y, z, 'x', 'z', false);
			hasGlassYPos = true;
		}

		// Liquid
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof TileEntityTank) {
			TileEntityTank tile = (TileEntityTank) te;

			int o = 0;
			if (x == -499 && y == 5 && z == -558)
				o = 1;

			if (tile.getFluid() != null) {
				float level = (float) (tile.getFluid().amount) / (float) (BlockTank.TANK_CAPACITY);
				int ldXNeg = tile.isLiquidGoingDown ? tile.fluidFromFace[ForgeDirection.WEST.ordinal() - 2] : 0;
				int ldXPos = tile.isLiquidGoingDown ? tile.fluidFromFace[ForgeDirection.EAST.ordinal() - 2] : 0;
				int ldZNeg = tile.isLiquidGoingDown ? tile.fluidFromFace[ForgeDirection.NORTH.ordinal() - 2] : 0;
				int ldZPos = tile.isLiquidGoingDown ? tile.fluidFromFace[ForgeDirection.SOUTH.ordinal() - 2] : 0;
				int ltXNeg = tile.liquidCommingTop[ForgeDirection.WEST.ordinal() - 2];
				int ltXPos = tile.liquidCommingTop[ForgeDirection.EAST.ordinal() - 2];
				int ltZNeg = tile.liquidCommingTop[ForgeDirection.NORTH.ordinal() - 2];
				int ltZPos = tile.liquidCommingTop[ForgeDirection.SOUTH.ordinal() - 2];
				TileEntityTank below = TileEntityTank.getTankBelow(tile);
				boolean drawFluidLevel = below == null || below.isFull();
				boolean liquidFromTop = false;

				xMin = hasGlassXNeg ? boardLiqOffset : 0f;
				xMax = hasGlassXPos ? 1 - boardLiqOffset : 1f;
				yMin = hasGlassYNeg ? boardLiqOffset : 0f;
				yMax = hasGlassYPos ? 1 - boardLiqOffset : 1f;
				zMin = hasGlassZNeg ? boardLiqOffset : 0f;
				zMax = hasGlassZPos ? 1 - boardLiqOffset : 1f;

				if (ltXNeg > 0) {
					renderLiquidLevel(renderer, world, block, tile, tile.getFluid().getFluid(), x, y, z, 1f,
							boardLiqOffset, yMin, zMin, boardLiqOffset + ltXNeg * 0.001f, 1f, zMax, true);
					liquidFromTop = true;
				}
				if (ltXPos > 0) {
					renderLiquidLevel(renderer, world, block, tile, tile.getFluid().getFluid(), x, y, z, 1f, 1
							- boardLiqOffset - ltXPos * 0.001f, yMin, zMin, 1 - boardLiqOffset, 1f, zMax, true);
					liquidFromTop = true;
				}
				if (ltZNeg > 0) {
					renderLiquidLevel(renderer, world, block, tile, tile.getFluid().getFluid(), x, y, z, 1f, xMin,
							yMin, boardLiqOffset, xMax, 1f, boardLiqOffset + ltZNeg * 0.001f, true);
					liquidFromTop = true;
				}
				if (ltZPos > 0) {
					renderLiquidLevel(renderer, world, block, tile, tile.getFluid().getFluid(), x, y, z, 1f, xMin,
							yMin, 1 - boardLiqOffset - ltZPos * 0.001f, xMax, 1f, 1 - boardLiqOffset, true);
					liquidFromTop = true;
				}
				float toAdd = boardLiqOffset;
				if (ldXNeg > 0 && !liquidFromTop) {
					renderLiquidLevel(renderer, world, block, tile, tile.getFluid().getFluid(), x, y, z, level, xMin,
							yMin, zMin, xMin + ldXNeg * (14f / 16000f - 0.001f) + toAdd, yMax, zMax, true);
				}
				if (ldXPos > 0 && !liquidFromTop) {
					renderLiquidLevel(renderer, world, block, tile, tile.getFluid().getFluid(), x, y, z, level, xMax
							- ldXPos * (14f / 16000f - 0.001f) - toAdd, yMin, zMin, xMax, yMax, zMax, true);
				}
				if (ldZNeg > 0 && !liquidFromTop) {
					renderLiquidLevel(renderer, world, block, tile, tile.getFluid().getFluid(), x, y, z, level, xMin,
							yMin, zMin, xMax, yMax, zMin + ldZNeg * (14f / 16000f - 0.001f) + toAdd, true);
				}
				if (ldZPos > 0 && !liquidFromTop) {
					renderLiquidLevel(renderer, world, block, tile, tile.getFluid().getFluid(), x, y, z, level, xMin,
							yMin, zMax - ldZPos * (14f / 16000f - 0.001f) - toAdd, xMax, yMax, zMax, true);
				}

				te = world.getBlockTileEntity(x, y - 1, z);
				if (drawFluidLevel && level > 0) {
					renderLiquidLevel(renderer, world, block, tile, tile.getFluid().getFluid(), x, y, z, level, xMin,
							yMin, zMin, xMax, yMax, zMax, false);
				}
			}
		}

		block.resetBlockBound(world, x, y, z);
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

	@Override
	public int getRenderId() {
		return UniversalWire.blockRenderId;
	}

	private void tessellatCube(Tessellator tessellator, Block block, Icon icon, RenderBlocks renderer, double x,
			double y, double z) {
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

	private void tessellatInvertYFace(Tessellator tessellator, Block block, Icon icon, RenderBlocks renderer, double x,
			double y, double z) {
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1F, 0.0F);
		renderer.renderFaceYNeg(block, x, y, z, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		renderer.renderFaceYPos(block, x, y, z, icon);
		tessellator.draw();
	}

	private void tessellatInvertZFace(Tessellator tessellator, Block block, Icon icon, RenderBlocks renderer, double x,
			double y, double z) {
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1F);
		renderer.renderFaceZNeg(block, x, y, z, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceZPos(block, x, y, z, icon);
		tessellator.draw();
	}

	private void tessellatInvertXFace(Tessellator tessellator, Block block, Icon icon, RenderBlocks renderer, double x,
			double y, double z) {
		tessellator.startDrawingQuads();
		tessellator.setNormal(1F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(block, x, y, z, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(block, x, y, z, icon);
		tessellator.draw();
	}

	private void tessellatYFace(Tessellator tessellator, Block block, Icon icon, RenderBlocks renderer, double x,
			double y, double z) {
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1F, 0.0F);
		renderer.renderFaceYNeg(block, x, y, z, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, x, y, z, icon);
		tessellator.draw();
	}

	private void tessellatZFace(Tessellator tessellator, Block block, Icon icon, RenderBlocks renderer, double x,
			double y, double z) {
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1F);
		renderer.renderFaceZNeg(block, x, y, z, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceZPos(block, x, y, z, icon);
		tessellator.draw();
	}

	private void tessellatXFace(Tessellator tessellator, Block block, Icon icon, RenderBlocks renderer, double x,
			double y, double z) {
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(block, x, y, z, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(block, x, y, z, icon);
		tessellator.draw();
	}

	private void tessellatInvertCube(Tessellator tessellator, Block block, Icon icon, RenderBlocks renderer, double x,
			double y, double z) {
		tessellatInvertYFace(tessellator, block, icon, renderer, x, y, z);
		tessellatInvertZFace(tessellator, block, icon, renderer, x, y, z);
		tessellatInvertXFace(tessellator, block, icon, renderer, x, y, z);
	}

	private void renderAllFaceExeptAxe(RenderBlocks renderblocks, Block block, Icon icon, int x, int y, int z,
			char axe, boolean twoWay) {
		float minX = (float) block.getBlockBoundsMinX();
		float minY = (float) block.getBlockBoundsMinY();
		float minZ = (float) block.getBlockBoundsMinZ();
		float maxX = (float) block.getBlockBoundsMaxX();
		float maxY = (float) block.getBlockBoundsMaxY();
		float maxZ = (float) block.getBlockBoundsMaxZ();
		if (axe != 'x') {
			if (twoWay) {
				renderTwoWayXFace(renderblocks, block, icon, x, y, z, minY, minZ, maxY, maxZ, minX);
				renderTwoWayXFace(renderblocks, block, icon, x, y, z, minY, minZ, maxY, maxZ, maxX);
			}
			else {
				renderblocks.renderFaceXNeg(block, x, y, z, icon);
				renderblocks.renderFaceXPos(block, x, y, z, icon);
			}
		}
		if (axe != 'y') {
			if (twoWay) {
				renderTwoWayYFace(renderblocks, block, icon, x, y, z, minX, minZ, maxX, maxZ, minY);
				renderTwoWayYFace(renderblocks, block, icon, x, y, z, minX, minZ, maxX, maxZ, maxY);
			}
			else {
				renderblocks.renderFaceYNeg(block, x, y, z, icon);
				renderblocks.renderFaceYPos(block, x, y, z, icon);
			}
		}
		if (axe != 'z') {
			if (twoWay) {
				renderTwoWayZFace(renderblocks, block, icon, x, y, z, minX, minY, maxX, maxY, minZ);
				renderTwoWayZFace(renderblocks, block, icon, x, y, z, minX, minY, maxX, maxY, maxZ);
			}
			else {
				renderblocks.renderFaceZNeg(block, x, y, z, icon);
				renderblocks.renderFaceZPos(block, x, y, z, icon);
			}
		}
	}

	private void renderAllFaceExeptAxe(RenderBlocks renderblocks, Block block, Icon icon, int x, int y, int z,
			char axe1, char axe2, boolean twoWay) {
		float minX = (float) block.getBlockBoundsMinX();
		float minY = (float) block.getBlockBoundsMinY();
		float minZ = (float) block.getBlockBoundsMinZ();
		float maxX = (float) block.getBlockBoundsMaxX();
		float maxY = (float) block.getBlockBoundsMaxY();
		float maxZ = (float) block.getBlockBoundsMaxZ();
		if (axe1 != 'x' && axe2 != 'x') {
			if (twoWay) {
				renderTwoWayXFace(renderblocks, block, icon, x, y, z, minY, minZ, maxY, maxZ, minX);
				renderTwoWayXFace(renderblocks, block, icon, x, y, z, minY, minZ, maxY, maxZ, maxX);
			}
			else {
				renderblocks.renderFaceXNeg(block, x, y, z, icon);
				renderblocks.renderFaceXPos(block, x, y, z, icon);
			}
		}
		if (axe1 != 'y' && axe2 != 'y') {
			if (twoWay) {
				renderTwoWayYFace(renderblocks, block, icon, x, y, z, minX, minZ, maxX, maxZ, minY);
				renderTwoWayYFace(renderblocks, block, icon, x, y, z, minX, minZ, maxX, maxZ, maxY);
			}
			else {
				renderblocks.renderFaceYNeg(block, x, y, z, icon);
				renderblocks.renderFaceYPos(block, x, y, z, icon);
			}
		}
		if (axe1 != 'z' && axe2 != 'z') {
			if (twoWay) {
				renderTwoWayZFace(renderblocks, block, icon, x, y, z, minX, minY, maxX, maxY, minZ);
				renderTwoWayZFace(renderblocks, block, icon, x, y, z, minX, minY, maxX, maxY, maxZ);
			}
			else {
				renderblocks.renderFaceZNeg(block, x, y, z, icon);
				renderblocks.renderFaceZPos(block, x, y, z, icon);
			}
		}
	}

	private void renderTwoWayXFace(RenderBlocks renderblocks, Block block, Icon icon, int xCoord, int yCoord,
			int zCoord, float minY, float minZ, float maxY, float maxZ, float x) {
		block.setBlockBounds(x, minY, minZ, x, maxY, maxZ);
		renderblocks.setRenderBoundsFromBlock(block);
		renderblocks.renderStandardBlock(block, xCoord, yCoord, zCoord);
	}

	private void renderTwoWayYFace(RenderBlocks renderblocks, Block block, Icon icon, int xCoord, int yCoord,
			int zCoord, float minX, float minZ, float maxX, float maxZ, float y) {
		block.setBlockBounds(minX, y, minZ, maxX, y, maxZ);
		renderblocks.setRenderBoundsFromBlock(block);
		renderblocks.renderStandardBlock(block, xCoord, yCoord, zCoord);
	}

	private void renderTwoWayZFace(RenderBlocks renderblocks, Block block, Icon icon, int xCoord, int yCoord,
			int zCoord, float minX, float minY, float maxX, float maxY, float z) {
		block.setBlockBounds(minX, minY, z, maxX, maxY, z);
		renderblocks.setRenderBoundsFromBlock(block);
		renderblocks.renderStandardBlock(block, xCoord, yCoord, zCoord);
	}

	private void renderLiquidLevel(RenderBlocks renderer, IBlockAccess world, Block block, TileEntityTank tile,
			Fluid fluid, int x, int y, int z, float level, float xMin, float yMin, float zMin, float xMax, float yMax,
			float zMax, boolean isFlow) {

		float yMaxToAdd = 1f / 16f + 0.001f;
		TileEntityTank above = TileEntityTank.getTankAbove(tile);
		if (yMax == 1f && level > 0.99f
				&& ((above != null && above.getFluid() != null && above.getFluid().amount != 0) || isFlow))
			yMaxToAdd += 1f / 16f - 0.001f;
		block.setBlockBounds(xMin, yMin, zMin, xMax, (14f / 16f) * level + yMaxToAdd, zMax);
		renderer.setRenderBoundsFromBlock(block);
		boolean doLight = true;
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		float f = 0.5F;
		float f1 = 1.0F;
		float f2 = 0.8F;
		float f3 = 0.6F;
		float f4 = 0, f5 = 0;
		Tessellator tessellator = Tessellator.instance;
		Icon icon = fluid.getStillIcon();
		if (isFlow)
			icon = fluid.getFlowingIcon();

		if (xMin != 0f) {
			if (doLight) {
				f5 = block.getBlockBrightness(world, x, y, z);
				if (f5 < f4) {
					f5 = f4;
				}
				tessellator.setColorOpaque_F(f3 * f5, f3 * f5, f3 * f5);
			}
			renderer.renderFaceXNeg(block, x, y, z, icon);
		}

		if (xMax != 1f) {
			if (doLight) {
				f5 = block.getBlockBrightness(world, x, y, z);
				if (f5 < f4) {
					f5 = f4;
				}
				tessellator.setColorOpaque_F(f3 * f5, f3 * f5, f3 * f5);
			}
			renderer.renderFaceXPos(block, x, y, z, icon);
		}
		if (yMin != 0f) {
			if (doLight) {
				f4 = block.getBlockBrightness(world, x, y, z);
				f5 = block.getBlockBrightness(world, x, y, z);
				if (f5 < f4) {
					f5 = f4;
				}
				tessellator.setColorOpaque_F(f * f5, f * f5, f * f5);
			}
			renderer.renderFaceYNeg(block, x, y, z, icon);
		}
		if (yMax != 1f || level != 1 || yMaxToAdd == 1f / 16f) {
			if (doLight) {
				f5 = block.getBlockBrightness(world, x, y, z);
				if (f5 < f4) {
					f5 = f4;
				}
				tessellator.setColorOpaque_F(f1 * f5, f1 * f5, f1 * f5);
			}
			renderer.renderFaceYPos(block, x, y, z, icon);
		}
		if (zMin != 0f) {
			if (doLight) {
				f5 = block.getBlockBrightness(world, x, y, z);
				if (f5 < f4) {
					f5 = f4;
				}
				tessellator.setColorOpaque_F(f2 * f5, f2 * f5, f2 * f5);
			}
			renderer.renderFaceZNeg(block, x, y, z, icon);
		}
		if (zMax != 1f) {
			if (doLight) {
				f5 = block.getBlockBrightness(world, x, y, z);
				if (f5 < f4) {
					f5 = f4;
				}
				tessellator.setColorOpaque_F(f2 * f5, f2 * f5, f2 * f5);
			}
			renderer.renderFaceZPos(block, x, y, z, icon);
		}
		GL11.glPopAttrib();
	}
}

package samoht2401.universalwire.gui;

import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.LiquidStack;

import org.lwjgl.opengl.GL11;

import samoht2401.universalwire.UniversalWire;
import samoht2401.universalwire.container.ContainerRubberSmelter;
import samoht2401.universalwire.tileentity.TileEntityRubberSmelter;
import samoht2401.universalwire.util.Constantes;

public class GuiRubberSmelter extends GuiContainer {

	TileEntityRubberSmelter tileEntity;

	public GuiRubberSmelter(InventoryPlayer inventoryPlayer, TileEntityRubberSmelter tileEntity) {
		// the container is instanciated and passed to the superclass for
		// handling
		super(new ContainerRubberSmelter(inventoryPlayer, tileEntity));
		this.tileEntity = tileEntity;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		// draw text and stuff here
		// the parameters for drawString are: string, x, y, color
		fontRenderer.drawString("Rubber Smelter", 8, 6, 4210752);
		// draws "Inventory" or your regional equivalent
		fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int j, int k) {
		// draw your Gui here, only thing you need to change is the path
		// int texture = mc.renderEngine.getTexture("/gui/trap.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(Constantes.GUI_DIR + "/rubber_smelter_gui.png");
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

		int sCookTime = this.tileEntity.getScaledCookTime(48);
		if (sCookTime > 0)
			this.drawTexturedModalRect(x + 72, y + 35, 176, 74, sCookTime, 16);
		int sFuelLevel = this.tileEntity.getScaledFuelLevel(12);
		if (tileEntity.currentFuelLevel > 0)
			this.drawTexturedModalRect(x + 56, y + 48 - sFuelLevel, 176, 72 - sFuelLevel, 14, sFuelLevel + 2);

		if (tileEntity.getScaledLiquid(58) > 0)
			displayGauge(x, y, 14, 122, tileEntity.getScaledLiquid(58), tileEntity.getTank(null, null).getLiquid());
	}

	public void displayGauge(int j, int k, int line, int col, int squaled, LiquidStack liquid) {
		if (liquid == null) {
			return;
		}
		int start = 0;

		Icon liquidIcon;
		String textureSheet;
		if (liquid.canonical() != null && liquid.canonical().getRenderingIcon() != null) {
			textureSheet = liquid.canonical().getTextureSheet();
			liquidIcon = liquid.canonical().getRenderingIcon();
		} else {
			if (liquid.itemID < Block.blocksList.length && Block.blocksList[liquid.itemID].blockID > 0) {
				liquidIcon = Block.blocksList[liquid.itemID].getBlockTextureFromSide(0);
				textureSheet = "/terrain.png";
			} else {
				liquidIcon = Item.itemsList[liquid.itemID].getIconFromDamage(liquid.itemMeta);
				textureSheet = "/gui/items.png";
			}
		}
		mc.renderEngine.bindTexture(textureSheet);

		while (true) {
			int x = 0;

			if (squaled > 16) {
				x = 16;
				squaled -= 16;
			} else {
				x = squaled;
				squaled = 0;
			}

			drawTexturedModelRectFromIcon(j + col, k + line + 58 - x - start, liquidIcon, 16, 16 - (16 - x));
			start = start + 16;

			if (x == 0 || squaled == 0) {
				break;
			}
		}

		mc.renderEngine.bindTexture(Constantes.GUI_DIR + "/rubber_smelter_gui.png");
		drawTexturedModalRect(j + col, k + line, 176, 0, 16, 60);
	}
}

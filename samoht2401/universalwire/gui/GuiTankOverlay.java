package samoht2401.universalwire.gui;

import samoht2401.universalwire.UniversalWire;
import samoht2401.universalwire.tileentity.TileEntityTank;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;

public class GuiTankOverlay extends Gui {

	private Minecraft mc;

	public GuiTankOverlay() {
		super();
		this.mc = FMLClientHandler.instance().getClient();
	}

	@ForgeSubscribe(priority = EventPriority.NORMAL)
	public void onRenderExperienceBar(RenderGameOverlayEvent event) {
		if (event.isCanceled() || event.type != ElementType.TEXT)
			return;

		MovingObjectPosition mop = mc.thePlayer.rayTrace(mc.playerController.getBlockReachDistance(),
				event.partialTicks);
		if (mop != null
				&& mc.thePlayer.worldObj.getBlockId(mop.blockX, mop.blockY, mop.blockZ) == UniversalWire.blockTank.blockID) {
			TileEntityTank tileTank = (TileEntityTank) mc.thePlayer.worldObj.getBlockTileEntity(mop.blockX, mop.blockY,
					mop.blockZ);
			Vec3 centerPlan = mc.thePlayer.getPosition(1.0f);
			centerPlan.yCoord += mc.thePlayer.getEyeHeight();
			Vec3 centerTank = Vec3.createVectorHelper(mop.blockX + 0.5, mop.blockY + 0.5, mop.blockZ + 0.5);
			Vec3 contreNormalPlan = mc.thePlayer.getLookVec();
			Vec3 normalPlan = Vec3.createVectorHelper(-contreNormalPlan.xCoord, -contreNormalPlan.yCoord,
					-contreNormalPlan.zCoord);

			Vec3 scalar = centerPlan.subtract(centerTank);
			double dot = scalar.dotProduct(normalPlan);
			Vec3 mult = Vec3.createVectorHelper(normalPlan.xCoord * dot, normalPlan.yCoord * dot, normalPlan.zCoord
					* dot);
			Vec3 result = Vec3.createVectorHelper(scalar.xCoord + mult.xCoord, scalar.yCoord + mult.yCoord,
					scalar.zCoord + mult.zCoord);

			/*
			 * double zoom = 1; double A = 1 / Math.sqrt(2); double B = zoom *
			 * Math.sin(dot) / Math.sqrt(2); double C = zoom * Math.cos(dot);
			 * Vec3 result = Vec3.createVectorHelper(centerPlan.xCoord + A *
			 * (centerTank.xCoord - centerTank.yCoord), centerPlan.yCoord - B *
			 * (centerTank.xCoord + centerTank.yCoord) - C * centerTank.zCoord,
			 * 0);
			 */

			FontRenderer fontRenderer = mc.fontRenderer;
			ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
			drawCenteredString(fontRenderer, tileTank.getFluidAmount() + "mB",
					(int) result.xCoord + (scaledresolution.getScaledWidth() / 2), (int) result.yCoord
							+ (scaledresolution.getScaledHeight() / 2), 0xFFFFFF);
		}
	}
}

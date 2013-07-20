package samoht2401.universalwire.model;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import samoht2401.universalwire.tileentity.TileEntityCable;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.ForgeDirection;

@SideOnly(Side.CLIENT)
public class ModelCable extends ModelBase {

	private ModelRenderer Body;
	private ModelRenderer Liquid;
	private ModelRenderer ArmW;
	private ModelRenderer ArmE;
	private ModelRenderer ArmN;
	private ModelRenderer ArmS;
	private ModelRenderer ArmU;
	private ModelRenderer ArmD;

	public ModelCable() {
		textureWidth = 64;
		textureHeight = 64;

		Body = new ModelRenderer(this, 0, 0);
		Body.addBox(-3F, -3F, -3F, 6, 6, 6);
		Body.setRotationPoint(0F, 0F, 0F);
		Body.setTextureSize(64, 32);
		Body.mirror = true;
		setRotation(Body, 0F, 0F, 0F);
		Liquid = new ModelRenderer(this, 0, 23);
		Liquid.addBox(-2F, -2F, -2F, 4, 4, 4);
		Liquid.setRotationPoint(0F, 0F, 0F);
		Liquid.setTextureSize(64, 32);
		Liquid.mirror = true;
		setRotation(Liquid, 0F, 0F, 0F);
		ArmW = new ModelRenderer(this, 24, 0);
		ArmW.addBox(-8F, -3F, -3F, 5, 6, 6);
		ArmW.setRotationPoint(0F, 0F, 0F);
		ArmW.setTextureSize(64, 32);
		ArmW.mirror = true;
		setRotation(ArmW, 0F, 0F, 0F);
		ArmE = new ModelRenderer(this, 24, 0);
		ArmE.addBox(-8F, -3F, -3F, 5, 6, 6);
		ArmE.setRotationPoint(0F, 0F, 0F);
		ArmE.setTextureSize(64, 32);
		ArmE.mirror = true;
		setRotation(ArmE, 0F, 3.141593F, 0F);
		ArmS = new ModelRenderer(this, 24, 0);
		ArmS.addBox(-8F, -3F, -3F, 5, 6, 6);
		ArmS.setRotationPoint(0F, 0F, 0F);
		ArmS.setTextureSize(64, 32);
		ArmS.mirror = true;
		setRotation(ArmS, 0F, 1.570796F, 0F);
		ArmN = new ModelRenderer(this, 24, 0);
		ArmN.addBox(-8F, -3F, -3F, 5, 6, 6);
		ArmN.setRotationPoint(0F, 0F, 0F);
		ArmN.setTextureSize(64, 32);
		ArmN.mirror = true;
		setRotation(ArmN, 0F, -1.570796F, 0F);
		ArmU = new ModelRenderer(this, 24, 0);
		ArmU.addBox(-8F, -3F, -3F, 5, 6, 6);
		ArmU.setRotationPoint(0F, 0F, 0F);
		ArmU.setTextureSize(64, 32);
		ArmU.mirror = true;
		setRotation(ArmU, 0F, 0F, 1.570796F);
		ArmD = new ModelRenderer(this, 24, 0);
		ArmD.addBox(-8F, -3F, -3F, 5, 6, 6);
		ArmD.setRotationPoint(0F, 0F, 0F);
		ArmD.setTextureSize(64, 32);
		ArmD.mirror = true;
		setRotation(ArmD, 0F, 0F, -1.570796F);

	}

	public void render(TileEntityCable te) {
		FMLClientHandler.instance().getClient().renderEngine.bindTexture("/mods/UniversalWire/textures/blocks/cable.png");
		Body.render(0.0625F);
		Liquid.render(0.0625F);

		if (te != null) {
			ArrayList<ForgeDirection> list = te.getConnections();

			if (list.contains(ForgeDirection.NORTH)) {
				ArmN.render(0.0625F);
			}
			if (list.contains(ForgeDirection.SOUTH)) {
				ArmS.render(0.0625F);
			}
			if (list.contains(ForgeDirection.WEST)) {
				ArmW.render(0.0625F);
			}
			if (list.contains(ForgeDirection.EAST)) {
				ArmE.render(0.0625F);
			}
			if (list.contains(ForgeDirection.UP)) {
				ArmU.render(0.0625F);
			}
			if (list.contains(ForgeDirection.DOWN)) {
				ArmD.render(0.0625F);
			}
		}
	}

	public void render(TileEntityCable box, double x, double y, double z) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5f, (float) y + 0.5f, (float) z + 0.5f);
		// GL11.glScalef(0.5f, 0.5f, 0.5f);

		render(box);
		GL11.glPopMatrix();
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}

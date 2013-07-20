package samoht2401.universalwire.client;

import net.minecraft.network.packet.Packet;
import net.minecraft.src.FMLRenderAccessLibrary;
import net.minecraftforge.client.MinecraftForgeClient;
import samoht2401.universalwire.CommonProxy;
import samoht2401.universalwire.UniversalWire;
import samoht2401.universalwire.render.UniversalRenderingHandler;
import samoht2401.universalwire.tileentity.TileEntityCable;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

	@Override
	public void registerRenderers() {
		UniversalWire.blockRenderId = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(new UniversalRenderingHandler());
		//MinecraftForgeClient.registerItemRenderer(UniversalWire.blockCable.blockID, new TileEntityCableRenderer());
	}
	
	@Override
	public void sendToServer(Packet packet) {
		FMLClientHandler.instance().getClient().getNetHandler().addToSendQueue(packet);
	}
}
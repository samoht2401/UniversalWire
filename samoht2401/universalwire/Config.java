package samoht2401.universalwire;

import java.io.File;

import net.minecraftforge.common.Configuration;

public class Config {
	
	public static int blockRubberSmelterID;
	public static int fluidRubberID;
	public static int blockRubberStillID;
	public static int blockCableID;
	public static int blockTankID;

	public static void getOrCreateConfig(File cfgFile) {
		Configuration config = new Configuration(cfgFile);
		
		try {
			config.load();
			blockRubberSmelterID = config.getBlock("Rubber Smelter ID", 1337).getInt();
			blockRubberStillID = config.getBlock("Rubber Still ID", 2400).getInt();
			blockCableID = config.getBlock("Cable ID", 2401).getInt();
			blockTankID = config.getBlock("Tank ID", 2402).getInt();
			
			fluidRubberID = config.get("fluid", "Fluid Rubber ID", 2400).getInt();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(config.hasChanged()) {
				config.save();
			}
		}
	}
}

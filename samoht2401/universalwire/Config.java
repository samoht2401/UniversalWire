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
			blockCableID = config.getBlock("Cable ID", 2401).getInt();
			blockTankID = config.getBlock("Tank ID", 2402).getInt();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(config.hasChanged()) {
				config.save();
			}
		}
	}
}

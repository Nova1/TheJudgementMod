package TheJudgementMod.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import java.io.File;
import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {

	public static void init(File configFile, Logger logger) {
		Configuration config = new Configuration(configFile);

		try {
			config.load();
			// Place configuration stuff here
		} catch (Exception e) {
			logger.log(
					Level.ERROR,
					"A severe error has occured when attempting to load the config file for this mod!");
		} finally {
			if (config.hasChanged()) {
				config.save();
			}
		}
	}

}

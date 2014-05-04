package TheJudgementMod;

import net.minecraft.creativetab.CreativeTabs;
import TheJudgementMod.util.ConfigHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = TheJudgementMod.modid, name = TheJudgementMod.name, version = TheJudgementMod.version)
public class TheJudgementMod {
	
	public static final String modid = "thejudgementmod";
	public static final String name = "TheJudgementMod";
	public static final String version = "0.1";
	
	public CreativeTabs TJMtab = new TJMtab("Judgement Mod");

	public void preinit(FMLPreInitializationEvent event) {
		ConfigHandler.init(event.getSuggestedConfigurationFile(), event.getModLog());
		TJMBlocks.init(TJMtab);
		TJMItems.inti(TJMtab);
	}

	public void init(FMLInitializationEvent event) {
		
	}

	public void postinit(FMLPostInitializationEvent event) {
		
	}

}

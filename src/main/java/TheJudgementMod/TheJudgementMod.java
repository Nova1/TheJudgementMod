package TheJudgementMod;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatBasic;
import net.minecraft.util.ChatComponentTranslation;
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
    public static StatBase carma = (new StatBasic("stat.carma", new ChatComponentTranslation("stat.carma", new Object[0]))).registerStat();


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

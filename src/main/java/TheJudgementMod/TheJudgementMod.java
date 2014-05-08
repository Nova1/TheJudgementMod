package TheJudgementMod;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatBasic;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import TheJudgementMod.network.packetPipeline;
import TheJudgementMod.network.commands.addKarmaCommand;
import TheJudgementMod.network.commands.setKarmaCommand;
import TheJudgementMod.util.ConfigHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = TheJudgementMod.modid, name = TheJudgementMod.name, version = TheJudgementMod.version)
public class TheJudgementMod {
	@Instance(modid)
	public TheJudgementMod instance;

	public static final String modid = "thejudgementmod";
	public static final String name = "TheJudgementMod";
	public static final String version = "0.1";

	public static final packetPipeline packetpipeline = new packetPipeline();
	public static TJMRecipes TJMRecipes;

	public CreativeTabs TJMtab = new TJMtab("Judgement Mod");

	public void preinit(FMLPreInitializationEvent event) {
		ConfigHandler.init(event.getSuggestedConfigurationFile(), event.getModLog());
		TJMBlocks.init(TJMtab);
		TJMItems.inti(TJMtab);
	}

	public void init(FMLInitializationEvent event) {
		TJMRecipes.addRecipes();
		packetpipeline.initialise();
	}

	public void postinit(FMLPostInitializationEvent event) {
		// I'll add this back once I fix the GuiKarmaBar class or if someone else will.
		// if (FMLCommonHandler.instance().getEffectiveSide().isClient()) MinecraftForge.EVENT_BUS.register(new GuiKarmaBar(Minecraft.getMinecraft()));

		packetpipeline.postInitialise();
	}
	
     @EventHandler
     public void serverStart(FMLServerStartingEvent event) {
          MinecraftServer server = MinecraftServer.getServer();
          ICommandManager command = server.getCommandManager();
          ServerCommandManager manager = (ServerCommandManager) command;
          manager.registerCommand(new setKarmaCommand());
          manager.registerCommand(new addKarmaCommand());
          manager.registerCommand(new setKarmaCommand());
          manager.registerCommand(new setKarmaCommand());
          manager.registerCommand(new setKarmaCommand());
     }


}

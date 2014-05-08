package TheJudgementMod.network.commands;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class addKarmaCommand extends CommandBase {
	
	@Override
	public String getCommandName() {
		return "addkarma";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "adds to a players karma value";
	}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring) {
	}

}

package TheJudgementMod.network.commands;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class setKarmaCommand extends CommandBase {

	@Override
	public String getCommandName() {
		return "setkarma";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "sets a players karma value";
	}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring) {
	}


}

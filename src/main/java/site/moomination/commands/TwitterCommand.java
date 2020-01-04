package site.moomination.commands;

import net.rires.bukkitutils.command.InjectableCommand;
import org.bukkit.command.CommandExecutor;
import site.moomination.Main;

public class TwitterCommand extends InjectableCommand {

  @Override
  public String getName() {
    return "twitter";
  }

  @Override
  public CommandExecutor getExecutor() {
    return ((sender, command, label, args) -> {
      if (Main.getTwitter() == null) {
        sender.sendMessage("Posting the death message to twitter is disabled");
        return true;
      }
      String status;
      Main.postToTwitter = !Main.postToTwitter;
      if (Main.postToTwitter) {
        status = "on";
      } else {
        status = "off";
      }
      sender.sendMessage("Posting the death message to twitter is turned " + status);
      Main.saveTwitterConfig();
      return true;
    });
  }

}

package site.moomination.commands;

import com.google.common.collect.ImmutableList;
import net.rires.bukkitutils.command.InjectableCommand;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import site.moomination.Main;

public class TwitterCommand extends InjectableCommand {

  @Override
  public String getName() {
    return "twitter";
  }

  @Override
  public String getUsage() {
    return "/twitter <on|off>";
  }

  @Override
  public TabCompleter getTabCompleter() {
    return (sender, command, alias, args) -> {
      if (args.length < 2)
        return ImmutableList.of("on", "off");
      return ImmutableList.of();
    };
  }

  @Override
  public CommandExecutor getExecutor() {
    return ((sender, command, label, args) -> {
      if (Main.getTwitter() == null) {
        sender.sendMessage("Posting the death message to twitter is disabled");
        return true;
      }

      if (args.length == 0) {
        Main.postToTwitter = !Main.postToTwitter;
      } else {
        switch (args[0].toLowerCase()) {
          case "on":
            Main.postToTwitter = true;
            break;
          case "off":
            Main.postToTwitter = false;
            break;
          default:
            return false;
        }
      }

      String status;
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

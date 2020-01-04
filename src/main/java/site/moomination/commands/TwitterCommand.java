package site.moomination.commands;

import com.google.common.collect.ImmutableList;
import net.rires.bukkitutils.command.InjectableCommand;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import site.moomination.Main;

import java.util.stream.Collectors;
import java.util.stream.Stream;

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

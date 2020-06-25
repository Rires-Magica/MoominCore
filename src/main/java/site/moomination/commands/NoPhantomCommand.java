package site.moomination.commands;

import com.google.common.collect.Lists;
import net.rires.bukkitutils.command.InjectableCommand;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import site.moomination.Main;

public class NoPhantomCommand extends InjectableCommand {

  @Override
  public String getName() {
    return "nophantom";
  }

  @Override
  public String getUsage() {
    return "/" + getName();
  }

  @Override
  public String getDescription() {
    return "phantom fuck you";
  }

  @Override
  public TabCompleter getTabCompleter() {
    return (sender, command, alias, args) -> Lists.newArrayList("on", "off");
  }

  @Override
  public CommandExecutor getExecutor() {
    return ((sender, command, label, args) -> {
      if (args.length == 0) {
        Main.noPhantom = !Main.noPhantom;
      } else {
        switch (args[0].toLowerCase()) {
          case "on":
            Main.noPhantom = true;
            break;
          case "off":
            Main.noPhantom = false;
            break;
          default:
            return false;
        }
      }

      String status;
      if (Main.noPhantom) {
        status = "on";
      } else {
        status = "off";
      }
      sender.sendMessage("NoPhantom is turned " + status);
      Main.saveNoPhantomConfig();
      return true;
    });
  }

  @Override
  public String getPermission() {
    return "moomination.commands.nophantom";
  }

}

package site.moomination.commands;

import com.google.common.collect.ImmutableList;
import net.rires.bukkitutils.command.InjectableCommand;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import site.moomination.Main;

public class CoordCommand extends InjectableCommand {

  @Override
  public String getName() {
    return "coord";
  }

  @Override
  public String getDescription() {
    return "Show or edit the list of saved coordinates by /here";
  }

  @Override
  public String getUsage() {
    return "/coord (list/remove)";
  }

  @Override
  public TabCompleter getTabCompleter() {
    return (sender, command, alias, args) -> {
      if (args.length < 2)
        return ImmutableList.of("list", "remove");
      return ImmutableList.of();
    };
  }

  @Override
  public CommandExecutor getExecutor() {
    return (sender, command, label, args) -> {
      if (!(sender instanceof Player)) {
        return true;
      }
      if (ArrayUtils.isEmpty(args)) {
        sender.sendMessage("Usage: " + getUsage());
        return true;
      }
      switch (args[0].toLowerCase()) {
        case "list":
          sender.sendMessage(ChatColor.YELLOW + "====" + ChatColor.WHITE + "Saved Coordinates" + ChatColor.YELLOW + "===========");
          Main.getCoordinates().values().forEach(coord -> {
            String line = ChatColor.AQUA + "Name: " +
              ChatColor.WHITE + coord.name +
              ChatColor.AQUA + " xyz: " +
              ChatColor.WHITE + coord.x +
              ChatColor.GRAY + ", " +
              ChatColor.WHITE + coord.y +
              ChatColor.GRAY + ", " +
              ChatColor.WHITE + coord.z +
              ChatColor.DARK_GRAY + " (" +
              coord.playerName + ')';
            sender.sendMessage(line);
          });
          break;
        case "remove":
          if (args.length <= 1) {
            sender.sendMessage("Usage: /coord remove <name>");
            return true;
          }
          String name = args[1].toLowerCase();
          if (!(Main.getCoordinates().containsKey(name))) {
            sender.sendMessage("\"" + name + "\" is not found");
            return true;
          }
          Main.getCoordinates().remove(name);
          Main.saveCoords();
          sender.sendMessage("\"" + name + "\" has been removed");
          break;
      }
      return true;
    };
  }

  @Override
  public String getPermission() {
    return "moomination.commands.coord";
  }

}

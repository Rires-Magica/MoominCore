package site.moomination.commands;

import net.rires.bukkitutils.command.InjectableCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import site.moomination.Main;

public class CoordCommand extends InjectableCommand {

  @Override
  public String getName() {
    return "coord";
  }

  @Override
  public CommandExecutor getExecutor() {
    return (sender, command, label, args) -> {
      if (!(sender instanceof Player)) {
        return true;
      }
      if (args.length == 0) {
        sender.sendMessage("Usage: /coord (list/remove)");
        return true;
      }
      switch (args[0].toLowerCase()) {
        case "list":
          sender.sendMessage(ChatColor.YELLOW + "====" + ChatColor.WHITE + "Saved Coordinates" + ChatColor.YELLOW + "===========");
          Main.coordinates.values().forEach(coord -> {
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
          if (!(Main.coordinates.containsKey(name))) {
            sender.sendMessage("\"" + name + "\" is not found");
            return true;
          }
          Main.coordinates.remove(name);
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

package site.moomination.commands;

import net.rires.bukkitutils.command.InjectableCommand;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import site.moomination.Coord;
import site.moomination.Main;

import java.util.Collections;

public class HereCommand extends InjectableCommand {

  @Override
  public String getName() {
    return "here";
  }

  @Override
  public String getUsage() {
    return "/here <name>";
  }

  @Override
  public String getDescription() {
    return "Save coordinates of your current location with label.";
  }

  @Override
  public TabCompleter getTabCompleter() {
    return (sender, command, alias, args) -> Collections.emptyList();
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
      String name = args[0].toLowerCase();
      if (Main.getCoordinates().containsKey(name)) {
        sender.sendMessage("\"" + name + "\" already exists");
        return true;
      }
      Player player = (Player) sender;
      Location location = player.getLocation();
      int x = location.getBlockX();
      int y = location.getBlockY();
      int z = location.getBlockZ();
      Main.getCoordinates().put(name, new Coord(x, y, z, name, player.getDisplayName()));
      Main.saveCoords();
      sender.sendMessage("Saved as" + name);
      return true;
    };
  }

  @Override
  public String getPermission() {
    return "moomination.commands.here";
  }

}

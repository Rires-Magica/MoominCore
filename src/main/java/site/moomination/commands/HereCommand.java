package site.moomination.commands;

import net.rires.bukkitutils.command.InjectableCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import site.moomination.Coord;
import site.moomination.Main;

public class HereCommand extends InjectableCommand {

  @Override
  public String getName() {
    return "here";
  }

  @Override
  public CommandExecutor getExecutor() {
    return (sender, command, label, args) -> {
      if (!(sender instanceof Player)) {
        return true;
      }
      if (args.length == 0) {
        sender.sendMessage("Usage: /here <name>");
        return true;
      }
      String name = args[0].toLowerCase();
      if (Main.coordinates.containsKey(name)) {
        sender.sendMessage("\"" + name + "\" already exists");
        return true;
      }
      Player player = (Player) sender;
      Location location = player.getLocation();
      int x = location.getBlockX();
      int y = location.getBlockY();
      int z = location.getBlockZ();
      Main.coordinates.put(name, new Coord(x, y, z, name, player.getDisplayName()));
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

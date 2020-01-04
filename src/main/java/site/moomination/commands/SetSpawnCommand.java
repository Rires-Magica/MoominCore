package site.moomination.commands;

import net.rires.bukkitutils.command.InjectableCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand extends InjectableCommand {

  @Override
  public String getName() {
    return "setspawn";
  }

  @Override
  public CommandExecutor getExecutor() {
    return (sender, command, label, args) -> {
      if (sender instanceof ConsoleCommandSender) {
        sender.sendMessage("Cannot execute this command from console");
        return true;
      }
      Player player = (Player) sender;
      Location location = player.getLocation();
      location.getWorld().setSpawnLocation(location);
      sender.sendMessage("Spawn was set to " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
      return true;
    };
  }

  @Override
  public String getPermission() {
    return "moomination.commands.setspawn";
  }

}

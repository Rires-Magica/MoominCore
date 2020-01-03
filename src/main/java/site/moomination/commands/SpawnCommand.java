package site.moomination.commands;

import net.rires.bukkitutils.command.InjectableCommand;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpawnCommand extends InjectableCommand {

  @Override
  public String getName() {
    return "spawn";
  }

  @Override
  public String getDescription() {
    return "Teleport to spawn point";
  }

  @Override
  public String getPermission() {
    return "moomination.commands.spawn";
  }

  @Override
  public TabCompleter getTabCompleter() {
    return (sender, command, alias, args) -> {
      Server server = sender.getServer();
      Stream<String> stream = server.getOnlinePlayers().stream()
        .map(Player::getName);
      if (!ArrayUtils.isEmpty(args)) {
        stream = stream.filter(n -> n.startsWith(args[0]));
      }
      return stream.collect(Collectors.toList());
    };
  }

  @Override
  public CommandExecutor getExecutor() {
    return (sender, command, label, args) -> {
      Player teleportee;
      if (ArrayUtils.isEmpty(args)) {
        if (!(sender instanceof Player)) {
          sender.sendMessage(ChatColor.RED + "Please specify the <Player> to teleport.");
          return true;
        }
        teleportee = (Player) sender;
      } else {
        if (!sender.isOp() && sender.hasPermission("moomination.commands.spawn.teleportother")) {
          sender.sendMessage(Optional.ofNullable(command.getPermissionMessage())
                  .orElseGet(() -> ChatColor.RED + "You don't have permission for use this command."));
          return true;
        }
        String name = String.join(" ", args);
        Player found = sender.getServer().getPlayerExact(name);
        if (found == null) {
          sender.sendMessage(ChatColor.RED + "Player " + name + " is not online or not found.");
          return true;
        }
        teleportee = found;
      }
      assert teleportee != null;
      respawn(sender, teleportee);
      return true;
    };
  }

  private static void respawn(CommandSender sender, Player teleportee) {
    sender.sendMessage(ChatColor.GOLD + "Teleporting...");
    final Location spawn = Stream
      .of(teleportee.getBedSpawnLocation(), teleportee.getWorld().getSpawnLocation())
      .filter(Objects::nonNull)
      .findFirst()
      .orElseGet(teleportee::getLocation);
    assert spawn != null;
    teleportee.teleport(spawn, PlayerTeleportEvent.TeleportCause.COMMAND);
  }

}

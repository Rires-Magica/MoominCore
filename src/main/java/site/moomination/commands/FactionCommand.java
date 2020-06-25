package site.moomination.commands;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.rires.bukkitutils.command.InjectableCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import site.moomination.Main;

import java.util.Map;

public class FactionCommand extends InjectableCommand {

  @Override
  public String getName() {
    return "f";
  }

  @Override
  public String getUsage() {
    return "/" + getName();
  }

  @Override
  public String getDescription() {
    return "set prefix";
  }

  @Override
  public TabCompleter getTabCompleter() {
    return (sender, command, alias, args) -> ImmutableList.of("join", "leave");
  }

  @Override
  public CommandExecutor getExecutor() {
    return ((commandSender, command, label, args) -> {
      if (!(commandSender instanceof Player)) {
        return true;
      }
      if (args.length == 0) {
        commandSender.sendMessage("Usage: /f join <name> or /f leave");
        return true;
      }
      if (args[0].equalsIgnoreCase("join")) {
        if (args.length <= 1) {
          commandSender.sendMessage("Usage: /f join <name>");
          return true;
        }
        if (args[1].length() > 16) {
          commandSender.sendMessage("too long name");
          return true;
        }
        Player player = (Player) commandSender;
        String uuid = player.getUniqueId().toString().replaceAll("-", "");
        Map<String, String> prefixes = Main.getPrefixes();
        if (prefixes.containsKey(uuid)) {
          commandSender.sendMessage(ChatColor.DARK_GRAY + prefixes.get(uuid) + " → " + args[1]);
        } else {
          commandSender.sendMessage(ChatColor.DARK_GRAY + "Joined" + args[1]);
        }
        prefixes.put(uuid, args[1]);
        Main.savePrefixes();
      } else if (args[0].equalsIgnoreCase("leave")) {
        Player player = (Player) commandSender;
        String uuid = player.getUniqueId().toString().replaceAll("-", "");
        Map<String, String> prefixes = Main.getPrefixes();
        if (prefixes.containsKey(uuid)) {
          prefixes.remove(uuid);
        } else {
          commandSender.sendMessage("fuck you");
        }
        Main.savePrefixes();
      } else {
        commandSender.sendMessage("Usage: /f join <name> or /f leave");
      }
      return true;
    });
  }

  @Override
  public String getPermission() {
    return "moomination.commands.faction";
  }

}

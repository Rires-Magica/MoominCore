package site.moomination.commands;

import net.rires.bukkitutils.command.InjectableCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.stream.Stream;

public class MemoryCommand extends InjectableCommand {

  @Override
  public String getName() {
    return "memory";
  }

  @Override
  public String getUsage() {
    return "/" + getName();
  }

  @Override
  public String getDescription() {
    return "Shows about current JVM heap and TPS (tick per second).";
  }

  @Override
  public TabCompleter getTabCompleter() {
    return (sender, command, alias, args) -> Collections.emptyList();
  }

  @Override
  public CommandExecutor getExecutor() {
    return ((sender, command, label, args) -> {
      Runtime runtime = Runtime.getRuntime();
      double[] tps = Bukkit.getServer().getTPS();
      Stream.of(
        "Memory Max: " + (runtime.maxMemory() / 1024 / 1024) + "MB",
        "Memory Total: " + (runtime.totalMemory() / 1024 / 1024) + "MB",
        "Memory Used: " + ((runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024) + "MB",
        "Memory Free: " + (runtime.freeMemory() / 1024 / 1024) + "MB",
        String.format("TPS (1m, 5m, 15m): %.2f, %.2f, %.2f", tps[0], tps[1], tps[2])
      ).forEach(sender::sendMessage);
      return true;
    });
  }

  @Override
  public String getPermission() {
    return "moomination.commands.memory";
  }

}

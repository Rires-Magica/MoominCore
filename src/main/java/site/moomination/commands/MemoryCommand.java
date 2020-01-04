package site.moomination.commands;

import net.rires.bukkitutils.command.InjectableCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;

import java.util.stream.Stream;

public class MemoryCommand extends InjectableCommand {

  @Override
  public String getName() {
    return "memory";
  }

  @Override
  public CommandExecutor getExecutor() {
    return ((sender, command, label, args) -> {
      Runtime runtime = Runtime.getRuntime();
      double[] tps = Bukkit.getServer().getTPS();
      Stream.of(
        "Memory Max: " + runtime.maxMemory(),
        "Memory Total: " + runtime.totalMemory(),
        "Memory Used: " + (runtime.totalMemory() - runtime.freeMemory()),
        "Memory Free: " + runtime.freeMemory(),
        "TPS (1m, 5m, 15m): " + tps[0] + ", " + tps[1] + ", " + tps[2]
      ).forEach(sender::sendMessage);
      return true;
    });
  }

  @Override
  public String getPermission() {
    return "moomination.commands.memory";
  }

}

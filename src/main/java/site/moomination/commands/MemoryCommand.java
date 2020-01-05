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

  public enum SizeUnit {
    B(1L),
    KiB(1024L),
    MiB(1024L * 1024L),
    GiB(1024L * 1024L * 1024L),
    TiB(1024L * 1024L * 1024L * 1024L),
    PiB(1024L * 1024L * 1024L * 1024L * 1024L),
    EiB(1024L * 1024L * 1024L * 1024L * 1024L * 1024L);

    public static String toString(long value) {
      SizeUnit determined, prev;
      determined = prev = SizeUnit.B;
      for (SizeUnit unit : values()) {
        if (value < unit.multiplier) {
          determined = prev;
          break;
        }
        prev = unit;
      }
      return determined.format(value);
    }

    private final long multiplier;

    SizeUnit(long multiplier) {
      this.multiplier = multiplier;
    }

    @Override
    public String toString() {
      return this == KiB ? "kiB" : name();
    }

    public String format(long value) {
      return String.format("%d %s", value / multiplier, toString());
    }

  }

  @Override
  public CommandExecutor getExecutor() {
    return ((sender, command, label, args) -> {
      Runtime runtime = Runtime.getRuntime();
      double[] tps = Bukkit.getServer().getTPS();

      long maxMemory = runtime.maxMemory();
      long totalMemory = runtime.totalMemory();
      long freeMemory = runtime.freeMemory();
      long usedMemory = totalMemory - freeMemory;

      String usedPercentage = String.format("%.2f", (double) usedMemory / maxMemory * 100);

      Stream.of(
        "Memory Max: " + SizeUnit.toString(maxMemory),
        "Memory Total: " + SizeUnit.toString(totalMemory),
        "Memory Used: " + SizeUnit.toString(usedMemory) + " (" + usedPercentage + "%)",
        "Memory Free: " + SizeUnit.toString(freeMemory),
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

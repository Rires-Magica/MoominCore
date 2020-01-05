package site.moomination.commands;

import net.rires.bukkitutils.command.InjectableCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

      double totalPercentage = (double) totalMemory / maxMemory * 100;
      double usedPercentage = (double) usedMemory / maxMemory * 100;

      Stream.of(
        String.format("%sHeap Limit: %s%s",
          ChatColor.GOLD,
          ChatColor.GREEN,
          SizeUnit.toString(maxMemory)),
        String.format("%sHeap Allocated: %s%s (%.2f%%)",
          ChatColor.GOLD,
          colorizePercentage(totalPercentage), SizeUnit.toString(totalMemory), totalPercentage),
        String.format("%sHeap Free: %s%s",
          ChatColor.GOLD,
          colorizePercentageReversed((double) freeMemory / totalMemory * 100),
          SizeUnit.toString(freeMemory)),
        String.format("%sHeap Used: %s%s (%.2f%%)",
          ChatColor.GOLD,
          colorizePercentage(usedPercentage), SizeUnit.toString(usedMemory), usedPercentage),
        String.format("%sTPS (1m, 5m, 15m): %s%.2f, %s%.2f, %s%.2f",
          ChatColor.GOLD,
          colorizeTPS(tps[0]), tps[0], colorizeTPS(tps[1]), tps[1], colorizeTPS(tps[2]), tps[2])
      ).forEach(sender::sendMessage);
      return true;
    });
  }

  private static ChatColor colorizePercentage(double percentage) {
    if (Double.compare(percentage, 30) < 0) {
      return ChatColor.GREEN;
    }
    if (Double.compare(percentage, 80) < 0) {
      return ChatColor.YELLOW;
    }
    return ChatColor.RED;
  }

  // TODO: fix long name
  private static ChatColor colorizePercentageReversed(double percentage) {
    ChatColor r = colorizePercentage(percentage);
    return r == ChatColor.GREEN ? ChatColor.RED : r == ChatColor.RED ? ChatColor.GREEN : r;
  }

  private static ChatColor colorizeTPS(double tps) {
    if (Double.compare(tps, 18) > 0) {
      return ChatColor.GREEN;
    }
    if (Double.compare(tps, 15) > 0) {
      return ChatColor.YELLOW;
    }
    return ChatColor.RED;
  }

  @Override
  public String getPermission() {
    return "moomination.commands.memory";
  }

}

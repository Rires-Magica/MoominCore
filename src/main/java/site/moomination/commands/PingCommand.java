package site.moomination.commands;

import com.google.common.collect.ImmutableList;
import net.rires.bukkitutils.command.InjectableCommand;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import site.moomination.Main;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PingCommand extends InjectableCommand {

  private static final int GOOD = 40;
  private static final int AVERAGE = 90;
  private static final int POOR = 160;

  private static final Lock GET_HANDLE_LOCK = new ReentrantLock(false);
  private static final Lock PING_LOCK = new ReentrantLock(false);

  private static volatile Method GET_HANDLE;
  private static volatile Field PING;

  private static void initReflectiveData(Player instance) {
    if (GET_HANDLE == null) {
      try {
        GET_HANDLE_LOCK.lock();
        // double-checked locking
        if (GET_HANDLE == null)
          GET_HANDLE = instance.getClass().getMethod("getHandle");
      } catch (NoSuchMethodException exception) {
        throw new RuntimeException(exception);
      } finally {
        GET_HANDLE_LOCK.unlock();
      }
    }
    if (PING == null) {
      try {
        PING_LOCK.lock();
        // double-checked locking
        if (PING == null)
          PING = getHandle(instance).getClass().getField("ping");
      } catch (NoSuchFieldException exception) {
        throw new RuntimeException(exception);
      } finally {
        PING_LOCK.unlock();
      }
    }
  }

  private static Object getHandle(Player player) {
    boolean accessible = GET_HANDLE.isAccessible();
    try {
      return GET_HANDLE.invoke(player);
    } catch (IllegalAccessException | InvocationTargetException exception) {
      throw new RuntimeException(exception);
    } finally {
      GET_HANDLE.setAccessible(accessible);
    }
  }

  private static long getPing(Object handle) {
    boolean accessible = PING.isAccessible();
    try {
      return PING.getLong(handle);
    } catch (IllegalAccessException exception) {
      throw new RuntimeException(exception);
    } finally {
      PING.setAccessible(accessible);
    }
  }

  @Override
  public String getName() {
    return "ping";
  }

  @Override
  public String getDescription() {
    return "Shows your network latency";
  }

  @Override
  public String getPermission() {
    return "moomination.commands.ping";
  }

  @Override
  public String getUsage() {
    return "/ping or /ping <Player>";
  }

  @Override
  public TabCompleter getTabCompleter() {
    return (sender, command, alias, args) -> {
      if (!hasPermission(sender))
        return ImmutableList.of(sender.getName());
      if (args.length >= 2)
        return Collections.emptyList();
      Stream<String> stream = sender.getServer()
        .getOnlinePlayers().stream().map(Player::getName);
      if (!ArrayUtils.isEmpty(args)) {
        stream = stream.filter(n -> n.startsWith(args[0]));
      }
      return stream.collect(Collectors.toList());
    };
  }

  @Override
  public CommandExecutor getExecutor() {
    return (sender, command, label, args) -> {
      final Player target;
      if (ArrayUtils.isEmpty(args)) {
        if (!(sender instanceof Player)) {
          sender.sendMessage(ChatColor.RED + "Please specify the <Player>.");
          return true;
        }
        target = (Player) sender;
      } else {
        String name = String.join(" ", args);
        Player found = sender.getServer().getPlayerExact(name);
        if (found == null) {
          sender.sendMessage(ChatColor.RED + "Player " + name + " is not online or not found.");
          return true;
        }
        if (!hasPermission(sender) && !Objects.equals(sender, found)) {
          sender.sendMessage(Optional.ofNullable(command.getPermissionMessage())
            .orElseGet(() -> ChatColor.RED + "You don't have permission for use this command."));
          return true;
        }
        target = found;
      }
      assert target != null;
      sender.getServer().getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
        long ping = ping(target);
        sender.sendMessage(ChatColor.YELLOW + target.getName() + "'s ping: " + colorize(ping) + ping);
      });
      return true;
    };
  }

  private static long ping(Player teleportee) {
    initReflectiveData(teleportee);
    return getPing(getHandle(teleportee));
  }

  private static ChatColor colorize(long ping) {
    if (ping <= GOOD)
      return ChatColor.GREEN;
    if (ping <= AVERAGE)
      return ChatColor.YELLOW;
    if (ping <= POOR)
      return ChatColor.GOLD;
    return ChatColor.RED;
  }

  private static boolean hasPermission(CommandSender sender) {
    return sender.isOp() || sender.hasPermission("moomination.commands.ping.others");
  }

}

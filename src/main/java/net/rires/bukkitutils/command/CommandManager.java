package net.rires.bukkitutils.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class CommandManager {

  public static void inject(Plugin plugin, InjectableCommand... commands) {
    inject(plugin.getName(), plugin, commands);
  }

  public static void inject(String prefix, Plugin plugin, InjectableCommand... commands) {
    CommandMap commandMap = getCommandMap();
    for (InjectableCommand command : commands) {
      if (command.getName() == null || command.getExecutor() == null) {
        Bukkit.getServer().getLogger().severe("Could not register command " + command.getName() + " for plugin " + plugin.getName() + ": CommandName or CommandExecutor cannot be null");
        continue;
      }
      if (command.getName().contains(":")) {
        Bukkit.getServer().getLogger().severe("Could not register command " + command.getName() + " for plugin " + plugin.getName() + ": CommandName cannot contain \":\"");
        continue;
      }
      PluginCommand _command = getPluginCommand(command.getName(), plugin);
      _command.setExecutor(command.getExecutor());
      _command.setPermission(command.getPermission());
      _command.setPermissionMessage(command.getPermissionMessage());
      _command.setTabCompleter(command.getTabCompleter());
      if (command.getUsage() != null)
        _command.setUsage(command.getUsage());
      if (command.getDescription() != null)
        _command.setDescription(command.getDescription());
      if (command.getAliases() != null)
        _command.setAliases(command.getAliases());
      commandMap.register(prefix, _command);
    }
  }

  private static CommandMap getCommandMap() {
    if (!(Bukkit.getPluginManager() instanceof SimplePluginManager))
      throw new IllegalStateException("PluginManager instance is not SimplePluginManager");
    try {
      Field field = SimplePluginManager.class.getDeclaredField("commandMap");
      field.setAccessible(true);
      return (SimpleCommandMap) field.get(Bukkit.getPluginManager());
    } catch (IllegalAccessException | NoSuchFieldException excepted) {
      excepted.printStackTrace();
    }
    return null;
  }

  private static PluginCommand getPluginCommand(String name, Plugin plugin) {
    try {
      Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
      constructor.setAccessible(true);
      return constructor.newInstance(name, plugin);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException excepted) {
      excepted.printStackTrace();
    }
    return null;
  }

}

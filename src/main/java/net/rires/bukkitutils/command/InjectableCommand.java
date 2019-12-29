package net.rires.bukkitutils.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

import java.util.List;

public abstract class InjectableCommand {

  public abstract String getName();

  public abstract CommandExecutor getExecutor();

  public String getDescription() {
    return null;
  }

  public List<String> getAliases() {
    return null;
  }

  public String getPermission() {
    return null;
  }

  public String getPermissionMessage() {
    return null;
  }

  public TabCompleter getTabCompleter() {
    return null;
  }

}

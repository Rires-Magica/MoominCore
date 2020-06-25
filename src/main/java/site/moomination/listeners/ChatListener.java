package site.moomination.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import site.moomination.Main;

public class ChatListener implements Listener {

  @EventHandler
  public void onChat(AsyncPlayerChatEvent event) {
    String uuid = event.getPlayer().getUniqueId().toString().replaceAll("-", "");
    if (Main.getPrefixes().containsKey(uuid)) {
      event.setFormat(ChatColor.RED + "[" + Main.getPrefixes().get(uuid) + "]" + ChatColor.WHITE + "%s: %s");
    } else {
      event.setFormat(ChatColor.RED + "[-]" + ChatColor.WHITE + "%s: %s");
    }
  }

}

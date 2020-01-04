package site.moomination.listeners;

import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import site.moomination.Main;
import twitter4j.TwitterException;

public class PlayerDeathListener implements Listener {

  @EventHandler
  public void onDeath(PlayerDeathEvent event) {
    Player player = event.getEntity();
    Location location = player.getLocation();
    /*
    LocalDateTime time = LocalDateTime.now(ZoneId.of("Asia/Tokyo"));
    String message =
      ChatColor.GRAY + "(x: " + location.getBlockX() + ", y: " + location.getBlockY() +
        ", z: " + location.getBlockZ() + ", dimension: " + location.getWorld().getName() +
        ", time: " + time.getHour() + ":" + time.getMinute() + ":" + time.getSecond() + ")";
     */
    int killCount = player.getStatistic(Statistic.PLAYER_KILLS);
    String deathMessage = "";
    String cause = ChatColor.stripColor(event.getDeathMessage().replace(player.getName(), "").trim());
    if (player.getKiller() != null && cause.contains(player.getKiller().getDisplayName())) {
      int killerKillCount = player.getKiller().getStatistic(Statistic.PLAYER_KILLS);
      cause = cause.replace(player.getKiller().getDisplayName(), "").trim();
      deathMessage = " " + ChatColor.RED + player.getKiller().getDisplayName() + ChatColor.DARK_RED + "[" + killerKillCount + "]";
      ItemStack itemInMainHand = player.getKiller().getInventory().getItemInMainHand();
      if (itemInMainHand.getType() != Material.AIR) {
        String weaponName;
        if (itemInMainHand.getItemMeta().hasDisplayName()) {
          String displayName = itemInMainHand.getItemMeta().getDisplayName();
          if (cause.contains(displayName)) {
            cause = cause.substring(0, cause.length() - 10 - displayName.length());
          }
          weaponName = displayName;
        } else {
          weaponName = WordUtils.capitalize(itemInMainHand.getType().name().replace("_", " ").toLowerCase());
        }
        deathMessage = deathMessage + ChatColor.YELLOW + " using " + ChatColor.RED + weaponName + ChatColor.YELLOW;
      }
    }
    deathMessage = ChatColor.RED + player.getDisplayName() + ChatColor.DARK_RED + "[" + killCount + "] " + ChatColor.YELLOW + cause + deathMessage;
    event.setDeathMessage(deathMessage + ".");
    String finalDeathMessage = deathMessage;
    location.getWorld().strikeLightningEffect(location);
    if (Main.getTwitter() == null) return;
    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
      try {
        Main.getTwitter().updateStatus("RIP: " + ChatColor.stripColor(finalDeathMessage)
          .replace("@", "(at)").replace("#", "(hash)").replace(".", ",") + ".");
        Bukkit.getLogger().info("Successfully sent the death message");
      } catch (TwitterException e) {
        e.printStackTrace();
      }
    });
    // player.sendMessage(message);
  }

}

package site.moomination.listeners;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.ProjectileSource;
import site.moomination.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BossDamageListener implements Listener {

  private static final String KEY_DAMAGES = "damages";

  @EventHandler
  public void onBossDamagedByEntity(EntityDamageByEntityEvent event) {
    if (event.getEntityType() != EntityType.WITHER && event.getEntityType() != EntityType.ENDER_DRAGON) return;
    Entity victim = event.getEntity();
    Player player;
    if (event.getDamager() instanceof Player) {
      // プレイヤーの直接攻撃
      player = (Player) event.getDamager();
    } else if (event.getDamager() instanceof Projectile) {
      ProjectileSource shooter = ((Projectile) event.getDamager()).getShooter();
      if (shooter instanceof Player) {
        // プレイヤーの飛び道具
        player = (Player) shooter;
      } else {
        // プレイヤー以外の飛び道具
        return;
      }
    } else {
      // プレイヤーが絡まない要因
      return;
    }
    HashMap<Player, Integer> damages;
      if (victim.hasMetadata(KEY_DAMAGES)) {
        try {
          damages = (HashMap<Player, Integer>) victim.getMetadata(KEY_DAMAGES).get(0).value();
        } catch (Exception ex) {
          ex.printStackTrace();
          return;
        }
        if (damages.containsKey(player)) {
          damages.put(player, damages.get(player) + (int) Math.round(event.getFinalDamage()));
        } else {
          damages.put(player, (int) Math.round(event.getFinalDamage()));
        }
      } else {
        damages = new HashMap<>();
        damages.put(player, (int) Math.round(event.getFinalDamage()));
        victim.setMetadata(KEY_DAMAGES, new FixedMetadataValue(Main.getInstance(), damages));
      }
  }

  @EventHandler
  public void onBossSlain(EntityDeathEvent event) {
    if (event.getEntityType() != EntityType.WITHER && event.getEntityType() != EntityType.ENDER_DRAGON) return;
    if (!event.getEntity().hasMetadata(KEY_DAMAGES)) return;
    Entity victim = event.getEntity();
    HashMap<Player, Integer> damages;
    try {
      damages = (HashMap<Player, Integer>) victim.getMetadata(KEY_DAMAGES).get(0).value();
    } catch (Exception ex) {
      ex.printStackTrace();
      return;
    }
    ArrayList<String> lines = new ArrayList<>();
    int[] place = { 0 };
    lines.add("== らんきんぐ ==");
    damages.entrySet().stream().sorted(Map.Entry.<Player, Integer>comparingByValue().reversed()).forEach(set -> {
      place[0]++;
      lines.add(place[0] + ". " + set.getKey().getName() + " (" + set.getValue() + ")");
    });
    lines.forEach(Bukkit::broadcastMessage);
  }

}

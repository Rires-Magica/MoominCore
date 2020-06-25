package site.moomination.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import site.moomination.Main;

public class MobSpawnListener implements Listener {

  @EventHandler
  public void onPhantomSpawned(EntitySpawnEvent event) {
    if (event.getEntityType() == EntityType.PHANTOM && Main.noPhantom) {
      event.setCancelled(true);
    }
  }

}

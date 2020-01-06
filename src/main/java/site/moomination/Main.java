package site.moomination;

import net.rires.bukkitutils.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import site.moomination.commands.*;
import site.moomination.listeners.BossDamageListener;
import site.moomination.listeners.ChatListener;
import site.moomination.listeners.PlayerDeathListener;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Map;
import java.util.stream.Stream;

public final class Main extends JavaPlugin {

  static {
    ConfigurationSerialization.registerClass(Coord.class, "Coord");
  }

  private static Map<String, Coord> coordinates;

  private static Main instance;

  private static Twitter twitter;

  public static boolean postToTwitter;

  @Override
  public void onEnable() {
    // Plugin startup logic
    instance = this;
    saveDefaultConfig();

    coordinates = (Map) getConfig().getConfigurationSection("coordinates").getValues(false);
    Bukkit.getLogger().info("Successfully loaded saved coordinates");

    CommandManager.inject(this.getName(), this,
      new HereCommand(), new CoordCommand(), new SetSpawnCommand(), new MemoryCommand(),
      new TwitterCommand(), new SpawnCommand(), new PingCommand());
    Bukkit.getLogger().info("Successfully injected commands");

    registerListeners(new ChatListener(), new PlayerDeathListener(), new BossDamageListener());
    Bukkit.getLogger().info("Successfully registered listeners");

    ConfigurationSection twitterConfig = getConfig().getConfigurationSection("twitter");
    if (twitterConfig.getBoolean("enabled")) {
      ConfigurationBuilder cb = new ConfigurationBuilder();
      cb.setOAuthConsumerKey(twitterConfig.getString("consumerkey"))
        .setOAuthConsumerSecret(twitterConfig.getString("consumersecret"))
        .setOAuthAccessToken(twitterConfig.getString("accesstoken"))
        .setOAuthAccessTokenSecret(twitterConfig.getString("accesstokensecret"));
      twitter = new TwitterFactory(cb.build()).getInstance();
      postToTwitter = twitterConfig.getBoolean("deathmessage");
      Bukkit.getLogger().info("Successfully initialized twitter4j");
    } else {
      twitter = null;
      Bukkit.getLogger().info("Skipped twitter4j initialization");
      postToTwitter = false;
    }

    Bukkit.getLogger().info("Completed plugin initialization process");
  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
    saveCoords();
    coordinates = null;
    instance = null;
  }

  private void registerListeners(Listener... listeners) {
    Stream.of(listeners).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
  }

  public static void saveCoords() {
    instance.getConfig().createSection("coordinates", coordinates);
    instance.saveConfig();
  }

  public static void saveTwitterConfig() {
    instance.getConfig().set("twitter.deathmessage", postToTwitter);
    instance.saveConfig();
  }

  public static Main getInstance() {
    return instance;
  }

  public static Twitter getTwitter() {
    return twitter;
  }

  public static Map<String, Coord> getCoordinates() {
    return coordinates;
  }

}

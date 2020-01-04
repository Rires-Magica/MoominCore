package site.moomination;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.LinkedHashMap;
import java.util.Map;

public class Coord implements ConfigurationSerializable {

  public Coord(int x, int y, int z, String name, String playerName) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.name = name;
    this.playerName = playerName;
  }

  public int x;
  public int y;
  public int z;
  public String name;
  public String playerName;

  @Override
  public Map<String, Object> serialize() {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("x", x);
    map.put("y", y);
    map.put("z", z);
    map.put("name", name);
    map.put("playername", playerName);
    return map;
  }

  public static Coord deserialize(Map<String, Object> args) {
    int x = (Integer) args.get("x");
    int y = (Integer) args.get("y");
    int z = (Integer) args.get("z");
    String name = args.get("name").toString();
    String playerName = args.get("playername").toString();
    return new Coord(x, y, z, name, playerName);
  }

}

package tv.ingoh.minecraft.plugins.resourcechickens;

import org.bukkit.plugin.java.JavaPlugin;

public class Config {

    int min;
    int max;
    int radius;
    JavaPlugin plugin;
    int defaultDimension;
    boolean minecord;

    public Config(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        load();
    }

	public int getRandomDelay() {
        double rng = Math.random();
		return (int) (rng * (max - min) + min);
    }
    
    public int getRadius() {
        return radius;
	}

	public void save() {
        plugin.getConfig().set("min-timer", min);
        plugin.getConfig().set("max-timer", max);
        plugin.getConfig().set("radius", radius);
        plugin.getConfig().set("default-dimension", defaultDimension);
        plugin.getConfig().set("minecord", minecord);
        plugin.saveConfig();
	}

	public void load() {
        min = (int) plugin.getConfig().get("min-timer");
        max = (int) plugin.getConfig().get("max-timer");
        radius = (int) plugin.getConfig().get("radius");
        defaultDimension = (int) plugin.getConfig().get("default-dimension");
        minecord = (boolean) plugin.getConfig().get("minecord");
	}
    
}

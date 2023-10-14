package net.ingoh.minecraft.plugins.resourcechickens;

import org.bukkit.plugin.java.JavaPlugin;

public class Config {

    int min;
    int max;
    int centerX;
    int centerZ;
    int radius;
    JavaPlugin plugin;
    int defaultDimension;
    boolean minecord;
    boolean mchickens;

    public Config(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        load();
    }

	public int getRandomDelay() {
        double rng = Math.random();
		return (int) (rng * (max - min) + min);
    }

    public int getCenterX() {
        return centerX;
    }
    public int getCenterZ() {
        return centerZ;
    }
    
    public int getRadius() {
        return radius;
	}

    public boolean useMultipleChickens() {
        return mchickens;
    }

	public void save() {
        plugin.getConfig().set("min-timer", min);
        plugin.getConfig().set("max-timer", max);
        plugin.getConfig().set("center-x", centerX);
        plugin.getConfig().set("center-z", centerZ);
        plugin.getConfig().set("radius", radius);
        plugin.getConfig().set("default-dimension", defaultDimension);
        plugin.getConfig().set("minecord", minecord);
        plugin.getConfig().set("multiple-chickens", mchickens);
        plugin.saveConfig();
	}

	public void load() {
        min = (int) plugin.getConfig().get("min-timer");
        max = (int) plugin.getConfig().get("max-timer");
        centerX = (int) plugin.getConfig().get("center-x");
        centerZ = (int) plugin.getConfig().get("center-z");
        radius = (int) plugin.getConfig().get("radius");
        defaultDimension = (int) plugin.getConfig().get("default-dimension");
        minecord = (boolean) plugin.getConfig().get("minecord");
        mchickens = (boolean) plugin.getConfig().get("multiple-chickens");
	}
    
}

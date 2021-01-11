package tv.ingoh.minecraft.plugins.resourcechickens;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.minecraft.server.v1_16_R3.WorldServer;
import tv.ingoh.util.Pluralize;

public class Main extends JavaPlugin implements Listener {

    JavaPlugin plugin;
    Config config;
    BukkitRunnable tickThread;
    long next;

    @Override
    public void onEnable() {
        plugin = this;
        config = new Config(this);
        config.load();
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("rc").setTabCompleter(new TabCompleter() {
            @Override
            public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
                if (args.length <= 1) {
                    List<String> chickens = new ArrayList<>();
                    for (ResourceChickenType type : ResourceChickenType.values()) {
                        if (type.toString().toLowerCase().startsWith(args[args.length - 1].toString().toLowerCase()))
                            chickens.add(type.toString().toLowerCase());
                    }
                    return chickens;
                } else
                    return null;
            }
        });
        ResourceChicken.loadAll();
        next = System.currentTimeMillis() + config.getRandomDelay();
        startTickThread();
    }

    @Override
    public void onDisable() {
        ResourceChicken.saveAll();
        tickThread.cancel();
    }

    // Runs every tick
    public void startTickThread() {
        tickThread = new BukkitRunnable() {

            @Override
            public void run() {
                if (System.currentTimeMillis() > next) {
                    next = System.currentTimeMillis() + config.getRandomDelay();
                    World world = (CraftWorld) Bukkit.getServer().getWorlds().get(config.defaultDimension); // Default world
                    int x = (int) Math.round(2 * Math.random() * config.getRadius() + 160 - config.getRadius());
                    int z = (int) Math.round(2 * Math.random() * config.getRadius() + 208 - config.getRadius());
                    ResourceChicken chicken = new ResourceChicken(new Location(world, x, 256, z), ResourceChickenType.random(), ResourceChicken.Rarity.random(), true);
                    try {
                        WorldServer worldServer = ((CraftWorld) world).getHandle();
                        worldServer.addEntity(chicken);
                        Bukkit.getLogger().info("Summoned new [" + chicken.rarity.color + chicken.rarity.toString() + ChatColor.RESET + "] " + chicken.type.color + chicken.type.name + ChatColor.RESET + " at [" + x + ", " + z + "z]");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Bukkit.getLogger().warning("Failed to summon chicken");
                    }
                }
            }
        };
        tickThread.runTaskTimer(this, 0, 1);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("rc")) {
            World world = (CraftWorld) Bukkit.getServer().getWorlds().get(config.defaultDimension);
            if (sender instanceof Player) {
                Player player = (Player) sender;
                world = player.getWorld();
            }
            int x = (int) Math.round(2 * Math.random() * config.getRadius() + 160 - config.getRadius());
            int z = (int) Math.round(2 * Math.random() * config.getRadius() + 208 - config.getRadius());   
            ResourceChicken chicken;
            if (args.length == 1) {
                String type = args[0];
                chicken = new ResourceChicken(new Location(world, x, 256, z), ResourceChickenType.valueOf(type.toUpperCase()), ResourceChicken.Rarity.random(), true);
            } else if (args.length == 0) {
                chicken = new ResourceChicken(new Location(world, x, 256, z), ResourceChickenType.random(), ResourceChicken.Rarity.random(), true);
            } else {
                sender.sendMessage(ChatColor.RED + "Too many arguments");
                return false;
            }
            try {
                WorldServer worldServer = ((CraftWorld) world).getHandle();
                worldServer.addEntity(chicken);
                sender.sendMessage("Summoned new " + "[" + chicken.rarity.color + chicken.rarity.toString() + ChatColor.RESET + "] " + chicken.type.color + chicken.type.name + ChatColor.RESET + " at [" + x + "x, " + z + "z]");
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(ChatColor.RED + "Failed to execute command");
                return false;
            }
        } else if (label.equalsIgnoreCase("rcdata")) {
            ResourceChicken.printData(sender);
        } else if (label.equalsIgnoreCase("chickencount")) {
            int count = ResourceChicken.getCount();
            sender.sendMessage("There " + Pluralize.are(count) + " currently " + count + " chicken" + Pluralize.s(count) + " to be found");
        } else if (label.equalsIgnoreCase("rctimer")) {
            if (args.length == 2) {
                try {
                    config.min = Math.min(Integer.valueOf(args[0]), Integer.valueOf(args[1]));
                    config.max = Math.max(Integer.valueOf(args[0]), Integer.valueOf(args[1]));
                    config.save();
                    next = System.currentTimeMillis() + config.getRandomDelay();
                    sender.sendMessage("Set timer to [" + args[0] + ", " + args[1] + "]");
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "[" + args[0] + ", " + args[1] + "] is not a valid range.");
                }
            } else {
                if (args.length > 2) sender.sendMessage(ChatColor.RED + "Too many arguments");
                else sender.sendMessage(ChatColor.RED + "Too few arguments");
                return false;
            }
        } else if (label.equalsIgnoreCase("rcradius")) {
            if (args.length == 1) {
                try {
                    config.radius = Integer.valueOf(args[0]);
                    config.save();
                    sender.sendMessage("Set radius to " + args[0]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + args[0] + " is not a valid radius.");
                }
            } else {
                if (args.length > 1) sender.sendMessage(ChatColor.RED + "Too many arguments");
                else sender.sendMessage(ChatColor.RED + "Too few arguments");
                return false;
            }
        }
        return true;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        ResourceChicken.reInit(event.getChunk().getEntities());
    }
}
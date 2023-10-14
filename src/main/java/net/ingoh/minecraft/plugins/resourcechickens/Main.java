package net.ingoh.minecraft.plugins.resourcechickens;

import java.util.*;

import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.starsdown64.minecord.api.ExternalMessageEvent;
import net.minecraft.server.level.ServerLevel;
import net.ingoh.minecraft.plugins.resourcechickens.ResourceChicken.Rarity;
import net.ingoh.util.Pluralize;

public class Main extends JavaPlugin implements Listener {

    long recentTime = System.currentTimeMillis();
    JavaPlugin plugin;
    Config config;
    BukkitRunnable tickThread;
    long next;

    boolean broadcast;
    int bcCount;

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
                } else if (args.length == 2) {
                    List<String> rarities = new ArrayList<>();
                    for (Rarity r : Rarity.values()) {
                        if (r.toString().toLowerCase().startsWith(args[args.length - 1].toString().toLowerCase()))
                            rarities.add(r.toString().toLowerCase());
                    }
                    return rarities;
                } else if (args.length <= 5) {
                    String coords = args.length == 5 ? "~ ~ ~" : args.length == 4 ? "~ ~" : "~";
                    if (sender instanceof Player player) {
                        var block = player.getTargetBlock(null, 5);
                        if (block != null && block.getType() != Material.AIR) {
                            coords = block.getX() + " " + block.getY() + " " + block.getZ();
                        }
                    }
                    List<String> a = new ArrayList<>();
                    if (coords.startsWith(args[args.length - 1].toString().toLowerCase())) a.add(coords);
                    return a;
                }
                return null;
            }
        });
        getCommand("rcminecord").setTabCompleter(new TabCompleter() {
            @Override
            public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
                if (args.length <= 1) {
                    List<String> a = new ArrayList<>();
                    if ("enabled".startsWith(args[args.length - 1].toString().toLowerCase())) a.add("enabled");
                    if ("disabled".startsWith(args[args.length - 1].toString().toLowerCase())) a.add("disabled");
                    return a;
                } else
                    return null;
            }
        });
        next = System.currentTimeMillis() + config.getRandomDelay();
        startTickThread();
    }

    @Override
    public void onDisable() {
        tickThread.cancel();
    }

    class ChickenRunnable extends BukkitRunnable {

        Main pl;

        public ChickenRunnable(Main pl) {
            this.pl = pl;
        }

        @Override
        public void run() {
            if (broadcast) {
                if (bcCount == 1) {
                    Bukkit.broadcastMessage(ChatColor.AQUA + "A chicken appeared near spawn!");
                    Bukkit.broadcastMessage(ChatColor.AQUA + "Kill it for a reward!");
                } else {
                    Bukkit.broadcastMessage(ChatColor.AQUA + "" + bcCount + " chickens appeared near spawn!");
                    Bukkit.broadcastMessage(ChatColor.AQUA + "Kill them for a rewards!");
                }
                if (config.minecord && Bukkit.getServer().getPluginManager().getPlugin("Minecord") != null) {
                    ExternalMessageEvent messageEvent = new ExternalMessageEvent("A chicken appeared near spawn!");
                    Bukkit.getServer().getPluginManager().callEvent(messageEvent);
                    messageEvent = new ExternalMessageEvent("Kill it for a reward!");
                    Bukkit.getServer().getPluginManager().callEvent(messageEvent);
                }
                broadcast = false;
                bcCount = 0;
            }
            if (System.currentTimeMillis() > next) {
                int count = config.mchickens ? ResourceChicken.randomCount() : 1;
                next = System.currentTimeMillis() + config.getRandomDelay();
                World world = (CraftWorld) Bukkit.getServer().getWorlds().get(config.defaultDimension); // Default world
                for (int i = 0; i < count; i++) {
                    int x = (int) Math.round(2 * Math.random() * config.getRadius() + config.getCenterX() - config.getRadius());
                    int z = (int) Math.round(2 * Math.random() * config.getRadius() + config.getCenterZ() - config.getRadius());
                    ResourceChicken chicken = new ResourceChicken(pl, new Location(world, x, 320, z), ResourceChickenType.random(), ResourceChicken.Rarity.randomRarity(), true, config);
                    try {
                        ServerLevel worldServer = ((CraftWorld) world).getHandle();
                        worldServer.addFreshEntity(chicken);
                        recentTime = System.currentTimeMillis();
                        Bukkit.getLogger().info("Summoned new [" + chicken.rarity.color + chicken.rarity.toString() + ChatColor.RESET + "] " + chicken.type.color + chicken.type.name + ChatColor.RESET + " at [" + x + ", " + z + "z]");
                    } catch (Exception e) {
                        Bukkit.getLogger().warning("Failed to summon chicken.");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // Runs every tick
    public void startTickThread() {
        tickThread = new ChickenRunnable(this);
        tickThread.runTaskTimer(this, 0, 1);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("rc")) {
            World world = (CraftWorld) Bukkit.getServer().getWorlds().get(config.defaultDimension);
            if (sender instanceof Player player) {
                world = player.getWorld();
            }
            int x = (int) Math.round(2 * Math.random() * config.getRadius() + config.getCenterX() - config.getRadius());
            int z = (int) Math.round(2 * Math.random() * config.getRadius() + config.getCenterZ() - config.getRadius());
            ResourceChicken chicken;
            if (args.length == 5) {
                String type = args[0];
                String rarity = args[1];
                String xStr = args[2];
                String yStr = args[3];
                String zStr = args[4];
                x = Integer.parseInt(xStr);
                int y = Integer.parseInt(yStr);
                z = Integer.parseInt(zStr);
                chicken = new ResourceChicken(this, new Location(world, x, y, z), ResourceChickenType.valueOf(type.toUpperCase()), ResourceChicken.Rarity.valueOf(rarity.toUpperCase()), true, config);
            } else if (args.length == 3 || args.length == 4) {
                sender.sendMessage(ChatColor.RED + "Not enough arguments, idiot.");
                return false;
            } else if (args.length == 2) {
                String type = args[0];
                String rarity = args[1];
                chicken = new ResourceChicken(this, new Location(world, x, 320, z), ResourceChickenType.valueOf(type.toUpperCase()), ResourceChicken.Rarity.valueOf(rarity.toUpperCase()), true, config);
            } else if (args.length == 1) {
                String type = args[0];
                chicken = new ResourceChicken(this, new Location(world, x, 320, z), ResourceChickenType.valueOf(type.toUpperCase()), ResourceChicken.Rarity.randomRarity(), true, config);
            } else if (args.length == 0) {
                chicken = new ResourceChicken(this, new Location(world, x, 320, z), ResourceChickenType.random(), ResourceChicken.Rarity.randomRarity(), true, config);
            } else {
                sender.sendMessage(ChatColor.RED + "Too many arguments, idiot.");
                return false;
            }
            try {
                ServerLevel worldServer = ((CraftWorld) world).getHandle();
                worldServer.addFreshEntity(chicken);
                sender.sendMessage("Summoned new " + "[" + chicken.rarity.color + chicken.rarity.toString() + ChatColor.RESET + "] " + chicken.type.color + chicken.type.name + ChatColor.RESET + " at [" + x + "x, " + z + "z]");
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(ChatColor.RED + "Failed to execute command, idiot.");
                return false;
            }
        } else if (label.equalsIgnoreCase("chickencount")) {
            int count = ResourceChicken.getCount();
            sender.sendMessage("There " + Pluralize.are(count) + " currently " + count + " chicken" + Pluralize.s(count) + " to be found");
        } else if (label.equalsIgnoreCase("chickenrecent")) {
            int time = (int)((System.currentTimeMillis() - recentTime) / 60000);
            String p = "chicken(s)";
            if (!config.mchickens) p = "chicken";
            sender.sendMessage("The most recent " + p + " spawned " + time + " minute" + Pluralize.s(time) + " ago");
        } else if (label.equalsIgnoreCase("rctimer")) {
            if (args.length == 2) {
                try {
                    config.min = Math.min(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
                    config.max = Math.max(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
                    config.save();
                    next = System.currentTimeMillis() + config.getRandomDelay();
                    sender.sendMessage("Set timer to [" + args[0] + ", " + args[1] + "]");
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "[" + args[0] + ", " + args[1] + "] is not a valid range, idiot.");
                }
            } else {
                if (args.length > 2) sender.sendMessage(ChatColor.RED + "Too many arguments, idiot.");
                else sender.sendMessage(ChatColor.RED + "Too few arguments, idiot.");
                return false;
            }
        } else if (label.equalsIgnoreCase("rcminecord")) {
            if (args.length == 1) {
                try {
                    config.minecord = Boolean.parseBoolean(args[0].toLowerCase().replace("enabled", "true").replace("disabled", "false"));
                    config.save();
                    if (config.minecord) sender.sendMessage("Enabled Minecord integration");
                    else sender.sendMessage("Disabled Minecord integration");
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "[" + args[0] + ", " + args[1] + "] is not a valid range, idiot.");
                }
            } else {
                if (args.length > 1) sender.sendMessage(ChatColor.RED + "Too many arguments, idiot.");
                else sender.sendMessage(ChatColor.RED + "Too few arguments, idiot.");
                return false;
            }
        } else if (label.equalsIgnoreCase("rcradius")) {
            if (args.length == 1) {
                try {
                    config.radius = Integer.parseInt(args[0]);
                    config.save();
                    sender.sendMessage("Set radius to " + args[0]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + args[0] + " is not a valid radius, idiot.");
                }
            } else {
                if (args.length > 1) sender.sendMessage(ChatColor.RED + "Too many arguments, idiot.");
                else sender.sendMessage(ChatColor.RED + "Too few arguments, idiot.");
                return false;
            }
        }
        return true;
    }


    @EventHandler
    public void onEntityLoad(EntitiesLoadEvent event) {
        ResourceChicken.reInit(this, config, event.getEntities());
    }

    @EventHandler
    public void onEntityUnload(EntitiesUnloadEvent event) {
        ResourceChicken.deInit(config, event.getEntities());
    }


    @EventHandler
    public void interact(PlayerInteractEntityEvent e) {
        if (e.getHand() == EquipmentSlot.HAND) {
            Entity entity = e.getRightClicked();
            if (entity.getType().equals(EntityType.CHICKEN)) {
                if (entity.getCustomName() != null && entity.getCustomName().contains("§") && !e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.LEAD)) {
                    ResourceChickenType type = ResourceChickenType.INVALID;
                    for (ResourceChickenType t : ResourceChickenType.values()) {
                        if (entity.getCustomName().contains(t.name)) {
                            type = t;
                        }
                    }
                    Rarity rarity = Rarity.SPECIAL;
                    for (Rarity r : Rarity.values()) {
                        if (entity.getCustomName().contains(r.name())) {
                            rarity = r;
                        }
                    }
                    e.getPlayer().sendMessage("Type: " + type.name());
                    if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.PAPER)) {
                        for (String s : type.listLoot()) {
                            e.getPlayer().sendMessage(s);
                        }
                    }
                    e.getPlayer().sendMessage("Rarity: " + rarity.name() + " Drops: " + rarity.dropMultiplier + "x Luck: " + rarity.luckMultiplier + "x");
                    if (!e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.PAPER)) {
                        e.getPlayer().sendMessage("Use " + Material.PAPER.name() + " to see the list of drops");
                    }
                    if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.NAME_TAG)) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerLeashEntity(PlayerLeashEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType().equals(EntityType.CHICKEN) && event.getLeashHolder().getType().equals(EntityType.LEASH_HITCH)){
            if (entity.getCustomName() != null && entity.getCustomName().contains("§")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntityType().equals(EntityType.CHICKEN)) {
            if (event.getEntity().getCustomName() != null && event.getEntity().getCustomName().contains("§")) {
                if (ResourceChicken.loadedChickens.stream().noneMatch(c -> c.uuid.equals(event.getEntity().getUniqueId()))) {
                    event.setCancelled(true);
                    ResourceChicken.reInit(this, config, List.of(event.getEntity()));
                }
            }
        }
    }

    public void scheduleBroadcast() {
        broadcast = true;
        bcCount++;
    }
}
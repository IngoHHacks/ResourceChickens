package tv.ingoh.minecraft.plugins.resourcechickens;

import java.util.Collection;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import io.github.starsdown64.minecord.api.ExternalMessageEvent;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.protocol.game.PacketPlayOutChat;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.animal.EntityChicken;
import net.minecraft.world.item.Items;

public class ResourceChicken extends EntityChicken {

    private static LinkedList<LoadedChicken> loadedChickens = new LinkedList<>();

    Config config;
    ResourceChickenType type;
    long nextEvent;
    Rarity rarity;
    boolean found;
    long spawnTime;
    float damageBuffer = 0;
    long damageBufferTime = 0;
    DamageSource damageBufferSource;

    public enum Rarity {
        COMMON(1,1, ChatColor.WHITE), UNCOMMON(1.5, 1.2, ChatColor.GREEN), RARE(2, 1.5, ChatColor.BLUE), EPIC(2.5, 2, ChatColor.DARK_PURPLE), LEGENDARY(5, 2.5, ChatColor.GOLD), SPECIAL(1,1, ChatColor.RED);
        double dropMultiplier;
        double luckMultiplier;
        ChatColor color;

        Rarity(double dropMultiplier, double luckMultiplier, ChatColor color) {
            this.dropMultiplier = dropMultiplier;
            this.luckMultiplier = luckMultiplier;
            this.color = color;
        }

        static Rarity randomRarity() {
            double rng = Math.random();
            if (rng < 0.01) return LEGENDARY;
            else if (rng < 0.04) return EPIC;
            else if (rng < 0.10) return RARE;
            else if (rng < 0.31) return UNCOMMON;
            else return COMMON;
        }

    }

    static int randomCount() {
        double rng = Math.random();
        if (rng < 0.01) return 5;
        else if (rng < 0.04) return 4;
        else if (rng < 0.10) return 3;
        else if (rng < 0.31) return 2;
        else return 1;
    }


    public ResourceChicken(Main pl, Location loc, ResourceChickenType type, Rarity rarity, boolean isNew, Config config) {
        super(EntityTypes.l /*CHICKEN*/, ((CraftWorld) loc.getWorld()).getHandle());

        this.config = config;

        // Find highest non-air block and spawn on top of it
        if (isNew) {
            if (loc.getY() > 255) {
                while ((loc.getY() > 0 && loc.add(0, -1, 0).getBlock().getType().equals(Material.AIR)));
                loc.add(0, 1, 0);
            }
        }
    	if (isNew) e(Math.round(loc.getX()) + 0.5, loc.getY(), Math.round(loc.getZ()) + 0.5);
        else e(loc.getX(), loc.getY(), loc.getZ());
        a(loc.getYaw(), loc.getPitch());
        a(new ChatComponentText("[" + rarity.color + rarity.toString() + ChatColor.RESET + "] " + type.color + type.name + ChatColor.RESET));

        spawnTime = System.currentTimeMillis();

        this.type = type;
        this.rarity = rarity;
        if (type.equals(ResourceChickenType.UNDYING)) this.a(EnumItemSlot.a /*MAINHAND*/, new net.minecraft.world.item.ItemStack(Items.sw/*TOTEM*/, 1));
        nextEvent = (long) (System.currentTimeMillis() + 10000 * Math.random());
        found = !isNew;

        loadedChickens.add(new LoadedChicken(this, this.cm()));

        if (isNew) {
            pl.scheduleBroadcast();
        }

        loc.getChunk().load();
    }

    // k = tick()
    @Override
    public void k() {
        super.k();

        // s = level

        switch (type) {
            case DIAMOND:
                s.getWorld().spawnParticle(Particle.ITEM_CRACK,
                        new Location(s.getWorld(), cV().b, cV().c + 0.35, cV().d), 3, 0.5, 0.5, 0.5, 0.1,
                        new ItemStack(Material.DIAMOND));
                break;
            case AQUATIC:
                s.getWorld().spawnParticle(Particle.DRIP_WATER,
                        new Location(s.getWorld(), cV().b, cV().c  + 0.35, cV().d), 3, 0.5, 0.5, 0.5, 1);
                break;
            case GOLDEN_APPLE:
                s.getWorld().spawnParticle(Particle.ITEM_CRACK,
                        new Location(s.getWorld(), cV().b, cV().c  + 0.35, cV().d), 3, 0.5, 0.5, 0.5, 0.1,
                        new ItemStack(Material.GOLDEN_APPLE));
                break;
            case LUCKY:
                long time = System.currentTimeMillis();
                for (double i = 0; i < 2 * Math.PI; i+= 0.2 * Math.PI) {
                    java.awt.Color color = new java.awt.Color(java.awt.Color.HSBtoRGB((float)(((time + (i/Math.PI * 3000.0)) % 3000.0)/3000.0), 1, 1));
                    Color c = Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
                    s.getWorld().spawnParticle(Particle.REDSTONE,
                            new Location(s.getWorld(), cV().b + Math.sin(time/500.0 + i), cV().c + 0.35 + Math.sin(time/250.0 + i), cV().d + Math.cos(time/500.0 + i)), 1, 0.02, 0.02, 0.02, 1,
                            new Particle.DustOptions(c, 1));
                    s.getWorld().spawnParticle(Particle.REDSTONE,
                            new Location(s.getWorld(), cV().b - Math.sin(time/500.0 + i), cV().c + 0.35 + Math.sin(time/250.0 + i), cV().d - Math.cos(time/500.0 + i)), 1, 0.02, 0.02, 0.02, 1,
                            new Particle.DustOptions(c, 1));
                }
                break;
            case BASTION:
                s.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(s.getWorld(), cV().b, cV().c + 0.35, cV().d), 2, 0.5, 0.5, 0.5, 0.1,
                        Material.POLISHED_BLACKSTONE_BRICKS.createBlockData());
                s.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(s.getWorld(), cV().b, cV().c + 0.35, cV().d), 1, 0.5, 0.5, 0.5, 0.1,
                        Material.GILDED_BLACKSTONE.createBlockData());
                break;
            case CAVE:
                s.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(s.getWorld(), cV().b, cV().c + 0.35, cV().d), 3, 0.5, 0.5, 0.5, 0.1,
                        Material.STONE.createBlockData());
                s.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(s.getWorld(), cV().b, cV().c + 0.35, cV().d), 3, 0.5, 0.5, 0.5, 0.1,
                        Material.DIAMOND_ORE.createBlockData());
                break;
            case CONCRETE:
                s.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(s.getWorld(), cV().b, cV().c + 0.35, cV().d), 3, 0.5, 0.5, 0.5, 0.1,
                        Material.WHITE_CONCRETE.createBlockData());
                break;
            case DESERT:
                s.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(s.getWorld(), cV().b, cV().c + 0.35, cV().d), 3, 0.5, 0.5, 0.5, 0.1,
                        Material.SAND.createBlockData());
                break;
            case END:
                s.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(s.getWorld(), cV().b, cV().c + 0.35, cV().d), 3, 0.5, 0.5, 0.5, 0.1,
                        Material.END_STONE.createBlockData());
                break;
            case FOOD:
                s.getWorld().spawnParticle(Particle.ITEM_CRACK,
                        new Location(s.getWorld(), cV().b, cV().c + 0.35, cV().d), 3, 0.5, 0.5, 0.5, 0.1,
                        new ItemStack(Material.COOKED_CHICKEN));
                break;
            case MUSIC:
                s.getWorld().spawnParticle(Particle.NOTE,
                        new Location(s.getWorld(), cV().b, cV().c + 0.35, cV().d), 3, 0.5, 0.5, 0.5, 0.1);
                break;
            case NETHER:
                s.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(s.getWorld(), cV().b, cV().c + 0.35, cV().d), 1, 0.5, 0.5, 0.5, 0.1,
                        Material.NETHERRACK.createBlockData());
                s.getWorld().spawnParticle(Particle.FLAME,
                        new Location(s.getWorld(), cV().b, cV().c + 0.35, cV().d), 2, 0.5, 0.5, 0.5, 0.1);
                break;
            case OVERWORLD:
                s.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(s.getWorld(), cV().b, cV().c + 0.35, cV().d), 3, 0.5, 0.5, 0.5, 0.1,
                        Material.GRASS_BLOCK.createBlockData());
                break;
            case REDSTONE:
                s.getWorld().spawnParticle(Particle.REDSTONE,
                        new Location(s.getWorld(), cV().b, cV().c + 0.35, cV().d), 3, 0.5, 0.5, 0.5, 1,
                        new Particle.DustOptions(Color.RED, 1));
                break;
            case TERRACOTTA:
                s.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(s.getWorld(), cV().b, cV().c + 0.35, cV().d), 3, 0.5, 0.5, 0.5, 0.1,
                        Material.TERRACOTTA.createBlockData());
                break;
            case UNDYING:
                s.getWorld().spawnParticle(Particle.TOTEM,
                        new Location(s.getWorld(), cV().b, cV().c + 0.35, cV().d), 3, 0.5, 0.5, 0.5, 0.1);
                break;
            case UNSTABLE:
                s.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE,
                        new Location(s.getWorld(), cV().b, cV().c + 0.35, cV().d), 10, 2.5, 2.5, 2.5, 0.1);
                if (System.currentTimeMillis() > nextEvent) {
                    nextEvent = (long) (500 + System.currentTimeMillis() + 4500 * Math.random());
                    Location l = new Location(s.getWorld(), Math.round(cV().b - 8 + Math.random() * 16.0) + 0.5, Math.round(cV().c - 4 + Math.random() * 8.0), Math.round(cV().d - 8 + Math.random() * 16.0) + 0.5);
                    if (l.getBlock().getType().equals(Material.AIR) && l.getY() > 0 && l.getY() < 256) b(l.getX(), l.getY(), l.getZ());
                }
                if (damageBufferTime != 0 && System.currentTimeMillis() > damageBufferTime && System.currentTimeMillis() > spawnTime + 5000) {
                    a(damageBufferSource, damageBuffer);
                }
                break;
            case WITHER:
                s.getWorld().spawnParticle(Particle.REDSTONE,
                        new Location(s.getWorld(), cV().b, cV().c + 0.35, cV().d), 3, 0.5, 0.5, 0.5, 1,
                        new Particle.DustOptions(Color.BLACK, 1));
                break;
            case STABLE:
                s.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE,
                        new Location(s.getWorld(), cV().b, cV().c + 0.35, cV().d), 10, 2.5, 2.5, 2.5, 0.1);
                if (damageBufferTime != 0 && System.currentTimeMillis() > damageBufferTime && System.currentTimeMillis() > spawnTime + 5000) {
                    a(damageBufferSource, damageBuffer);
                }
                break;
            default:
                break;
        }
        if (!found) {
            Collection<Entity> near = s.getWorld().getNearbyEntities(new Location(s.getWorld(), cV().b, cV().c, cV().d), 10, 10, 10, (entity -> entity.getType() == EntityType.PLAYER));
            if (near.size() > 0) {
                found = true;
            }
        }
    }


    // a = DropDeathLoot
    @Override
    protected void a(DamageSource damagesource, int i, boolean flag) {
        super.a(damagesource, i, flag);
        if (!Q) {
            for (ItemStack itemStack : type.getLoot(bE(), rarity)) {
                s.getWorld().dropItem(new Location(s.getWorld(), cV().b, cV().c, cV().d), itemStack);
            }
        }

        PacketPlayOutChat packet = new PacketPlayOutChat(damagesource.a(this), ChatMessageType.a /*SYSTEM*/, cm() /*UUID*/);
        Bukkit.getOnlinePlayers().forEach(player -> ((CraftPlayer) player).getHandle().b /*playerConnection*/ .a(packet));
        if (Bukkit.getServer().getPluginManager().getPlugin("Minecord") != null) {
            ExternalMessageEvent messageEvent = new ExternalMessageEvent(damagesource.a(this).getString());
            Bukkit.getPluginManager().callEvent(messageEvent);
        }

        LoadedChicken c = null;
        for (LoadedChicken chicken : loadedChickens) {
            if (chicken.uuid.equals(cm() /*UUID*/)) {
                c = chicken;
                break;
            }
        }
        if (c != null) loadedChickens.remove(c);
    }

	public static int getCount() {
        int c = 0;
		for (LoadedChicken resourceChicken : loadedChickens) {
            if (!resourceChicken.chicken.found && resourceChicken.chicken.spawnTime > System.currentTimeMillis() - 172800000) c++;
        }
        return c;
	}

    // Runs when a chunk is loaded; Deletes every normal chicken and spawns a Resource Chicken in its place.
	public static void reInit(Main pl, Entity[] entities, Config config) {
        for (Entity entity : entities) {
            if (entity.getType().equals(EntityType.CHICKEN)) {
                if (entity.getCustomName() != null && entity.getCustomName().contains("§")) {
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
                    ResourceChicken chicken = new ResourceChicken(pl, entity.getLocation(), type, rarity, false, config);
                    try {
                        WorldServer worldServer = ((CraftWorld) entity.getWorld()).getHandle();
                        worldServer.b(chicken);
                        if (entity.isInsideVehicle()) {
                            Entity v = entity.getVehicle();
                            entity.leaveVehicle();
                            v.addPassenger(chicken.getBukkitEntity());
                        }
                        entity.remove();
                    } catch (Exception e) {
                        Bukkit.getLogger().warning("Failed to re-initialize " + entity.getCustomName());
                        e.printStackTrace();
                    }
                }
            }
        }
	}

    public static void deInit(Entity[] entities, Config config) {
        for (Entity entity : entities) {
            if (entity.getType().equals(EntityType.CHICKEN)) {
                if (entity.getCustomName() != null && entity.getCustomName().contains("§")) {
                    LoadedChicken c = null;
                    for (LoadedChicken chicken : loadedChickens) {
                        if (chicken.uuid.equals(entity.getUniqueId())) {
                            c = chicken;
                            break;
                        }                    
                    }
                    if (c != null) loadedChickens.remove(c);
                }
            }
        }
    }

    // Code for stabilizing Unstable Chickens when struck with lightning
    @Override
    public boolean a(DamageSource damagesource, float f) {
        if ((!type.equals(ResourceChickenType.UNSTABLE) && !type.equals(ResourceChickenType.STABLE)) || (damageBufferTime != 0 && System.currentTimeMillis() > damageBufferTime && System.currentTimeMillis() > spawnTime + 5000)) {
            damageBufferTime = 0;
            damageBuffer = 0;
            return super.a(damagesource, f);
        } else {
            if (System.currentTimeMillis() > spawnTime + 5000) {
                if (damageBuffer == 0) {
                    damageBufferTime = System.currentTimeMillis() + 100;
                    damageBufferSource = damagesource;
                }
                damageBuffer += f;
                if (damagesource.equals(DamageSource.b /*LIGHTNING*/)) {
                        if (type.equals(ResourceChickenType.UNSTABLE)) type = ResourceChickenType.STABLE;
                        else if (type.equals(ResourceChickenType.STABLE)) type = ResourceChickenType.UNSTABLE;
                        spawnTime = System.currentTimeMillis();
                        damageBuffer = 0;
                        damageBufferTime = 0;
                        a(new ChatComponentText("[" + rarity.color + rarity.toString() + ChatColor.RESET + "] " + type.color + type.name + ChatColor.RESET));
                }
            }
            return true;
        }
    }
}

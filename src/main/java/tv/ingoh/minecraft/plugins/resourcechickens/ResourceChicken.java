package tv.ingoh.minecraft.plugins.resourcechickens;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import io.github.starsdown64.Minecord.api.ExternalMessageEvent;
import net.minecraft.server.v1_16_R3.ChatComponentText;
import net.minecraft.server.v1_16_R3.ChatMessageType;
import net.minecraft.server.v1_16_R3.DamageSource;
import net.minecraft.server.v1_16_R3.EntityChicken;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import net.minecraft.server.v1_16_R3.Items;
import net.minecraft.server.v1_16_R3.PacketPlayOutChat;
import net.minecraft.server.v1_16_R3.WorldServer;

public class ResourceChicken extends EntityChicken {

    private static ArrayList<LoadedChicken> loadedChickens = new ArrayList<>();

    Config config;
    ResourceChickenType type;
    long nextEvent;
    Rarity rarity;
    boolean found;
    long spawnTime;
    float damageBuffer = 0;
    long damageBufferTime = 0;
    DamageSource damageBufferSource;
    net.minecraft.server.v1_16_R3.Entity prevLeashHolder;

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

        static Rarity random() {
            double rng = Math.random();
            if (rng < 0.01) return LEGENDARY;
            else if (rng < 0.04) return EPIC;
            else if (rng < 0.10) return RARE;
            else if (rng < 0.31) return UNCOMMON;
            else return COMMON;
        }

    }


    public ResourceChicken(Location loc, ResourceChickenType type, Rarity rarity, boolean isNew, Config config) {
        super(EntityTypes.CHICKEN, ((CraftWorld) loc.getWorld()).getHandle());

        this.config = config;
        
        // Find highest non-air block and spawn on top of it
        if (isNew) {
            if (loc.getY() > 255) {
                while ((loc.getY() > 0 && loc.add(0, -1, 0).getBlock().getType().equals(Material.AIR)));
                loc.add(0, 1, 0);
            }
        }
    	if (isNew) setPosition(Math.round(loc.getX()) + 0.5, loc.getY(), Math.round(loc.getZ()) + 0.5);
        else setPosition(loc.getX(), loc.getY(), loc.getZ());
        setYawPitch(loc.getYaw(), loc.getPitch());
        setCustomName(new ChatComponentText("[" + rarity.color + rarity.toString() + ChatColor.RESET + "] " + type.color + type.name + ChatColor.RESET));
        
        spawnTime = System.currentTimeMillis();

        this.type = type;
        this.rarity = rarity;
        if (type.equals(ResourceChickenType.UNDYING)) this.setSlot(EnumItemSlot.MAINHAND, new net.minecraft.server.v1_16_R3.ItemStack(Items.TOTEM_OF_UNDYING, 1));
        nextEvent = (long) (System.currentTimeMillis() + 10000 * Math.random());
        found = !isNew;

        loadedChickens.add(new LoadedChicken(this, this.getUniqueID()));

        if (isNew) {
            Bukkit.broadcastMessage(ChatColor.AQUA + "A chicken appeared near spawn!");
            Bukkit.broadcastMessage(ChatColor.AQUA + "Kill it for a reward!");
            if (config.minecord && Bukkit.getServer().getPluginManager().getPlugin("Minecord") != null) {
                ExternalMessageEvent messageEvent = new ExternalMessageEvent("A chicken appeared near spawn!");
                Bukkit.getServer().getPluginManager().callEvent(messageEvent);
                messageEvent = new ExternalMessageEvent("Kill it for a reward!");
                Bukkit.getServer().getPluginManager().callEvent(messageEvent);
            }
        }

        loc.getChunk().load();
    }
    
    @Override
    public void tick() {
        super.tick();

        switch (type) {
            case DIAMOND:
                world.getWorld().spawnParticle(Particle.ITEM_CRACK,
                        new Location(world.getWorld(), locX(), locY() + 0.35, locZ()), 3, 0.5, 0.5, 0.5, 0.1,
                        new ItemStack(Material.DIAMOND));
                break;
            case AQUATIC:
                world.getWorld().spawnParticle(Particle.DRIP_WATER,
                        new Location(world.getWorld(), locX(), locY() + 0.35, locZ()), 3, 0.5, 0.5, 0.5, 1);
                break;
            case GOLDEN_APPLE:
                world.getWorld().spawnParticle(Particle.ITEM_CRACK,
                        new Location(world.getWorld(), locX(), locY() + 0.35, locZ()), 3, 0.5, 0.5, 0.5, 0.1,
                        new ItemStack(Material.GOLDEN_APPLE));
                break;
            case LUCKY:
                long t = System.currentTimeMillis();
                for (double i = 0; i < 2 * Math.PI; i+= 0.2 * Math.PI) {
                    java.awt.Color color = new java.awt.Color(java.awt.Color.HSBtoRGB((float)(((t + (i/Math.PI * 3000.0)) % 3000.0)/3000.0), 1, 1));
                    Color c = Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
                    world.getWorld().spawnParticle(Particle.REDSTONE,
                            new Location(world.getWorld(), locX() + Math.sin(t/500.0 + i), locY() + 0.35 + Math.sin(t/250.0 + i), locZ() + Math.cos(t/500.0 + i)), 1, 0.02, 0.02, 0.02, 1,
                            new Particle.DustOptions(c, 1));
                    world.getWorld().spawnParticle(Particle.REDSTONE,
                            new Location(world.getWorld(), locX() - Math.sin(t/500.0 + i), locY() + 0.35 + Math.sin(t/250.0 + i), locZ() - Math.cos(t/500.0 + i)), 1, 0.02, 0.02, 0.02, 1,
                            new Particle.DustOptions(c, 1));
                }
                break;
            case BASTION:
                world.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(world.getWorld(), locX(), locY() + 0.35, locZ()), 2, 0.5, 0.5, 0.5, 0.1,
                        Material.POLISHED_BLACKSTONE_BRICKS.createBlockData());
                world.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(world.getWorld(), locX(), locY() + 0.35, locZ()), 1, 0.5, 0.5, 0.5, 0.1,
                        Material.GILDED_BLACKSTONE.createBlockData());
                break;
            case CAVE:
                world.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(world.getWorld(), locX(), locY() + 0.35, locZ()), 3, 0.5, 0.5, 0.5, 0.1,
                        Material.STONE.createBlockData());
                world.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(world.getWorld(), locX(), locY() + 0.35, locZ()), 3, 0.5, 0.5, 0.5, 0.1,
                        Material.DIAMOND_ORE.createBlockData());
                break;
            case CONCRETE:
                world.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(world.getWorld(), locX(), locY() + 0.35, locZ()), 3, 0.5, 0.5, 0.5, 0.1,
                        Material.WHITE_CONCRETE.createBlockData());
                break;
            case DESERT:
                world.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(world.getWorld(), locX(), locY() + 0.35, locZ()), 3, 0.5, 0.5, 0.5, 0.1,
                        Material.SAND.createBlockData());
                break;
            case END:
                world.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(world.getWorld(), locX(), locY() + 0.35, locZ()), 3, 0.5, 0.5, 0.5, 0.1,
                        Material.END_STONE.createBlockData());
                break;
            case FOOD:
                world.getWorld().spawnParticle(Particle.ITEM_CRACK,
                        new Location(world.getWorld(), locX(), locY() + 0.35, locZ()), 3, 0.5, 0.5, 0.5, 0.1,
                        new ItemStack(Material.COOKED_CHICKEN));
                break;
            case MUSIC:
                world.getWorld().spawnParticle(Particle.NOTE,
                        new Location(world.getWorld(), locX(), locY() + 0.35, locZ()), 3, 0.5, 0.5, 0.5, 0.1);
                break;
            case NETHER:
                world.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(world.getWorld(), locX(), locY() + 0.35, locZ()), 1, 0.5, 0.5, 0.5, 0.1,
                        Material.NETHERRACK.createBlockData());
                world.getWorld().spawnParticle(Particle.FLAME,
                        new Location(world.getWorld(), locX(), locY() + 0.35, locZ()), 2, 0.5, 0.5, 0.5, 0.1);
                break;
            case OVERWORLD:
                world.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(world.getWorld(), locX(), locY() + 0.35, locZ()), 3, 0.5, 0.5, 0.5, 0.1,
                        Material.GRASS_BLOCK.createBlockData());
                break;
            case REDSTONE:
                world.getWorld().spawnParticle(Particle.REDSTONE,
                        new Location(world.getWorld(), locX(), locY() + 0.35, locZ()), 3, 0.5, 0.5, 0.5, 1,
                        new Particle.DustOptions(Color.RED, 1));
                break;
            case TERRACOTTA:
                world.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(world.getWorld(), locX(), locY() + 0.35, locZ()), 3, 0.5, 0.5, 0.5, 0.1,
                        Material.TERRACOTTA.createBlockData());
                break;
            case UNDYING:
                world.getWorld().spawnParticle(Particle.TOTEM,
                        new Location(world.getWorld(), locX(), locY() + 0.35, locZ()), 3, 0.5, 0.5, 0.5, 0.1);
                break;
            case UNSTABLE:
                world.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE,
                        new Location(world.getWorld(), locX(), locY() + 0.35, locZ()), 10, 2.5, 2.5, 2.5, 0.1);
                if (System.currentTimeMillis() > nextEvent) {
                    nextEvent = (long) (500 + System.currentTimeMillis() + 4500 * Math.random());
                    Location l = new Location(world.getWorld(), Math.round(locX() - 8 + Math.random() * 16.0) + 0.5, Math.round(locY() - 4 + Math.random() * 8.0), Math.round(locZ() - 8 + Math.random() * 16.0) + 0.5);
                    if (l.getBlock().getType().equals(Material.AIR) && l.getY() > 0 && l.getY() < 256) teleportAndSync(l.getX(), l.getY(), l.getZ());
                }
                if (damageBufferTime != 0 && System.currentTimeMillis() > damageBufferTime && System.currentTimeMillis() > spawnTime + 5000) {
                    damageEntity(damageBufferSource, damageBuffer);
                }
                break;
            case WITHER:
                world.getWorld().spawnParticle(Particle.REDSTONE,
                        new Location(world.getWorld(), locX(), locY() + 0.35, locZ()), 3, 0.5, 0.5, 0.5, 1,
                        new Particle.DustOptions(Color.BLACK, 1));
                break;
            case STABLE:
                world.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE,
                        new Location(world.getWorld(), locX(), locY() + 0.35, locZ()), 10, 2.5, 2.5, 2.5, 0.1);
                if (damageBufferTime != 0 && System.currentTimeMillis() > damageBufferTime && System.currentTimeMillis() > spawnTime + 5000) {
                    damageEntity(damageBufferSource, damageBuffer);
                }
                break;
            default:
                break;
        }
        if (!found) {
            Collection<Entity> near = world.getWorld().getNearbyEntities(new Location(world.getWorld(), locX(), locY(), locZ()), 10, 10, 10, entity -> entity.getType() == EntityType.PLAYER);
            if (near.size() > 0) {
                found = true;
            }
        }
    }

    @Override
    protected void dropDeathLoot(DamageSource damagesource, int i, boolean flag) {
        super.dropDeathLoot(damagesource, i, flag);
        for (ItemStack itemStack : type.getLoot(isBurning(), rarity)) {
            world.getWorld().dropItem(new Location(world.getWorld(), locX(), locY(), locZ()), itemStack);
        }
        PacketPlayOutChat packet = new PacketPlayOutChat(damagesource.getLocalizedDeathMessage(this), ChatMessageType.SYSTEM, uniqueID);
        Bukkit.getOnlinePlayers().forEach(player -> ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet));
        if (Bukkit.getServer().getPluginManager().getPlugin("Minecord") != null) {
            ExternalMessageEvent messageEvent = new ExternalMessageEvent(damagesource.getLocalizedDeathMessage(this).getString());
            Bukkit.getPluginManager().callEvent(messageEvent);
        }

        LoadedChicken c = null;
        for (LoadedChicken chicken : loadedChickens) {
            if (chicken.uuid.equals(uniqueID)) {
                c = chicken;
                break;
            }                    
        }
        if (c != null) loadedChickens.remove(c);
    }

	public static int getCount() {
        int c = 0;
		for (LoadedChicken resourceChicken : loadedChickens) {
            if (!resourceChicken.chicken.found && resourceChicken.chicken.spawnTime > System.currentTimeMillis() - 7200000) c++;
        }
        return c;
	}

    // Runs when a chunk is loaded; Deletes every normal chicken and spawns a Resource Chicken in its place.
	public static void reInit(Entity[] entities, Config config) {
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
                    ResourceChicken chicken = new ResourceChicken(entity.getLocation(), type, rarity, false, config);
                    try {
                        WorldServer worldServer = ((CraftWorld) entity.getWorld()).getHandle();
                        worldServer.addEntity(chicken);
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
    public boolean damageEntity(DamageSource damagesource, float f) {
        if ((!type.equals(ResourceChickenType.UNSTABLE) && !type.equals(ResourceChickenType.STABLE)) || (damageBufferTime != 0 && System.currentTimeMillis() > damageBufferTime && System.currentTimeMillis() > spawnTime + 5000)) {
            damageBufferTime = 0;
            damageBuffer = 0;
            return super.damageEntity(damagesource, f);
        } else {
            if (System.currentTimeMillis() > spawnTime + 5000) {
                if (damageBuffer == 0) {
                    damageBufferTime = System.currentTimeMillis() + 100;
                    damageBufferSource = damagesource;
                }
                damageBuffer += f;
                if (damagesource.equals(DamageSource.LIGHTNING)) {
                        if (type.equals(ResourceChickenType.UNSTABLE)) type = ResourceChickenType.STABLE;
                        else if (type.equals(ResourceChickenType.STABLE)) type = ResourceChickenType.UNSTABLE;
                        spawnTime = System.currentTimeMillis();
                        damageBuffer = 0;
                        damageBufferTime = 0;
                        setCustomName(new ChatComponentText("[" + rarity.color + rarity.toString() + ChatColor.RESET + "] " + type.color + type.name + ChatColor.RESET));
                }
            }
            return true;
        }
    }
}

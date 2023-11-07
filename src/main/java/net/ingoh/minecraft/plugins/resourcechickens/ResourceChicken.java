package net.ingoh.minecraft.plugins.resourcechickens;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import io.github.starsdown64.minecord.api.ExternalMessageEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.Items;

public class ResourceChicken extends Chicken {

    static LinkedList<LoadedChicken> loadedChickens = new LinkedList<>();

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
        super(net.minecraft.world.entity.EntityType.CHICKEN, ((CraftWorld) loc.getWorld()).getHandle());

        this.config = config;

        // Find highest non-air block and spawn on top of it
        if (isNew) {
            if (loc.getY() > 319) {
                while ((loc.getY() > -64 && loc.add(0, -1, 0).getBlock().getType().equals(Material.AIR)));
            }
            setPos(Math.round(loc.getX()) + 0.5, loc.getY() + 1, Math.round(loc.getZ()) + 0.5);
        }
        else setPos(loc.getX(), loc.getY(), loc.getZ());
        setRot(loc.getYaw(), loc.getPitch());
        
        setCustomName(Component.literal("[" + rarity.color + rarity.toString() + ChatColor.RESET + "] " + type.color + type.name + ChatColor.RESET));

        spawnTime = System.currentTimeMillis();

        this.type = type;
        this.rarity = rarity;
        if (type.equals(ResourceChickenType.UNDYING)) this.setItemSlot(EquipmentSlot.MAINHAND, new net.minecraft.world.item.ItemStack(Items.TOTEM_OF_UNDYING, 1));
        nextEvent = (long) (System.currentTimeMillis() + 10000 * Math.random());
        found = !isNew;

        loadedChickens.add(new LoadedChicken(this, this.uuid));

        if (isNew) {
            pl.scheduleBroadcast();
        }

        loc.getChunk().load();
    }

    @Override
    public void tick() {
        super.tick();

        Level level = this.level();

        switch (type) {
            case DIAMOND:
                level.getWorld().spawnParticle(Particle.ITEM_CRACK,
                        new Location(level.getWorld(), position().x, position().y + 0.35, position().z), 3, 0.5, 0.5, 0.5, 0.1,
                        new ItemStack(Material.DIAMOND));
                break;
            case AQUATIC:
                level.getWorld().spawnParticle(Particle.DRIP_WATER,
                        new Location(level.getWorld(), position().x, position().y  + 0.35, position().z), 3, 0.5, 0.5, 0.5, 1);
                break;
            case GOLDEN_APPLE:
                level.getWorld().spawnParticle(Particle.ITEM_CRACK,
                        new Location(level.getWorld(), position().x, position().y  + 0.35, position().z), 3, 0.5, 0.5, 0.5, 0.1,
                        new ItemStack(Material.GOLDEN_APPLE));
                break;
            case LUCKY:
                long time = System.currentTimeMillis();
                for (double i = 0; i < 2 * Math.PI; i+= 0.2 * Math.PI) {
                    java.awt.Color color = new java.awt.Color(java.awt.Color.HSBtoRGB((float)(((time + (i/Math.PI * 3000.0)) % 3000.0)/3000.0), 1, 1));
                    Color c = Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
                    level.getWorld().spawnParticle(Particle.REDSTONE,
                            new Location(level.getWorld(), position().x + Math.sin(time/500.0 + i), position().y + 0.35 + Math.sin(time/250.0 + i), position().z + Math.cos(time/500.0 + i)), 1, 0.02, 0.02, 0.02, 1,
                            new Particle.DustOptions(c, 1));
                    level.getWorld().spawnParticle(Particle.REDSTONE,
                            new Location(level.getWorld(), position().x - Math.sin(time/500.0 + i), position().y + 0.35 + Math.sin(time/250.0 + i), position().z - Math.cos(time/500.0 + i)), 1, 0.02, 0.02, 0.02, 1,
                            new Particle.DustOptions(c, 1));
                }
                break;
            case BASTION:
                level.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(level.getWorld(), position().x, position().y + 0.35, position().z), 2, 0.5, 0.5, 0.5, 0.1,
                        Material.POLISHED_BLACKSTONE_BRICKS.createBlockData());
                level.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(level.getWorld(), position().x, position().y + 0.35, position().z), 1, 0.5, 0.5, 0.5, 0.1,
                        Material.GILDED_BLACKSTONE.createBlockData());
                break;
            case ANCIENT:
                level.getWorld().spawnParticle(Particle.SCULK_SOUL,
                        new Location(level.getWorld(), position().x, position().y + 0.35, position().z), 2, 0.5, 0.5, 0.5, 0.1);
                level.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(level.getWorld(), position().x, position().y + 0.35, position().z), 1, 0.5, 0.5, 0.5, 0.1,
                        Material.SCULK.createBlockData());
                break;
            case CAVE:
                level.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(level.getWorld(), position().x, position().y + 0.35, position().z), 3, 0.5, 0.5, 0.5, 0.1,
                        Material.STONE.createBlockData());
                level.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(level.getWorld(), position().x, position().y + 0.35, position().z), 3, 0.5, 0.5, 0.5, 0.1,
                        Material.DIAMOND_ORE.createBlockData());
                break;
            case CONCRETE:
                level.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(level.getWorld(), position().x, position().y + 0.35, position().z), 3, 0.5, 0.5, 0.5, 0.1,
                        Material.WHITE_CONCRETE.createBlockData());
                break;
            case DESERT:
                level.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(level.getWorld(), position().x, position().y + 0.35, position().z), 3, 0.5, 0.5, 0.5, 0.1,
                        Material.SAND.createBlockData());
                break;
            case END:
                level.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(level.getWorld(), position().x, position().y + 0.35, position().z), 3, 0.5, 0.5, 0.5, 0.1,
                        Material.END_STONE.createBlockData());
                break;
            case FOOD:
                level.getWorld().spawnParticle(Particle.ITEM_CRACK,
                        new Location(level.getWorld(), position().x, position().y + 0.35, position().z), 3, 0.5, 0.5, 0.5, 0.1,
                        new ItemStack(Material.COOKED_CHICKEN));
                break;
            case MUSIC:
                level.getWorld().spawnParticle(Particle.NOTE,
                        new Location(level.getWorld(), position().x, position().y + 0.35, position().z), 3, 0.5, 0.5, 0.5, 0.1);
                break;
            case NETHER:
                level.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(level.getWorld(), position().x, position().y + 0.35, position().z), 1, 0.5, 0.5, 0.5, 0.1,
                        Material.NETHERRACK.createBlockData());
                level.getWorld().spawnParticle(Particle.FLAME,
                        new Location(level.getWorld(), position().x, position().y + 0.35, position().z), 2, 0.5, 0.5, 0.5, 0.1);
                break;
            case OVERWORLD:
                level.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(level.getWorld(), position().x, position().y + 0.35, position().z), 3, 0.5, 0.5, 0.5, 0.1,
                        Material.GRASS_BLOCK.createBlockData());
                break;
            case REDSTONE:
                level.getWorld().spawnParticle(Particle.REDSTONE,
                        new Location(level.getWorld(), position().x, position().y + 0.35, position().z), 3, 0.5, 0.5, 0.5, 1,
                        new Particle.DustOptions(Color.RED, 1));
                break;
            case TERRACOTTA:
                level.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                        new Location(level.getWorld(), position().x, position().y + 0.35, position().z), 3, 0.5, 0.5, 0.5, 0.1,
                        Material.TERRACOTTA.createBlockData());
                break;
            case UNDYING:
                level.getWorld().spawnParticle(Particle.TOTEM,
                        new Location(level.getWorld(), position().x, position().y + 0.35, position().z), 3, 0.5, 0.5, 0.5, 0.1);
                break;
            case UNSTABLE:
                level.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE,
                        new Location(level.getWorld(), position().x, position().y + 0.35, position().z), 10, 2.5, 2.5, 2.5, 0.1);
                if (System.currentTimeMillis() > nextEvent) {
                    nextEvent = (long) (500 + System.currentTimeMillis() + 4500 * Math.random());
                    Location l = new Location(level.getWorld(), Math.floor(position().x - 8 + Math.random() * 16.0) + 0.5, Math.round(position().y - 4 + Math.random() * 8.0), Math.floor(position().z - 8 + Math.random() * 16.0) + 0.5);
                    if (l.getBlock().getType().equals(Material.AIR) && l.getY() > 0 && l.getY() < 256) moveTo(l.getX(), l.getY(), l.getZ());
                }
                if (damageBufferTime != 0 && System.currentTimeMillis() > damageBufferTime && System.currentTimeMillis() > spawnTime + 5000) {
                    damageEntity0(damageBufferSource, damageBuffer);
                }
                break;
            case WITHER:
                level.getWorld().spawnParticle(Particle.REDSTONE,
                        new Location(level.getWorld(), position().x, position().y + 0.35, position().z), 3, 0.5, 0.5, 0.5, 1,
                        new Particle.DustOptions(Color.BLACK, 1));
                break;
            case STABLE:
                level.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE,
                        new Location(level.getWorld(), position().x, position().y + 0.35, position().z), 10, 2.5, 2.5, 2.5, 0.1);
                if (damageBufferTime != 0 && System.currentTimeMillis() > damageBufferTime && System.currentTimeMillis() > spawnTime + 5000) {
                    damageEntity0(damageBufferSource, damageBuffer);
                }
                break;
            default:
                break;
        }
        if (!found) {
            Collection<Entity> near = level.getWorld().getNearbyEntities(new Location(level.getWorld(), position().x, position().y, position().z), 10, 10, 10, (entity -> entity.getType() == EntityType.PLAYER));
            if (near.size() > 0) {
                found = true;
            }
        }
    }


    @Override
    protected void dropCustomDeathLoot(DamageSource damagesource, int i, boolean flag) {
        Level level = this.level();
        super.dropCustomDeathLoot(damagesource, i, flag);
        if (!noPhysics) {
            for (ItemStack itemStack : type.getLoot(isOnFire(), rarity, i)) {
                level.getWorld().dropItem(new Location(level.getWorld(), position().x, position().y, position().z), itemStack);
            }
        }
        ClientboundSystemChatPacket packet = new ClientboundSystemChatPacket(damagesource.getLocalizedDeathMessage(this), false);
        Bukkit.getOnlinePlayers().forEach(player -> ((CraftPlayer) player).getHandle().connection.send(packet));
        if (Bukkit.getServer().getPluginManager().getPlugin("Minecord") != null) {
            ExternalMessageEvent messageEvent = new ExternalMessageEvent(damagesource.getLocalizedDeathMessage(this).getString());
            Bukkit.getPluginManager().callEvent(messageEvent);
        }
        LoadedChicken c = null;
        for (LoadedChicken chicken : loadedChickens) {
            if (chicken.uuid.equals(uuid)) {
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

    // Runs when entities are loaded; Deletes every normal marked chicken and spawns a Resource Chicken in its place.
	public static void reInit(Main pl, Config config, List<Entity> entities) {
        if (entities.isEmpty()) return;
        for (Entity entity : entities) {
            if (!entity.getType().equals(EntityType.CHICKEN) || entity.getCustomName() == null || !entity.getCustomName().contains("§")) {
                continue;
            }
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
                ServerLevel worldServer = ((CraftWorld) entity.getWorld()).getHandle();
                worldServer.addFreshEntity(chicken);
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

    public static void deInit(Config config, List<Entity> entities) {
        if (entities.isEmpty()) return;
        for (Entity entity : entities) {
            if (entity.getType().equals(EntityType.CHICKEN) && entity.getCustomName() != null && entity.getCustomName().contains("§")) {
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

    @Override
    public boolean damageEntity0(DamageSource damagesource, float f) {
        if ((!type.equals(ResourceChickenType.UNSTABLE) && !type.equals(ResourceChickenType.STABLE)) || (damageBufferTime != 0 && System.currentTimeMillis() > damageBufferTime && System.currentTimeMillis() > spawnTime + 5000)) {
            damageBufferTime = 0;
            damageBuffer = 0;
            return super.damageEntity0(damagesource, f);
        } else {
            if (System.currentTimeMillis() > spawnTime + 5000) {
                if (damageBuffer == 0) {
                    damageBufferTime = System.currentTimeMillis() + 100;
                    damageBufferSource = damagesource;
                }
                damageBuffer += f;
                if (damagesource.is(DamageTypeTags.IS_LIGHTNING)) {
                        if (type.equals(ResourceChickenType.UNSTABLE)) type = ResourceChickenType.STABLE;
                        else if (type.equals(ResourceChickenType.STABLE)) type = ResourceChickenType.UNSTABLE;
                        spawnTime = System.currentTimeMillis();
                        damageBuffer = 0;
                        damageBufferTime = 0;
                        setCustomName(Component.literal("[" + rarity.color + rarity.toString() + ChatColor.RESET + "] " + type.color + type.name + ChatColor.RESET));
                }
            }
            return true;
        }
    }
}

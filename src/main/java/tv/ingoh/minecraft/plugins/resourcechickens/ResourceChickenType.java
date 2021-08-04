package tv.ingoh.minecraft.plugins.resourcechickens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import tv.ingoh.minecraft.plugins.resourcechickens.ResourceChicken.Rarity;

public enum ResourceChickenType {
    
    LUCKY("Lucky Chicken", ChatColor.GOLD, 0.01, List.of(new Loot(Material.DIAMOND, 1, 10, 20), new Loot(Material.GOLDEN_APPLE, 1, 5, 10), new LootGroup(1, new Loot(Material.DIAMOND_BLOCK, 1, 5, 10), new Loot(Material.ENCHANTED_GOLDEN_APPLE, 1, 1, 1), new Loot(Material.NETHERITE_INGOT, 1, 2, 6), new Loot(Material.HEART_OF_THE_SEA, 1, 1, 1)))),
    DIAMOND("Diamond Chicken", ChatColor.AQUA, 0.075, List.of(new Loot(Material.DIAMOND, 1, 1, 1), new Loot(Material.DIAMOND, 0.5, 1, 2), new Loot(Material.DIAMOND_ORE, 0.1, 1, 2), new Loot(Material.DIAMOND_BLOCK, 0.05, 1, 1))),
    GOLDEN_APPLE("Golden Apple Chicken", ChatColor.GOLD, 0.065, List.of(new Loot(Material.GOLDEN_APPLE, 1, 1, 1), new Loot(Material.GOLDEN_APPLE, 0.5, 1, 2), new Loot(Material.ENCHANTED_GOLDEN_APPLE, 0.01, 1, 1))),
    AQUATIC("Aquatic Chicken", ChatColor.BLUE, 0.05, List.of(new Loot(Material.SPONGE, 1, 1, 5), new Loot(Material.GRAVEL, 0.75, 1, 10), new Loot(Material.TRIDENT, 0.1, 1, 1), new Loot(Material.HEART_OF_THE_SEA, 0.03, 1, 1))),
    CAVE("Cave Chicken", ChatColor.GRAY, 0.075, List.of(new LootGroup(1.0, new Loot(Material.SANDSTONE, 1.0, 50, 100), new Loot(Material.GRANITE, 1.0, 50, 100), new Loot(Material.ANDESITE, 1.0, 50, 100), new Loot(Material.DIRT, 1.0, 50, 100), new Loot(Material.GRAVEL, 1.0, 50, 100), new Loot(Material.STONE, 1.0, 50, 100)), new LootGroup(0.5,new Loot(Material.COAL_ORE, 1.0, 10, 20), new Loot(Material.REDSTONE_ORE, 1.0, 10, 20), new Loot(Material.LAPIS_ORE, 1.0, 10, 20)),new LootGroup(0.3,new Loot(Material.IRON_ORE, 1.0, 5, 10), new Loot(Material.GOLD_ORE, 1.0, 5, 10)),new LootGroup(0.1,new Loot(Material.DIAMOND_ORE, 1.0, 5, 10), new Loot(Material.EMERALD_ORE, 1.0, 5, 10)))),
    OVERWORLD("Overworld Chicken", ChatColor.DARK_GREEN, 0.1, List.of(new LootGroup(1.0,new Loot(Material.TALL_GRASS, 1.0, 5, 10), new Loot(Material.LARGE_FERN, 1.0, 5, 10), new Loot(Material.DEAD_BUSH, 1.0, 5, 10)),new LootGroup(0.5,new Loot(Material.DANDELION, 1.0, 10, 20), new Loot(Material.POPPY, 1.0, 10, 20), new Loot(Material.BLUE_ORCHID, 1.0, 10, 20), new Loot(Material.ALLIUM, 1.0, 10, 20), new Loot(Material.AZURE_BLUET, 1.0, 10, 20), new Loot(Material.RED_TULIP, 1.0, 10, 20), new Loot(Material.ORANGE_TULIP, 1.0, 10, 20), new Loot(Material.WHITE_TULIP, 1.0, 10, 20), new Loot(Material.PINK_TULIP, 1.0, 10, 20), new Loot(Material.OXEYE_DAISY, 1.0, 10, 20), new Loot(Material.CORNFLOWER, 1.0, 10, 20), new Loot(Material.LILY_OF_THE_VALLEY, 1.0, 10, 20), new Loot(Material.WITHER_ROSE, 1.0, 10, 20)),new LootGroup(0.25,new Loot(Material.OAK_LOG, 1.0, 10, 20), new Loot(Material.SPRUCE_LOG, 1.0, 10, 20), new Loot(Material.BIRCH_LOG, 1.0, 10, 20), new Loot(Material.JUNGLE_LOG, 1.0, 10, 20), new Loot(Material.ACACIA_LOG, 1.0, 10, 20), new Loot(Material.DARK_OAK_LOG, 1.0, 10, 20)),new LootGroup(0.01,new Loot(Material.ZOMBIE_HEAD, 1.0, 1, 1), new Loot(Material.SKELETON_SKULL, 1.0, 1, 1), new Loot(Material.CREEPER_HEAD, 1.0, 1, 1)))),
    NETHER("Nether Chicken", ChatColor.DARK_RED, 0.075, List.of(new Loot(Material.NETHERRACK, 1.0, 20, 50), new Loot(Material.SOUL_SAND, 0.8, 10, 20), new LootGroup(0.4,new Loot(Material.NETHER_QUARTZ_ORE, 0.25, 5, 10), new Loot(Material.QUARTZ, 0.75, 10, 20)), new Loot(Material.ANCIENT_DEBRIS, 0.05, 1, 3))),
    END("End Chicken", ChatColor.YELLOW, 0.05, List.of(new Loot(Material.END_STONE, 1.0, 20, 50),new LootGroup(0.5, 2, new Loot(Material.DRAGON_HEAD, 1.0, 1, 1), new Loot(Material.DRAGON_EGG, 1.0, 1, 1), new Loot(Material.ELYTRA, 1.0, 1, 1), new Loot(Material.SHULKER_SHELL, 1.0, 1, 3)))),
    REDSTONE("Redstone Chicken", ChatColor.RED, 0.05, List.of(new Loot(Material.REDSTONE, 1.0, 20, 50),new LootGroup(0.5, 10, new Loot(Material.REDSTONE_BLOCK, 1.0, 10, 20), new Loot(Material.DISPENSER, 1.0, 10, 20), new Loot(Material.DROPPER, 1.0, 10, 20), new Loot(Material.STICKY_PISTON, 1.0, 10, 20), new Loot(Material.PISTON, 1.0, 10, 20), new Loot(Material.REDSTONE_TORCH, 1.0, 10, 20), new Loot(Material.HOPPER, 1.0, 10, 20), new Loot(Material.OBSERVER, 1.0, 10, 20), new Loot(Material.DAYLIGHT_DETECTOR, 1.0, 10, 20), new Loot(Material.REPEATER, 1.0, 10, 20), new Loot(Material.COMPARATOR, 1.0, 10, 20), new Loot(Material.TARGET, 1.0, 10, 20), new Loot(Material.TRIPWIRE_HOOK, 1.0, 10, 20), new Loot(Material.SLIME_BLOCK, 1.0, 10, 20), new Loot(Material.HONEY_BLOCK, 1.0, 10, 20)))),
    BASTION("Bastion Chicken", ChatColor.BLACK, 0.05, List.of(new LootGroup(1.0,new Loot(Material.CHAIN, 1.0, 20, 50), new Loot(Material.BLACKSTONE, 1.0, 20, 50), new Loot(Material.POLISHED_BLACKSTONE, 1.0, 20, 50), new Loot(Material.CHISELED_POLISHED_BLACKSTONE, 1.0, 20, 50), new Loot(Material.CRACKED_POLISHED_BLACKSTONE_BRICKS, 1.0, 20, 50), new Loot(Material.POLISHED_BLACKSTONE_BRICKS, 1.0, 20, 50), new Loot(Material.BASALT, 1.0, 20, 50), new Loot(Material.POLISHED_BASALT, 1.0, 20, 50)),new LootGroup(0.5,new Loot(Material.GILDED_BLACKSTONE, 1.0, 5, 10), new Loot(Material.GOLD_BLOCK, 1.0, 5, 10), new Loot(Material.GOLDEN_CARROT, 1.0, 5, 10), new Loot(Material.GOLDEN_APPLE, 1.0, 5, 10)),new LootGroup(0.1,new Loot(Material.CRYING_OBSIDIAN, 1.0, 5, 10), new Loot(Material.LODESTONE, 1.0, 1, 1)),new LootGroup(0.05,new Loot(Material.ENCHANTED_GOLDEN_APPLE, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_PIGSTEP, 1.0, 1, 1), new Loot(Material.PIGLIN_BANNER_PATTERN, 1.0, 1, 1), new Loot(Material.ANCIENT_DEBRIS, 1.0, 1, 1), new Loot(Material.NETHERITE_SCRAP, 1.0, 1, 1), new Loot(Material.NETHERITE_INGOT, 1.0, 1, 1)))),
    UNSTABLE("Unstable Chicken", ChatColor.MAGIC, 0.025, List.of(new LootGroup(0.005,new Loot(Material.ENCHANTED_GOLDEN_APPLE, 1.0, 1, 100, true, 1.0/3.0), new Loot(Material.NETHERITE_INGOT, 1.0, 1, 100, true, 1.0/3.0), new Loot(Material.DIAMOND_BLOCK, 1.0, 1, 100, true, 1.0/3.0), new Loot(Material.HEART_OF_THE_SEA, 1.0, 1, 100, true, 1.0/3.0), new Loot(Material.MUSIC_DISC_PIGSTEP, 1.0, 1, 100, true, 1.0/3.0)))),
    CONCRETE("Concrete Chicken", ChatColor.WHITE, 0.075, List.of(new LootGroup(0.5, 3, new Loot(Material.WHITE_CONCRETE, 1.0, 50, 100), new Loot(Material.ORANGE_CONCRETE, 1.0, 50, 100), new Loot(Material.MAGENTA_CONCRETE, 1.0, 50, 100), new Loot(Material.LIGHT_BLUE_CONCRETE, 1.0, 50, 100), new Loot(Material.YELLOW_CONCRETE, 1.0, 50, 100), new Loot(Material.LIME_CONCRETE, 1.0, 50, 100), new Loot(Material.PINK_CONCRETE, 1.0, 50, 100), new Loot(Material.GRAY_CONCRETE, 1.0, 50, 100), new Loot(Material.LIGHT_GRAY_CONCRETE, 1.0, 50, 100), new Loot(Material.CYAN_CONCRETE, 1.0, 50, 100), new Loot(Material.PURPLE_CONCRETE, 1.0, 50, 100), new Loot(Material.BLUE_CONCRETE, 1.0, 50, 100), new Loot(Material.BROWN_CONCRETE, 1.0, 50, 100), new Loot(Material.GREEN_CONCRETE, 1.0, 50, 100), new Loot(Material.RED_CONCRETE, 1.0, 50, 100), new Loot(Material.BLACK_CONCRETE, 1.0, 50, 100)),new LootGroup(0.5, 3,new Loot(Material.WHITE_CONCRETE_POWDER, 1.0, 50, 100), new Loot(Material.ORANGE_CONCRETE_POWDER, 1.0, 50, 100), new Loot(Material.MAGENTA_CONCRETE_POWDER, 1.0, 50, 100), new Loot(Material.LIGHT_BLUE_CONCRETE_POWDER, 1.0, 50, 100), new Loot(Material.YELLOW_CONCRETE_POWDER, 1.0, 50, 100), new Loot(Material.LIME_CONCRETE_POWDER, 1.0, 50, 100), new Loot(Material.PINK_CONCRETE_POWDER, 1.0, 50, 100), new Loot(Material.GRAY_CONCRETE_POWDER, 1.0, 50, 100), new Loot(Material.LIGHT_GRAY_CONCRETE_POWDER, 1.0, 50, 100), new Loot(Material.CYAN_CONCRETE_POWDER, 1.0, 50, 100), new Loot(Material.PURPLE_CONCRETE_POWDER, 1.0, 50, 100), new Loot(Material.BLUE_CONCRETE_POWDER, 1.0, 50, 100), new Loot(Material.BROWN_CONCRETE_POWDER, 1.0, 50, 100), new Loot(Material.GREEN_CONCRETE_POWDER, 1.0, 50, 100), new Loot(Material.RED_CONCRETE_POWDER, 1.0, 50, 100), new Loot(Material.BLACK_CONCRETE_POWDER, 1.0, 50, 100)))),
    TERRACOTTA("Terracotta Chicken", ChatColor.RED, 0.05, List.of(new LootGroup(0.5, 3, new Loot(Material.WHITE_TERRACOTTA, 1.0, 50, 100), new Loot(Material.ORANGE_TERRACOTTA, 1.0, 50, 100), new Loot(Material.MAGENTA_TERRACOTTA, 1.0, 50, 100), new Loot(Material.LIGHT_BLUE_TERRACOTTA, 1.0, 50, 100), new Loot(Material.YELLOW_TERRACOTTA, 1.0, 50, 100), new Loot(Material.LIME_TERRACOTTA, 1.0, 50, 100), new Loot(Material.PINK_TERRACOTTA, 1.0, 50, 100), new Loot(Material.GRAY_TERRACOTTA, 1.0, 50, 100), new Loot(Material.LIGHT_GRAY_TERRACOTTA, 1.0, 50, 100), new Loot(Material.CYAN_TERRACOTTA, 1.0, 50, 100), new Loot(Material.PURPLE_TERRACOTTA, 1.0, 50, 100), new Loot(Material.BLUE_TERRACOTTA, 1.0, 50, 100), new Loot(Material.BROWN_TERRACOTTA, 1.0, 50, 100), new Loot(Material.GREEN_TERRACOTTA, 1.0, 50, 100), new Loot(Material.RED_TERRACOTTA, 1.0, 50, 100), new Loot(Material.BLACK_TERRACOTTA, 1.0, 50, 100)),new LootGroup(0.5, 3,new Loot(Material.WHITE_GLAZED_TERRACOTTA, 1.0, 50, 100), new Loot(Material.ORANGE_GLAZED_TERRACOTTA, 1.0, 50, 100), new Loot(Material.MAGENTA_GLAZED_TERRACOTTA, 1.0, 50, 100), new Loot(Material.LIGHT_BLUE_GLAZED_TERRACOTTA, 1.0, 50, 100), new Loot(Material.YELLOW_GLAZED_TERRACOTTA, 1.0, 50, 100), new Loot(Material.LIME_GLAZED_TERRACOTTA, 1.0, 50, 100), new Loot(Material.PINK_GLAZED_TERRACOTTA, 1.0, 50, 100), new Loot(Material.GRAY_GLAZED_TERRACOTTA, 1.0, 50, 100), new Loot(Material.LIGHT_GRAY_GLAZED_TERRACOTTA, 1.0, 50, 100), new Loot(Material.CYAN_GLAZED_TERRACOTTA, 1.0, 50, 100), new Loot(Material.PURPLE_GLAZED_TERRACOTTA, 1.0, 50, 100), new Loot(Material.BLUE_GLAZED_TERRACOTTA, 1.0, 50, 100), new Loot(Material.BROWN_GLAZED_TERRACOTTA, 1.0, 50, 100), new Loot(Material.GREEN_GLAZED_TERRACOTTA, 1.0, 50, 100), new Loot(Material.RED_GLAZED_TERRACOTTA, 1.0, 50, 100), new Loot(Material.BLACK_GLAZED_TERRACOTTA, 1.0, 50, 100)))),
    DESERT("Desert Chicken", ChatColor.YELLOW, 0.05, List.of(new Loot(Material.SAND, 1.0, 50, 100),new LootGroup(0.5,new Loot(Material.CACTUS, 1.0, 10, 20), new Loot(Material.DEAD_BUSH, 1.0, 10, 20)),new LootGroup(0.25,new Loot(Material.SANDSTONE, 1.0, 50, 100), new Loot(Material.CHISELED_SANDSTONE, 1.0, 50, 100), new Loot(Material.CUT_SANDSTONE, 1.0, 50, 100), new Loot(Material.SMOOTH_SANDSTONE, 1.0, 50, 100)))),
    WITHER("Wither Chicken", ChatColor.WHITE, 0.05, List.of(new Loot(Material.SOUL_SAND, 1.0, 10, 20),new Loot(Material.WITHER_SKELETON_SKULL, 0.5, 1, 3),new Loot(Material.NETHER_STAR, 0.05, 1, 1))),
    MUSIC("Music Chicken", ChatColor.DARK_PURPLE, 0.05, List.of(new LootGroup(1,new Loot(Material.MUSIC_DISC_13, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_CAT, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_BLOCKS, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_CHIRP, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_FAR, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_MALL, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_MELLOHI, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_STAL, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_STRAD, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_WARD, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_11, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_WAIT, 1.0, 1, 1)), new LootGroup(0.2, 5,new Loot(Material.MUSIC_DISC_13, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_CAT, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_BLOCKS, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_CHIRP, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_FAR, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_MALL, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_MELLOHI, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_STAL, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_STRAD, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_WARD, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_11, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_WAIT, 1.0, 1, 1)),new LootGroup(0.2,new Loot(Material.MUSIC_DISC_13, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_CAT, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_BLOCKS, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_CHIRP, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_FAR, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_MALL, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_MELLOHI, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_STAL, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_STRAD, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_WARD, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_11, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_WAIT, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_PIGSTEP, 1.0, 1, 1)))),
    FOOD("Food Chicken", ChatColor.LIGHT_PURPLE, 0.05, List.of(new LootGroup(1.0,new Loot(Material.APPLE, 1.0, 10, 20), new Loot(Material.BREAD, 1.0, 10, 20), new Loot(Material.PORKCHOP, 1.0, 10, 20), new Loot(Material.COD, 1.0, 10, 20), new Loot(Material.SALMON, 1.0, 10, 20), new Loot(Material.TROPICAL_FISH, 1.0, 10, 20), new Loot(Material.PUFFERFISH, 1.0, 10, 20), new Loot(Material.COOKIE, 1.0, 10, 20), new Loot(Material.MELON_SLICE, 1.0, 10, 20), new Loot(Material.DRIED_KELP, 1.0, 10, 20), new Loot(Material.BEEF, 1.0, 10, 20), new Loot(Material.CHICKEN, 1.0, 10, 20), new Loot(Material.ROTTEN_FLESH, 1.0, 10, 20), new Loot(Material.SPIDER_EYE, 1.0, 10, 20), new Loot(Material.CARROT, 1.0, 10, 20), new Loot(Material.POTATO, 1.0, 10, 20), new Loot(Material.POISONOUS_POTATO, 1.0, 10, 20), new Loot(Material.RABBIT, 1.0, 10, 20), new Loot(Material.MUTTON, 1.0, 10, 20), new Loot(Material.BEETROOT, 1.0, 10, 20), new Loot(Material.SWEET_BERRIES, 1.0, 10, 20), new Loot(Material.HONEY_BOTTLE, 1.0, 10, 20)),new LootGroup(0.5,new Loot(Material.COOKED_PORKCHOP, 1.0, 1, 3), new Loot(Material.GOLDEN_APPLE, 1.0, 1, 3), new Loot(Material.GOLDEN_CARROT, 1.0, 1, 3), new Loot(Material.COOKED_COD, 1.0, 1, 3), new Loot(Material.COOKED_SALMON, 1.0, 1, 3), new Loot(Material.COOKED_BEEF, 1.0, 1, 3), new Loot(Material.COOKED_CHICKEN, 1.0, 1, 3), new Loot(Material.BAKED_POTATO, 1.0, 1, 3), new Loot(Material.PUMPKIN_PIE, 1.0, 1, 3), new Loot(Material.COOKED_RABBIT, 1.0, 1, 3), new Loot(Material.RABBIT_STEW, 1.0, 1, 3), new Loot(Material.COOKED_MUTTON, 1.0, 1, 3)),new Loot(Material.ENCHANTED_GOLDEN_APPLE, 0.01, 1, 1))),
    UNDYING("Undying Chicken", ChatColor.GREEN, 0.05, List.of(new Loot(Material.TOTEM_OF_UNDYING, 1.0, 1, 1),new Loot(Material.DIAMOND, 0.25, 1, 3),new LootGroup(0.01,new Loot(Material.ENCHANTED_GOLDEN_APPLE, 1.0, 1, 1), new Loot(Material.NETHERITE_INGOT, 1.0, 1, 1), new Loot(Material.DIAMOND_BLOCK, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_PIGSTEP, 1.0, 1, 1), new Loot(Material.HEART_OF_THE_SEA, 1.0, 1, 1)))),
    INVALID("Invalid Chicken", ChatColor.DARK_RED, 0, List.of(new Loot(Material.BRICK, 1.0, 1, 1))),
    STABLE("Stabilized Unstable Chicken", ChatColor.WHITE, 0, List.of(new LootGroup(0.025,new Loot(Material.ENCHANTED_GOLDEN_APPLE, 1.0, 1, 1), new Loot(Material.NETHERITE_INGOT, 1.0, 1, 1), new Loot(Material.DIAMOND_BLOCK, 1.0, 1, 1), new Loot(Material.HEART_OF_THE_SEA, 1.0, 1, 1), new Loot(Material.MUSIC_DISC_PIGSTEP, 1.0, 1, 1))));

    String name;
    ChatColor color;
    Collection<Drops> loot;
    double weight;

    ResourceChickenType(String name, ChatColor color, double weight, Collection<Drops> loot) {
        this.name = name;
        this.color = color;
        this.loot = loot;
        this.weight = weight;
    }

    public ArrayList<ItemStack> getLoot(boolean isBurning, Rarity rarity) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        for (Drops l : loot) {
            drops.addAll(l.getLoot(isBurning, rarity));
        }
        return drops;
    }

    
    public ArrayList<String> listLoot() {
        ArrayList<String> strings = new ArrayList<>();
        loot.forEach(l -> {
            strings.addAll(l.list());
        });
        return strings;
    }

	public static ResourceChickenType random() {
        double totalPrb = 0;
        for (ResourceChickenType type : ResourceChickenType.values()) {
            totalPrb += type.weight;
        }
        double prb = Math.random() * totalPrb;
        double curPrb = 0;
        for (ResourceChickenType type : ResourceChickenType.values()) {
            curPrb += type.weight;
            if (curPrb > prb) return type;
        }
        return INVALID;
    }
    

}

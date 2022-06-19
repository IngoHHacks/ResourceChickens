package tv.ingoh.minecraft.plugins.resourcechickens;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import org.bukkit.ChatColor;
import tv.ingoh.minecraft.plugins.resourcechickens.ResourceChicken.Rarity;

public class Loot extends Drops {
    Material item;
    double prb;
    int min;
    int max;
    boolean exponential = false;
    double expIncPrb = 0.5;
    
    Loot (Material item, double prb, int min, int max) {
        this.item = item;
        this.prb = prb;
        this.min = min;
        this.max = max;
    }

    Loot (Material item, double prb, int min, int max, boolean exponential) {
        this.item = item;
        this.prb = prb;
        this.min = min;
        this.max = max;
        this.exponential = exponential;
    }

    Loot (Material item, double prb, int min, int max, boolean exponential, double expIncPrb) {
        this.item = item;
        this.prb = prb;
        this.min = min;
        this.max = max;
        this.exponential = exponential;
        this.expIncPrb = expIncPrb;
    }

    @Override
    public ArrayList<ItemStack> getLoot(boolean isBurning, Rarity rarity) {
        return getLoot(isBurning, rarity, false);
    }

    public ArrayList<ItemStack> getLoot(boolean isBurning, Rarity rarity, boolean force) {
        Material tItem = item;
        
        // Change sand type items to red sand type if burning
        if (item.equals(Material.SAND) && isBurning) tItem = Material.RED_SAND;
        else if (item.equals(Material.SANDSTONE) && isBurning) tItem = Material.RED_SANDSTONE;
        else if (item.equals(Material.CUT_SANDSTONE) && isBurning) tItem = Material.CUT_RED_SANDSTONE;
        else if (item.equals(Material.CHISELED_SANDSTONE) && isBurning) tItem = Material.CHISELED_RED_SANDSTONE;
        else if (item.equals(Material.SMOOTH_SANDSTONE) && isBurning) tItem = Material.SMOOTH_RED_SANDSTONE;

        int count = min;
        if (exponential) while (Math.random() < expIncPrb && count < max);
        else count = (int)(Math.round(Math.random() * (max - min)) + min);
        count *= rarity.dropMultiplier;
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        double rng = Math.random() / rarity.luckMultiplier;
        if (force || rng < prb) {
            // Split items into stacks
            int stacks = count / tItem.getMaxStackSize();
            int items = count % tItem.getMaxStackSize();
            for (int i = 0; i < stacks; i++) itemStacks.add(new ItemStack(tItem, tItem.getMaxStackSize()));
            ItemStack stack = new ItemStack(tItem, items);
            if (tItem == Material.ENCHANTED_BOOK) {
                stack.getItemMeta().addEnchant(Enchantment.SWEEPING_EDGE, (int)(Math.random() * 3), false);
            }
            if (items > 0) itemStacks.add(stack);
        }
        return itemStacks;
    }

    @Override
    ArrayList<String> list() {
        ArrayList<String> items = new ArrayList<>();
        if (exponential) items.add(ChatColor.GREEN + "[" + Math.round(prb * 10000.0) / 100.0 + "%] " + ChatColor.GOLD + item.name().replace("_", " ") + ChatColor.RED + " (" + min + "-" + max + "*)");
        else items.add(ChatColor.GREEN + "[" + Math.round(prb * 10000.0) / 100.0 + "%] " + ChatColor.GOLD + item.name().replace("_", " ") + ChatColor.RED + " (" + min + "-" + max + ")");
        return items;
    }


    ArrayList<String> list(double p) {
        ArrayList<String> items = new ArrayList<>();
        if (exponential) items.add(ChatColor.GREEN + "[" + Math.round(prb * p* 10000.0) / 100.0 + "%] " + ChatColor.GOLD + item.name().replace("_", " ") + ChatColor.RED + "(" + min + "-" + max + "*)");
        else items.add(ChatColor.GREEN + "[" + Math.round(prb * p * 10000.0) / 100.0 + "%] " + ChatColor.GOLD + item.name().replace("_", " ") + ChatColor.RED + " (" + min + "-" + max + ")");
        return items;
    }

    @Override
    ArrayList<String> listRaw() {
        ArrayList<String> items = new ArrayList<>();
        if (exponential) items.add("[" + Math.round(prb * 10000.0) / 100.0 + "%] " + item.name().replace("_", " ") + " (" + (min == max ? min : (min + "-" + max)) + "*)" );
        else items.add("[" + Math.round(prb * 10000.0) / 100.0 + "%] " + item.name().replace("_", " ") + " (" + (min == max ? min : (min + "-" + max)) + ")");
        return items;
    }

    ArrayList<String> listRaw(double p) {
        ArrayList<String> items = new ArrayList<>();
        if (exponential) items.add("[" + Math.round(prb * p* 10000.0) / 100.0 + "%] " + item.name().replace("_", " ") + "(" + (min == max ? min : (min + "-" + max)) + "*)");
        else items.add("[" + Math.round(prb * p * 10000.0) / 100.0 + "%] " + item.name().replace("_", " ") + " (" + (min == max ? min : (min + "-" + max)) + ")");
        return items;
    }

}
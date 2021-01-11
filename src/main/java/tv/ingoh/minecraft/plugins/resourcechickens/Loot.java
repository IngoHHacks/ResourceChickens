package tv.ingoh.minecraft.plugins.resourcechickens;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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
            if (items > 0) itemStacks.add(new ItemStack(tItem, items));
        }
        return itemStacks;
    }
}
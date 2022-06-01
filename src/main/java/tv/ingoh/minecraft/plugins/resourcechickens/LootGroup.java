package tv.ingoh.minecraft.plugins.resourcechickens;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import org.bukkit.ChatColor;
import tv.ingoh.minecraft.plugins.resourcechickens.ResourceChicken.Rarity;

public class LootGroup extends Drops {

    ArrayList<Loot> loot = new ArrayList<>();
    double prb;
    int count = 1;

    public LootGroup(double prb, Loot... loots) {
        this.prb = prb;
        loot.addAll(List.of(loots));
    }

    public LootGroup(double prb, int count, Loot... loots) {
        this.prb = prb;
        this.count = count;
        loot.addAll(List.of(loots));
    }

    @Override
    ArrayList<ItemStack> getLoot(boolean isBurning, Rarity rarity) {
        ArrayList<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double totalPrb = 0;
            for (Loot l : loot) {
                totalPrb += l.prb;
            }
            double prb2 = Math.random() * totalPrb;
            double curPrb = 0;
            for (Loot l : loot) {
                curPrb += l.prb;
                if (curPrb > prb2) {
                    double rng = Math.random() / rarity.luckMultiplier;
                    if (rng < prb) {
                        items.addAll(l.getLoot(isBurning, rarity, true));
                    }
                    break;
                }
            }
        }
        return items;
    }

    @Override
    ArrayList<String> list() {
        ArrayList<String> items = new ArrayList<>();
        items.add(ChatColor.DARK_PURPLE + "[LIST]");
        double totalPrb = 0;
        for (Loot l : loot) {
            totalPrb += l.prb;
        }
        for (Loot l : loot) {
            items.addAll(l.list(prb / totalPrb));
        }
        items.add(ChatColor.DARK_PURPLE + "[END OF LIST]");
        return items;
    }

    @Override
    ArrayList<String> listRaw() {
        ArrayList<String> items = new ArrayList<>();
        items.add("[LIST]");
        double totalPrb = 0;
        for (Loot l : loot) {
            totalPrb += l.prb;
        }
        for (Loot l : loot) {
            items.addAll(l.listRaw(prb / totalPrb));
        }
        items.add("[END OF LIST]");
        return items;
    }
}

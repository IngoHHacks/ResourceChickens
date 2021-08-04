package tv.ingoh.util;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

import tv.ingoh.minecraft.plugins.resourcechickens.ResourceChickenType;
import tv.ingoh.minecraft.plugins.resourcechickens.ResourceChicken.Rarity;

public class Simulate {
    
    public static void main(String[] args) {
        simulate(ResourceChickenType.WITHER, Rarity.EPIC);
    }

    private static void simulate(ResourceChickenType type, Rarity rarity) {
        ArrayList<ItemStack> list = type.getLoot(false, rarity);
        list.forEach(item -> {
            System.out.println(item.getAmount() + "*" + item.getType());
        });
    }
}

package net.ingoh.util;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

import net.ingoh.minecraft.plugins.resourcechickens.ResourceChickenType;
import net.ingoh.minecraft.plugins.resourcechickens.ResourceChicken.Rarity;

public class Simulate {
    
    public static void main(String[] args) {
        simulate(ResourceChickenType.UNSTABLE, Rarity.UNCOMMON, 3);
    }

    private static void simulate(ResourceChickenType type, Rarity rarity, int looting) {
        ArrayList<ItemStack> list = type.getLoot(false, rarity, looting);
        System.out.println("Items dropped: ");
        list.forEach(item -> {
            System.out.println(item.getAmount() + "*" + item.getType());
        });
    }
}

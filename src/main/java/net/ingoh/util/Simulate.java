package net.ingoh.util;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

import net.ingoh.minecraft.plugins.resourcechickens.ResourceChickenType;
import net.ingoh.minecraft.plugins.resourcechickens.ResourceChicken.Rarity;

public class Simulate {
    
    public static void main(String[] args) {
        simulate(ResourceChickenType.WITHER, Rarity.EPIC, 0);
    }

    private static void simulate(ResourceChickenType type, Rarity rarity, int looting) {
        ArrayList<ItemStack> list = type.getLoot(false, rarity, looting);
        list.forEach(item -> {
            System.out.println(item.getAmount() + "*" + item.getType());
        });
    }
}

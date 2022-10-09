package net.ingoh.minecraft.plugins.resourcechickens;

import java.util.ArrayList;

public class PrintLoot {
    public static void main(String[] args) {
        for (ResourceChickenType type : ResourceChickenType.values()) {
            String name = type.name();
            ArrayList<String> loot = type.listLootRaw();
            System.out.println("== " + name + " (" + type.weight + ") ==");
            loot.forEach(System.out::println);
        }
    }
}

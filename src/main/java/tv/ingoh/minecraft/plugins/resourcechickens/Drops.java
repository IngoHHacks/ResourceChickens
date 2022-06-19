package tv.ingoh.minecraft.plugins.resourcechickens;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

import tv.ingoh.minecraft.plugins.resourcechickens.ResourceChicken.Rarity;

public abstract class Drops {
    abstract ArrayList<ItemStack> getLoot(boolean isBurning, Rarity rarity, int looting);
    abstract ArrayList<String> list();
    abstract ArrayList<String> listRaw();
}

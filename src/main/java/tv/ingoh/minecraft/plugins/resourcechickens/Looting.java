package tv.ingoh.minecraft.plugins.resourcechickens;

public class Looting {
    public static double getLuckMultiplier(int looting) {
        return 1.0 + 0.1 * looting;
    }

    public static double getDropMultiplier(int looting) {
        return 1.0 + 0.1 * looting;
    }
}

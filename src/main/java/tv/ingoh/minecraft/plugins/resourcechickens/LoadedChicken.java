package tv.ingoh.minecraft.plugins.resourcechickens;

import java.util.UUID;

public class LoadedChicken {

    public ResourceChicken chicken;
    public UUID uuid;

    public LoadedChicken(ResourceChicken chicken, UUID uuid) {
        this.chicken = chicken;
        this.uuid = uuid;
    }
    
}

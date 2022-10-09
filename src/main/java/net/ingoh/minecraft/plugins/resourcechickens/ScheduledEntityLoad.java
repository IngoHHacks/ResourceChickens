package net.ingoh.minecraft.plugins.resourcechickens;

import org.bukkit.Chunk;
import org.bukkit.entity.Entity;

public class ScheduledEntityLoad {

    private Chunk chunk;
    private long time;

    public ScheduledEntityLoad(Chunk chunk, long time) {
        this.chunk = chunk;
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public Entity[] getEntities() {
        return chunk.getEntities();
    }

}

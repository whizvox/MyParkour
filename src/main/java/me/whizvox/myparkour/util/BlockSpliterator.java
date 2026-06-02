package me.whizvox.myparkour.util;

import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Spliterator;
import java.util.function.Consumer;

public class BlockSpliterator implements Spliterator<Block> {

    private final World world;
    private final int minX;
    private final int minY;
    private final int minZ;

    private final int width;
    private final int depth;
    private final int size;
    private int index;

    public BlockSpliterator(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.world = world;
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        width = maxX - minX;
        depth = maxZ - minZ;
        size = width * depth * (maxY - minY);
        index = 0;
    }

    @Override
    public boolean tryAdvance(Consumer<? super Block> action) {
        if (index < size) {
            int x = minX + index % width;
            int z = minZ + (index / width) % depth;
            int y = minY + index / (width + depth);
            action.accept(world.getBlockAt(x, y, z));
            index++;
            return true;
        }
        return false;
    }

    @Override
    public Spliterator<Block> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return size;
    }

    @Override
    public int characteristics() {
        return SIZED | CONCURRENT | DISTINCT | IMMUTABLE | NONNULL | ORDERED;
    }

}

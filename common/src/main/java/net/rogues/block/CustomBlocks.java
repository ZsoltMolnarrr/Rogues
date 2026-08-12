package net.rogues.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.rogues.RoguesMod;

import java.util.ArrayList;

public class CustomBlocks {

    public record Entry(String name, Block block, BlockItem item) {
        public Entry(String name, Block block) {
            this(name, block, new BlockItem(block, new Item.Settings()));
        }
    }

    public static final ArrayList<Entry> all = new ArrayList<>();

    private static Entry entry(String name, Block block) {
        var entry = new Entry(name, block);
        all.add(entry);
        return entry;
    }

    public static final Entry WORKBENCH = entry(MartialWorkbenchBlock.ID.getPath(), new MartialWorkbenchBlock(
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.OAK_TAN)
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(2.5F)
                    .sounds(BlockSoundGroup.WOOD)
                    .nonOpaque()
    ));

    public static void register() {
        for (var entry : all) {
            Registry.register(Registries.BLOCK, Identifier.of(RoguesMod.NAMESPACE, entry.name), entry.block);
            Registry.register(Registries.ITEM, Identifier.of(RoguesMod.NAMESPACE, entry.name), entry.item());
        }
        // Creative-tab placement (into the Rogues group) is registered per-platform from each loader's
        // entrypoint, iterating CustomBlocks.all — no Fabric API ItemGroupEvents in common.
    }
}

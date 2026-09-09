package net.rogues.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.Instrument;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.rogues.RoguesMod;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class CustomBlocks {

    public record Entry(String name, Block block, BlockItem item) {
        public Entry(String name, Block block) {
            this(name, block, new BlockItem(block, new Item.Settings()));
        }

        /// The id both the block and its block item register under.
        public Identifier id() {
            return new Identifier(RoguesMod.NAMESPACE, name);
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
                    .instrument(Instrument.BASS)
                    .strength(2.5F)
                    .sounds(BlockSoundGroup.WOOD)
                    .nonOpaque()
    ));

    /// Split from {@link #registerItems()} for Forge 47: `RegisterEvent` opens one window per registry and
    /// locks every other one, so blocks and block items cannot be registered from the same window.
    public static void registerBlocks() {
        blocksToRegister().forEach((id, block) -> Registry.register(Registries.BLOCK, id, block));
    }

    public static void registerItems() {
        blockItemsToRegister().forEach((id, item) -> Registry.register(Registries.ITEM, id, item));
        // Creative-tab placement (into the Rogues group) is loader-neutral, dispatched by SpellEngine's
        // `PlatformEvents.onItemGroupModify` from `RoguesMod.registerItems()` — registered there ahead of
        // the weapon/armor registrations so the blocks come first in the tab.
    }

    /// The blocks keyed by the id they register under. Creation only — nothing is written here, so a
    /// loader that registers blocks itself (Forge) iterates this instead of {@link #registerBlocks()}.
    /// Class init builds each block *and* its `BlockItem`; both are constructed here, which is fine as
    /// long as this runs inside the `RegisterEvent` sequence.
    public static Map<Identifier, Block> blocksToRegister() {
        var blocks = new LinkedHashMap<Identifier, Block>();
        for (var entry : all) {
            if (Registries.BLOCK.containsId(entry.id())) { continue; }
            blocks.put(entry.id(), entry.block());
        }
        return blocks;
    }

    /// The block items keyed by the id they register under. Creation only — see {@link #blocksToRegister()}.
    public static Map<Identifier, Item> blockItemsToRegister() {
        var items = new LinkedHashMap<Identifier, Item>();
        for (var entry : all) {
            if (Registries.ITEM.containsId(entry.id())) { continue; }
            items.put(entry.id(), entry.item());
        }
        return items;
    }
}

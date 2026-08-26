package net.rogues.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.rogues.RoguesMod;

import java.util.ArrayList;
import java.util.function.Consumer;

public class CustomBlocks {

    public record Entry(String name, Block block, BlockItem item) { }

    public static final ArrayList<Entry> all = new ArrayList<>();

    private static Entry entry(Identifier id, Block block, BlockItem item) {
        var entry = new Entry(id.getPath(), block, item);
        all.add(entry);
        return entry;
    }

    /// 1.21.5+: `Block#appendTooltip` is gone — the hint line is appended by the block's item instead.
    private static BlockItem hintedBlockItem(Block block, Identifier id) {
        var settings = new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, id))
                .useBlockDescriptionPrefix();
        return new BlockItem(block, settings) {
            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay displayComponent,
                                      Consumer<Component> textConsumer, TooltipFlag type) {
                super.appendHoverText(stack, context, displayComponent, textConsumer, type);
                textConsumer.accept(Component.translatable("block." + id.getNamespace() + "." + id.getPath() + ".hint")
                        .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }
        };
    }

    private static final Block WORKBENCH_BLOCK = new MartialWorkbenchBlock(
            BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, MartialWorkbenchBlock.ID))
                    .mapColor(MapColor.WOOD)
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(2.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()
    );

    public static final Entry WORKBENCH = entry(MartialWorkbenchBlock.ID, WORKBENCH_BLOCK,
            hintedBlockItem(WORKBENCH_BLOCK, MartialWorkbenchBlock.ID));

    public static void register() {
        for (var entry : all) {
            Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(RoguesMod.NAMESPACE, entry.name), entry.block);
            Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(RoguesMod.NAMESPACE, entry.name), entry.item());
        }
        // Creative-tab placement (into the Rogues group) is registered per-platform from each loader's
        // entrypoint, iterating CustomBlocks.all — no Fabric API ItemGroupEvents in common.
    }
}

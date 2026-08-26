package net.rogues.block;

import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.rogues.RoguesMod;
import org.jetbrains.annotations.Nullable;

public class MartialWorkbenchBlock extends Block {
    public static Identifier ID = Identifier.fromNamespaceAndPath(RoguesMod.NAMESPACE, "arms_workbench");
    public MartialWorkbenchBlock(Properties settings) {
        super(settings);
    }

    // 1.21.5+: item tooltips are appended by the Item, not the Block — see CustomBlocks.hintedBlockItem.

    // MARK: Facing

    private static EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        FACING = BlockStateProperties.HORIZONTAL_FACING;
        builder.add(FACING);
    }

    // MARK: Partial transparency

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return true;
    }
}

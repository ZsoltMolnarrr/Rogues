package net.rogues.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.rogues.RoguesMod;
import org.jetbrains.annotations.Nullable;

public class MartialWorkbenchBlock extends Block {
    public static Identifier ID = Identifier.of(RoguesMod.NAMESPACE, "arms_workbench");
    public MartialWorkbenchBlock(Settings settings) {
        super(settings);
    }

    // 1.21.5+: item tooltips are appended by the Item, not the Block — see CustomBlocks.hintedBlockItem.

    // MARK: Facing

    private static EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        FACING = Properties.HORIZONTAL_FACING;
        builder.add(FACING);
    }

    // MARK: Partial transparency

    @Override
    protected boolean isTransparent(BlockState state) {
        return true;
    }
}

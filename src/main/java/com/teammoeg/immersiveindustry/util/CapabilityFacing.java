package com.teammoeg.immersiveindustry.util;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.ICommonMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.CapabilityPosition;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MultiblockFace;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;

/**
 * Capability and position handling for the outside face of a multiblock.
 *
 * <p>In IE 12 neighbour capabilities are resolved with
 * {@code ICommonMultiblockContext#getCapabilityAt(BlockCapability, MultiblockFace)}, which
 * returns a {@link java.util.function.Supplier}. {@code CapabilityReference} no longer exists,
 * so the supplier is wrapped in the mod's own {@link CapabilityReference} to keep the call
 * sites unchanged.
 */
public record CapabilityFacing(MultiblockFace pos, CapabilityPosition capPos) {

    public CapabilityFacing(MultiblockFace pos) {
        this(pos, CapabilityPosition.opposing(pos));
    }

    public CapabilityFacing(CapabilityPosition pos) {
        this(new MultiblockFace(pos.side().getOpposite(), pos.side().offsetRelative(pos.posInMultiblock(), 1)), pos);
    }

    public CapabilityFacing(int x, int y, int z, RelativeBlockFace side) {
        this(new CapabilityPosition(x, y, z, side));
    }

    /** Lazily-read capability of the block touching this face. */
    public <T> CapabilityReference<T> getFacingCapability(ICommonMultiblockContext ctx, BlockCapability<T, Direction> cap) {
        return CapabilityReference.of(ctx.getCapabilityAt(cap, pos));
    }

    public boolean isCapabilityPosition(CapabilityPosition pos) {
        return capPos.equalsOrNullFace(pos);
    }

    public boolean isPosEquals(BlockPos pos) {
        return capPos.posInMultiblock().equals(pos);
    }
}

package com.teammoeg.immersiveindustry.util;

import java.util.IdentityHashMap;
import java.util.Map;

import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IMultiblockComponent.CapabilityRegistrar;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.CapabilityPosition;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.minecraft.core.Direction;

/**
 * Per-state lookup table mapping a capability token plus a position inside the multiblock to
 * the handler that lives there.
 *
 * <p>Two NeoForge changes drive this shape. First, {@code Capability<T>} no longer exists;
 * capabilities are typed tokens such as {@link Capabilities.ItemHandler#BLOCK}, all of which
 * are {@code BlockCapability<T, Direction>}. Second, {@code LazyOptional} was removed, so a
 * miss is reported as {@code null} rather than an empty optional - including by IE's own
 * {@code CapabilityRegistrar}.
 *
 * <p>The table itself is still built while the state is created, because the handlers are
 * owned by the state; only the type of the key and the value changed.
 */
public class CapabilityProcessor {

    /** capability -> (position -> handler). Values are stored untyped and cast on read. */
    private final Map<BlockCapability<?, ?>, Map<CapabilityPosition, Object>> capabilities = new IdentityHashMap<>();

    public CapabilityProcessor() {
    }

    // ------------------------------------------------------------------ registration

    public <T> CapabilityBuilder<T> addCapabilities(BlockCapability<T, Direction> cap) {
        return new CapabilityBuilder<>(this, cap);
    }

    public CapabilityBuilder<IItemHandler> itemHandler() {
        return addCapabilities(Capabilities.ItemHandler.BLOCK);
    }

    public CapabilityBuilder<IFluidHandler> fluidHandler() {
        return addCapabilities(Capabilities.FluidHandler.BLOCK);
    }

    public CapabilityBuilder<IEnergyStorage> energy() {
        return addCapabilities(Capabilities.EnergyStorage.BLOCK);
    }

    public <T> CapabilityProcessor addCapability(BlockCapability<T, Direction> cap, CapabilityPosition pos, T obj) {
        capabilities.computeIfAbsent(cap, k -> new IdentityHashMap<>()).put(pos, obj);
        return this;
    }

    /** Position given as a face pair; only the inner capability position is used for lookup. */
    public <T> CapabilityProcessor addCapability(BlockCapability<T, Direction> cap, CapabilityFacing pos, T obj) {
        return addCapability(cap, pos.capPos(), obj);
    }

    // ------------------------------------------------------------------------ lookup

    /**
     * Handler registered for this capability at this position, or {@code null}.
     *
     * <p>{@code CapabilityPosition} is a record, so a plain {@code equals} lookup matches the
     * behaviour of the old per-capability query objects.
     */
    @SuppressWarnings("unchecked")
    public <T> T getCapability(BlockCapability<T, Direction> cap, CapabilityPosition pos) {
        if (pos == null) {
            return null;
        }
        Map<CapabilityPosition, Object> byPosition = capabilities.get(cap);
        if (byPosition == null) {
            return null;
        }
        return (T) byPosition.get(pos);
    }

    /** True when anything at all is registered for this capability. */
    public boolean hasAny(BlockCapability<?, ?> cap) {
        Map<CapabilityPosition, Object> byPosition = capabilities.get(cap);
        return byPosition != null && !byPosition.isEmpty();
    }

    /**
     * Hands every registered capability to IE's registrar.
     *
     * <p>IE 12 exposes multiblock capabilities through
     * {@code IMultiblockComponent#registerCapabilities} instead of the old
     * {@code IMultiblockLogic#getCapability} callback, so machines forward their table here.
     */
    public <State> void registerAll(CapabilityRegistrar<State> registrar, State state) {
        if (hasAny(Capabilities.ItemHandler.BLOCK)) {
            registrar.register(Capabilities.ItemHandler.BLOCK,
                    (st, pos) -> getCapability(Capabilities.ItemHandler.BLOCK, pos));
        }
        if (hasAny(Capabilities.FluidHandler.BLOCK)) {
            registrar.register(Capabilities.FluidHandler.BLOCK,
                    (st, pos) -> getCapability(Capabilities.FluidHandler.BLOCK, pos));
        }
        if (hasAny(Capabilities.EnergyStorage.BLOCK)) {
            registrar.register(Capabilities.EnergyStorage.BLOCK,
                    (st, pos) -> getCapability(Capabilities.EnergyStorage.BLOCK, pos));
        }
    }

    /** Fluent helper so state constructors read as one chain. */
    public record CapabilityBuilder<T>(CapabilityProcessor proc, BlockCapability<T, Direction> cap) {

        public CapabilityBuilder<T> addCapability(CapabilityPosition pos, T obj) {
            proc.addCapability(cap, pos, obj);
            return this;
        }

        public CapabilityBuilder<T> addCapability(CapabilityFacing pos, T obj) {
            proc.addCapability(cap, pos, obj);
            return this;
        }
    }
}

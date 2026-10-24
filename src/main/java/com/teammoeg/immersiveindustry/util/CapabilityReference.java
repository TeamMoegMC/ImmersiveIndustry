package com.teammoeg.immersiveindustry.util;

import java.util.function.Supplier;

/**
 * Replacement for {@code blusunrize.immersiveengineering.api.utils.CapabilityReference}.
 *
 * <p>Immersive Engineering 12 removed {@code CapabilityReference}: a multiblock now asks its
 * context for a neighbour capability and receives a {@link Supplier} that resolves lazily
 * against the current level state. Keeping a thin wrapper with the same
 * {@link #getNullable()} shape means the machine code keeps reading naturally without
 * sprinkling null checks everywhere, while the actual lookup stays IE-driven.
 *
 * @param <T> capability type, e.g. {@code IItemHandler}
 */
public final class CapabilityReference<T> implements Supplier<T> {

    private final Supplier<T> delegate;

    private CapabilityReference(Supplier<T> delegate) {
        this.delegate = delegate;
    }

    /** Wraps an IE capability supplier. A null supplier yields a reference that never resolves. */
    public static <T> CapabilityReference<T> of(Supplier<T> delegate) {
        return new CapabilityReference<>(delegate == null ? () -> null : delegate);
    }

    @Override
    public T get() {
        return delegate.get();
    }

    /** True when the neighbour currently exposes the capability. */
    public boolean isPresent() {
        return delegate.get() != null;
    }

    /** Current value, or {@code null} when the neighbour is absent or no longer exposes it. */
    public T getNullable() {
        return delegate.get();
    }
}

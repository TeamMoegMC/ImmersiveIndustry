package com.teammoeg.immersiveindustry.content;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.Property;

public interface IDirectionPropertyBlock {
    Property<Direction> getDirectionProperty();
}

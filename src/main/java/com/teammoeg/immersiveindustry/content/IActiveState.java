package com.teammoeg.immersiveindustry.content;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public interface IActiveState extends IEBlockInterfaces.BlockstateProvider {
    default boolean getIsActive() {
        BlockState state = this.getState();
        return state.hasProperty(BlockStateProperties.LIT) ? (Boolean) state.getValue(BlockStateProperties.LIT) : false;
    }

    default void setActive(boolean active) {
        BlockState state = this.getState();
        BlockState newState = (BlockState) state.setValue(BlockStateProperties.LIT, active);
        this.setState(newState);
    }
}
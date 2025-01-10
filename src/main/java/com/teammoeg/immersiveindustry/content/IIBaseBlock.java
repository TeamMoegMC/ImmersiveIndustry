/*
 * Copyright (c) 2021 TeamMoeg
 *
 * This file is part of Immersive Industry.
 *
 * Immersive Industry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Immersive Industry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Immersive Industry. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.immersiveindustry.content;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class IIBaseBlock extends Block {
    protected int lightOpacity;

    public IIBaseBlock( Properties blockProps) {
        super(blockProps);
        lightOpacity = 15;

    }

    public IIBaseBlock setLightOpacity(int opacity) {
        lightOpacity = opacity;
        return this;
    }

    public int getOpacity(BlockState state, BlockGetter worldIn, BlockPos pos) {
        if (state.isSolidRender(worldIn,pos))
            return lightOpacity;
        return state.propagatesSkylightDown(worldIn, pos) ? 0 : 1;
    }

}

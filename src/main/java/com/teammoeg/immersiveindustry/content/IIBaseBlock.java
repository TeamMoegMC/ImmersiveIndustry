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

import java.util.function.BiFunction;

import com.teammoeg.immersiveindustry.IIContent;
import com.teammoeg.immersiveindustry.IIMain;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.item.Item;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.property.Properties;

public class IIBaseBlock extends Block {
    public final String name;
    protected int lightOpacity;

    public IIBaseBlock(String name, Properties blockProps, BiFunction<Block, Item.Properties, Item> createItemBlock) {
        super(blockProps.variableOpacity());
        this.name = name;
        lightOpacity = 15;

        ResourceLocation registryName = createRegistryName();
        setRegistryName(registryName);

        IIContent.registeredBlocks.add(this);
        Item item = createItemBlock.apply(this, new Item.Properties().group(IIMain.itemGroup));
        if (item != null) {
            item.setRegistryName(registryName);
            IIContent.registeredItems.add(item);
        }
    }

    public ResourceLocation createRegistryName() {
        return new ResourceLocation(IIMain.MODID, name);
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

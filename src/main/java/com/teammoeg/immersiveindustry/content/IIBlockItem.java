/*
 * Copyright (c) 2021 TeamMoeg
 *
 * This file is part of Frosted Heart.
 *
 * Frosted Heart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Frosted Heart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Frosted Heart. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.immersiveindustry.content;

import com.teammoeg.immersiveindustry.IIMain;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class IIBlockItem extends BlockItem {
    public IIBlockItem(Block block, Properties props) {
        super(block, props);
    }

    public IIBlockItem(Block block) {
        this(block, new Properties().group(IIMain.itemGroup));
        setRegistryName(block.getRegistryName());
    }
}

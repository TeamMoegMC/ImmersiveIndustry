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

package com.teammoeg.immersiveindustry.content.carkiln;

import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import com.teammoeg.immersiveindustry.IIContent;
import com.teammoeg.immersiveindustry.IIMain;
import com.teammoeg.immersiveindustry.util.IIMultiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class CarKilnMultiblock extends IIMultiblock {

    // IMPORTANT NOTE ON TRIGGER POS:
    // It is NOT an arbitrary choice!
    // The default implementation of createStructure requires
    // the trigger position to be on the OPPOSITE side of the structure's x-axis baseline
    // ASCII art illustration:
    // [ ] [T] [ ] ^
    // [ ] [ ] [ ] z
    // [ ] [ ] [O] |
    // <-----x-----
    // "T" is the trigger position that must be on the opposite side of the x-axis baseline
    // "O" is the structure origin as defined by a structure file
    public CarKilnMultiblock() {
        super(new ResourceLocation(IIMain.MODID, "multiblocks/car_kiln"),
                new BlockPos(1, 1, 2), new BlockPos(1, 0, 4), new BlockPos(3, 5, 5),
                IIContent.IIMultiblocks.CAR_KILN);

    }



	@Override
    public float getManualScale() {
        return 16;
    }

    @Override
    public boolean canBeMirrored() {
        return false;
    }
}

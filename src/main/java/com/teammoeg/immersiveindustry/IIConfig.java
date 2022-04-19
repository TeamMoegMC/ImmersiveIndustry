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

package com.teammoeg.immersiveindustry;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class IIConfig {

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, IIConfig.COMMON_CONFIG);
    }


    public static class Common {
        public final ForgeConfigSpec.IntValue electrolyzerConsume;
        public final ForgeConfigSpec.IntValue rotaryKilnRotor;
        public final ForgeConfigSpec.IntValue rotaryKilnHeater;
        public final ForgeConfigSpec.IntValue carKilnConsume;
        public final ForgeConfigSpec.IntValue steamTurbineGenerator;
        public final ForgeConfigSpec.DoubleValue electrodeCost;

        Common(ForgeConfigSpec.Builder builder) {
            electrolyzerConsume = builder.defineInRange("electrolyzerConsume", 24, 0, 4096);
            rotaryKilnRotor = builder.defineInRange("rotaryKilnBase", 24, 0, 4096);
            rotaryKilnHeater = builder.defineInRange("rotaryKilnHeater", 256, 0, 4096);
            carKilnConsume = builder.defineInRange("carKilnConsume", 24, 0, 4096);
            steamTurbineGenerator = builder.defineInRange("steamTurbineGenerator", 1024, 0, 4096);
            electrodeCost = builder.defineInRange("electrodeCost", 0.25, 0, 96000);
        }
    }

    public static final ForgeConfigSpec COMMON_CONFIG;
    public static final Common COMMON;

    static {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        COMMON = new Common(COMMON_BUILDER);
        COMMON_CONFIG = COMMON_BUILDER.build();
    }
}

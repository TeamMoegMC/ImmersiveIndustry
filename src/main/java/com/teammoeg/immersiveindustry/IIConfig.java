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
        public final ForgeConfigSpec.IntValue electrolyzerBase;
        public final ForgeConfigSpec.IntValue rotaryKilnBase;
        public final ForgeConfigSpec.IntValue carKilnBase;
        public final ForgeConfigSpec.IntValue steamTurbineGenerator;
        public final ForgeConfigSpec.DoubleValue electrodeCost;

        Common(ForgeConfigSpec.Builder builder) {
            electrolyzerBase = builder.defineInRange("ElectrolyzerBaseTickEnergy", 32, 0, 4096);
            rotaryKilnBase = builder.defineInRange("RotaryKilnBaseTickEnergy", 48, 0, 4096);
            carKilnBase = builder.defineInRange("CarKilnBaseTickEnergy", 64, 0, 4096);
            steamTurbineGenerator = builder.defineInRange("SteamTurbineGenerator", 1024, 0, 4096);
            electrodeCost = builder.defineInRange("ElectrodeCost", 0.25, 0, 96000);
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

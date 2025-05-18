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
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, IIConfig.SERVER_CONFIG);
    }


    public static class Server {
        public final ForgeConfigSpec.DoubleValue electrolyzerBaseRate;
        public final ForgeConfigSpec.DoubleValue rotaryKilnBaseRate;
        public final ForgeConfigSpec.DoubleValue carKilnBaseRate;
        public final ForgeConfigSpec.DoubleValue crucibleCoolDown;
        public final ForgeConfigSpec.DoubleValue steamTurbineGenerator;
        public final ForgeConfigSpec.DoubleValue steamTurbineSaturationRate;
        public final ForgeConfigSpec.DoubleValue steamTurbineUnsaturationRate;
        public final ForgeConfigSpec.DoubleValue steamTurbineSaturationMin;
        public final ForgeConfigSpec.DoubleValue steamTurbineSaturationMax;
        public final ForgeConfigSpec.IntValue steamTurbineInputMin;
        public final ForgeConfigSpec.IntValue steamTurbineInputMax;
        public final ForgeConfigSpec.DoubleValue electrodeCost;
        public final ForgeConfigSpec.DoubleValue chemicalBaseRate;
        Server(ForgeConfigSpec.Builder builder) {
            electrolyzerBaseRate = builder.defineInRange("ElectrolyzerBaseTickEnergyRate", 1d, 0, Integer.MAX_VALUE);
            rotaryKilnBaseRate = builder.defineInRange("RotaryKilnBaseTickEnergyRate", 1d, 0, Integer.MAX_VALUE);
            carKilnBaseRate = builder.defineInRange("CarKilnBaseTickEnergyRate", 1d, 0, Integer.MAX_VALUE);
            steamTurbineGenerator = builder.defineInRange("SteamTurbineGeneratePerSteam", 16d, 0, Integer.MAX_VALUE);
            steamTurbineSaturationRate=builder.defineInRange("SteamTurbineSaturationPerSteam", 0.000002, 0, 1);
            steamTurbineUnsaturationRate=builder.defineInRange("SteamTurbineUnsaturationPerTickPerLevel", 0.00024, 0, 1);
            steamTurbineSaturationMin=builder.defineInRange("SteamTurbineMinimumSaturation", 0.4d, 0d, 100000d);
            steamTurbineSaturationMax=builder.defineInRange("SteamTurbineMaximumSaturation", 4d, 0d, 100000d);
            steamTurbineInputMax=builder.defineInRange("SteamTurbineMaximumInput", 288, 0, Integer.MAX_VALUE);
            steamTurbineInputMin=builder.defineInRange("SteamTurbineMinimumInput", 48, 0, Integer.MAX_VALUE);
            electrodeCost = builder.defineInRange("ElectrodeCost", 0.25, 0, 96000);
            crucibleCoolDown=builder.defineInRange("CrucibleCooldownRate",0.25,0,Integer.MAX_VALUE);
            chemicalBaseRate=builder.defineInRange("ChemicalReactorBaseTickEnergyRate", 1d, 0, Integer.MAX_VALUE);
            
        }
    }

    public static final ForgeConfigSpec SERVER_CONFIG;
    public static final Server SERVER;

    static {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        SERVER = new Server(COMMON_BUILDER);
        SERVER_CONFIG = COMMON_BUILDER.build();
    }
}

package com.teammoeg.immersiveindustry;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Server-side config.
 *
 * <p>Two renames for NeoForge 1.21: the spec class is {@link ModConfigSpec} rather than
 * {@code ForgeConfigSpec}, and the spec is registered on the mod's own
 * {@link ModContainer} instead of through the removed {@code ModLoadingContext} singleton.
 */
public class IIConfig {

    public static void register(ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, IIConfig.SERVER_CONFIG);
    }


    public static class Server {
        public final ModConfigSpec.DoubleValue electrolyzerBaseRate;
        public final ModConfigSpec.DoubleValue rotaryKilnBaseRate;
        public final ModConfigSpec.DoubleValue carKilnBaseRate;
        public final ModConfigSpec.DoubleValue crucibleCoolDown;
        public final ModConfigSpec.DoubleValue steamTurbineGenerator;
        public final ModConfigSpec.DoubleValue steamTurbineSaturationRate;
        public final ModConfigSpec.DoubleValue steamTurbineUnsaturationRate;
        public final ModConfigSpec.DoubleValue steamTurbineSaturationMin;
        public final ModConfigSpec.DoubleValue steamTurbineSaturationMax;
        public final ModConfigSpec.IntValue steamTurbineInputMin;
        public final ModConfigSpec.IntValue steamTurbineInputMax;
        public final ModConfigSpec.DoubleValue electrodeCost;
        public final ModConfigSpec.DoubleValue chemicalBaseRate;

        Server(ModConfigSpec.Builder builder) {
            electrolyzerBaseRate = builder.defineInRange("ElectrolyzerBaseTickEnergyRate", 1d, 0, Integer.MAX_VALUE);
            rotaryKilnBaseRate = builder.defineInRange("RotaryKilnBaseTickEnergyRate", 1d, 0, Integer.MAX_VALUE);
            carKilnBaseRate = builder.defineInRange("CarKilnBaseTickEnergyRate", 1d, 0, Integer.MAX_VALUE);
            steamTurbineGenerator = builder.defineInRange("SteamTurbineGeneratePerSteam", 16d, 0, Integer.MAX_VALUE);
            steamTurbineSaturationRate = builder.defineInRange("SteamTurbineSaturationPerSteam", 0.000002, 0, 1);
            steamTurbineUnsaturationRate = builder.defineInRange("SteamTurbineUnsaturationPerTickPerLevel", 0.00024, 0, 1);
            steamTurbineSaturationMin = builder.defineInRange("SteamTurbineMinimumSaturation", 0.4d, 0d, 100000d);
            steamTurbineSaturationMax = builder.defineInRange("SteamTurbineMaximumSaturation", 4d, 0d, 100000d);
            steamTurbineInputMax = builder.defineInRange("SteamTurbineMaximumInput", 288, 0, Integer.MAX_VALUE);
            steamTurbineInputMin = builder.defineInRange("SteamTurbineMinimumInput", 48, 0, Integer.MAX_VALUE);
            electrodeCost = builder.defineInRange("ElectrodeCost", 0.25, 0, 96000);
            crucibleCoolDown = builder.defineInRange("CrucibleCooldownRate", 0.25, 0, Integer.MAX_VALUE);
            chemicalBaseRate = builder.defineInRange("ChemicalReactorBaseTickEnergyRate", 1d, 0, Integer.MAX_VALUE);
        }
    }

    public static final ModConfigSpec SERVER_CONFIG;
    public static final Server SERVER;

    static {
        ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
        SERVER = new Server(COMMON_BUILDER);
        SERVER_CONFIG = COMMON_BUILDER.build();
    }
}

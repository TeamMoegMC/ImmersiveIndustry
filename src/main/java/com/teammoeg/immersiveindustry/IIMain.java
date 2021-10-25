package com.teammoeg.immersiveindustry;

import com.teammoeg.immersiveindustry.data.IIRecipeReloadListener;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nonnull;


@Mod(IIMain.MODID)
public class IIMain {
    public static final String MODID = "immersiveindustry";
    public static final String MODNAME = "Immersive Industry";
    public static final ItemGroup itemGroup = new ItemGroup(MODID) {
        @Override
        @Nonnull
        public ItemStack createIcon() {
            return new ItemStack(IIContent.IIBlocks.burning_chamber.asItem());
        }
    };

    public IIMain() {
        IEventBus mod = FMLJavaModLoadingContext.get().getModEventBus();
        mod.addListener(this::setup);
        IIContent.IIBlocks.init();
        IIContent.IIMultiblocks.init();

        IIContent.registerContainers();
        IIContent.IITileTypes.REGISTER.register(mod);
        IIContent.IIRecipes.RECIPE_SERIALIZERS.register(mod);
        DeferredWorkQueue.runLater(IIContent.IIRecipes::registerRecipeTypes);
    }

    public void setup(final FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new IIRecipeReloadListener(null));
    }
}

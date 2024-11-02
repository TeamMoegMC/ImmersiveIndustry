package com.teammoeg.immersiveindustry.content;

import com.teammoeg.immersiveindustry.IIContent;
import com.teammoeg.immersiveindustry.IIMain;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class IICreativeTab {
    public static final String ITEM_GROUP_NAME = "itemGroup.immersiveindustry";
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, IIMain.MODID);

    public static final RegistryObject<CreativeModeTab> MOD_TAB = CREATIVE_MODE_TABS.register("immersiveindustry",
            ()-> CreativeModeTab.builder().icon(()->new ItemStack(IIContent.IIBlocks.electrolyzer))
                    .title(Component.translatable(ITEM_GROUP_NAME))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(IIContent.IIBlocks.electrolyzer);
                        pOutput.accept(IIContent.IIBlocks.burning_chamber);
                        pOutput.accept(IIContent.IIBlocks.car_kiln_brick);
                        pOutput.accept(IIContent.IIBlocks.rotary_kiln_cylinder);
                        pOutput.accept(IIContent.IItems.refractory_kiln_brick);
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}

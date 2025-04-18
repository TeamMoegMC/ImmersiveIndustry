package com.teammoeg.immersiveindustry.content;

import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockItem;
import com.teammoeg.immersiveindustry.IIContent;
import com.teammoeg.immersiveindustry.IIMain;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class IICreativeTab {
    public static final String ITEM_GROUP_NAME = "itemGroup.immersiveindustry";
    public static final String MULTI_BLOCK_GROUP_NAME = "itemGroup.immersive_multiblocks";
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, IIMain.MODID);

    public static final RegistryObject<CreativeModeTab> MOD_TAB = CREATIVE_MODE_TABS.register("immersiveindustry",
            ()-> CreativeModeTab.builder().icon(()->new ItemStack(IIContent.IIBlocks.electrolyzer.get()))
                    .title(Component.translatable(ITEM_GROUP_NAME))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(IIContent.IIBlocks.electrolyzer.get());
                        pOutput.accept(IIContent.IIBlocks.burning_chamber.get());
                        pOutput.accept(IIContent.IIBlocks.car_kiln_brick.get());
                        pOutput.accept(IIContent.IIBlocks.rotary_kiln_cylinder.get());
                        pOutput.accept(IIContent.IItems.refractory_kiln_brick.get());
                    })
                    .build());
    
    public static final RegistryObject<CreativeModeTab> MULTI_BLOCKS_TAB = CREATIVE_MODE_TABS.register("immersive_multiblocks",
            ()-> CreativeModeTab.builder().icon(()->new ItemStack(IIContent.IIMultiblocks.INDUSTRIAL_ELECTROLYZER.blockItem().get()))
                    .title(Component.translatable(MULTI_BLOCK_GROUP_NAME))
                    .displayItems((param,out) ->
                            param.holders()
                                    .lookup(Registries.ITEM)
                                    .ifPresent(reg -> reg.filterElements(item -> item instanceof MultiblockItem)
                                            .listElements()
                                            .map(Holder::get)
                                            .forEach(out::accept)))
                    .withTabsBefore(MOD_TAB.getKey())
                    .build()
            );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}

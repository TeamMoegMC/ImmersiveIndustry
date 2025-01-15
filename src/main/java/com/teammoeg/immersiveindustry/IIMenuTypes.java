package com.teammoeg.immersiveindustry;

import blusunrize.immersiveengineering.common.gui.IEContainerMenu;
import blusunrize.immersiveengineering.common.register.IEMenuTypes;
import com.teammoeg.immersiveindustry.content.electrolyzer.ElectrolyzerBlockEntity;
import com.teammoeg.immersiveindustry.content.electrolyzer.ElectrolyzerContainer;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

public class IIMenuTypes {
    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.MENU_TYPES, IIMain.MODID);

    public static final IEMenuTypes.ArgContainer<ElectrolyzerBlockEntity, ElectrolyzerContainer> ELECTROLYZER = registerArg(
            "electrolyzer", ElectrolyzerContainer::makeServer, ElectrolyzerContainer::makeClient
    );

    public static <T, C extends IEContainerMenu>
    IEMenuTypes.ArgContainer<T, C> registerArg(
            String name, IEMenuTypes.ArgContainerConstructor<T, C> container, IEMenuTypes.ClientContainerConstructor<C> client
    )
    {
        RegistryObject<MenuType<C>> typeRef = registerType(name, client);
        return new IEMenuTypes.ArgContainer<>(typeRef, container);
    }
    private static <C extends IEContainerMenu>
    RegistryObject<MenuType<C>> registerType(String name, IEMenuTypes.ClientContainerConstructor<C> client)
    {
        return REGISTER.register(
                name, () -> {
                    Mutable<MenuType<C>> typeBox = new MutableObject<>();
                    MenuType<C> type = new MenuType<>((id, inv) -> client.construct(typeBox.getValue(), id, inv), FeatureFlagSet.of());
                    typeBox.setValue(type);
                    return type;
                }
        );
    }
}

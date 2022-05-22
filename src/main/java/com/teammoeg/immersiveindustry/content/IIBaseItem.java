package com.teammoeg.immersiveindustry.content;

import com.teammoeg.immersiveindustry.IIContent;
import com.teammoeg.immersiveindustry.IIMain;
import net.minecraft.item.Item;

public class IIBaseItem extends Item {
    public IIBaseItem(String name, Properties properties) {
        super(properties);
        setRegistryName(IIMain.MODID, name);
        IIContent.registeredItems.add(this);
    }
}

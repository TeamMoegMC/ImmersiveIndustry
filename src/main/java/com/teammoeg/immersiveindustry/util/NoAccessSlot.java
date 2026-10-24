package com.teammoeg.immersiveindustry.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.SlotItemHandler;

public class NoAccessSlot extends SlotItemHandler {
    public NoAccessSlot(IItemHandlerModifiable inv, int id, int x, int y) {
        super( inv, id, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return false;
    }
}
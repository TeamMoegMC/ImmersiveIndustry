package com.teammoeg.immersiveindustry.util;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class OutputSlot extends SlotItemHandler {
    public OutputSlot(IItemHandlerModifiable inv, int id, int x, int y) {
        super( inv, id, x, y);
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return false;
    }
}
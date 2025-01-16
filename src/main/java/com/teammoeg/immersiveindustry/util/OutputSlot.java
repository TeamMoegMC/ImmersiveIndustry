package com.teammoeg.immersiveindustry.util;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class OutputSlot extends SlotItemHandler {
    public OutputSlot(IItemHandlerModifiable inv, int id, int x, int y) {
        super( inv, id, x, y);
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return true;
    }

	@Override
	public boolean mayPlace(@NotNull ItemStack stack) {
		return false;
	}
    
}
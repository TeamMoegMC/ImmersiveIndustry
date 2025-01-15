package com.teammoeg.immersiveindustry.util;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RangedWrapper;

public class RangedOutputWrapper extends RangedWrapper {

	public RangedOutputWrapper(IItemHandlerModifiable compose, int minSlot, int maxSlotExclusive) {
		super(compose, minSlot, maxSlotExclusive);
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		return stack;
	}

}

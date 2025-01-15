package com.teammoeg.immersiveindustry.util;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RangedWrapper;

public class RangedInputWrapper extends RangedWrapper {

	public RangedInputWrapper(IItemHandlerModifiable compose, int minSlot, int maxSlotExclusive) {
		super(compose, minSlot, maxSlotExclusive);
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		return ItemStack.EMPTY;
	}

}

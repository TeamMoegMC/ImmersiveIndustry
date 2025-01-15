package com.teammoeg.immersiveindustry.util;

import java.util.function.BiPredicate;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public class RangedCheckedInputWrapper extends RangedInputWrapper {
	BiPredicate<Integer,ItemStack> check;
	public RangedCheckedInputWrapper(IItemHandlerModifiable compose, int minSlot, int maxSlotExclusive,BiPredicate<Integer,ItemStack> check) {
		super(compose, minSlot, maxSlotExclusive);
		this.check=check;
	}
	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return check.test(slot, stack)&&super.isItemValid(slot, stack);
	}

}

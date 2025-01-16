package com.teammoeg.immersiveindustry.util;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class ChangeDetectedItemHandler extends ItemStackHandler{
	Runnable onchange;
	List<Runnable> slotOnChange;
	public ChangeDetectedItemHandler(int slots,Runnable onchange) {
		super(slots);
		this.onchange = onchange;
		slotOnChange=new ArrayList<>(getSlots());
	}
	public void addSlotListener(int slot,Runnable onchange) {
		while(slotOnChange.size()<=slot)
			slotOnChange.add(null);
		slotOnChange.set(slot, onchange);
	}
	public void addSlotListener(int minSlot,int maxSlot,Runnable onchange) {
		while(slotOnChange.size()<maxSlot)
			slotOnChange.add(null);
		for(int i=minSlot;i<maxSlot;i++)
			slotOnChange.set(i, onchange);
	}
	protected void onContentsChanged(int slot) {
		onchange.run();
		if(slotOnChange.size()>slot&&slotOnChange.get(slot)!=null)
			slotOnChange.get(slot).run();
	}

	public void setStackInSlotNoChange(int slot, @NotNull ItemStack stack) {
		validateSlotIndex(slot);
		this.stacks.set(slot, stack);
	}

}

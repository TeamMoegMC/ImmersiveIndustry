package com.teammoeg.immersiveindustry.util;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import mezz.jei.common.Internal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class ChangeDetectedItemHandler implements IItemHandlerModifiable, INBTSerializable<CompoundTag>{
	ItemStackHandler handler;
	Runnable onchange;
	List<Runnable> slotOnChange;
	public ChangeDetectedItemHandler(int slots,Runnable onchange) {
		super();
		this.handler = new ItemStackHandler(slots);
		this.onchange = onchange;
		slotOnChange=new ArrayList<>(handler.getSlots());
	}
	public void addSlotListener(int slot,Runnable onchange) {
		while(slotOnChange.size()<=slot)
			slotOnChange.add(null);
		slotOnChange.set(slot, onchange);
	}
	public int getSlots() {
		return handler.getSlots();
	}
	public @NotNull ItemStack getStackInSlot(int slot) {
		return handler.getStackInSlot(slot);
	}
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		ItemStack ret= handler.insertItem(slot, stack, simulate);
		if(!simulate&&ret.getCount()!=stack.getCount())
			onContentsChanged(slot);
		return ret;
	}
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack extracted= handler.extractItem(slot, amount, simulate);
		if(!simulate&&!extracted.isEmpty())
			onContentsChanged(slot);
		return extracted;
	}
	public int getSlotLimit(int slot) {
		return handler.getSlotLimit(slot);
	}
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return handler.isItemValid(slot, stack);
	}
	protected void onContentsChanged(int slot) {
		onchange.run();
		if(slotOnChange.size()>slot&&slotOnChange.get(slot)!=null)
			slotOnChange.get(slot).run();
	}
	@Override
	public void setStackInSlot(int slot, @NotNull ItemStack stack) {
		handler.setStackInSlot(slot, stack);
	}
	@Override
	public CompoundTag serializeNBT() {
		return handler.serializeNBT();
	}
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		handler.deserializeNBT(nbt);
	}

}

package com.teammoeg.immersiveindustry.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

public class IIUtil {

	public IIUtil() {
		// TODO Auto-generated constructor stub
	}
	public static boolean outputItem(IItemHandlerModifiable inventory,CapabilityReference<IItemHandler> outputCap,int slot) {
		ItemStack stack = inventory.getStackInSlot(3);
		if(!stack.isEmpty())
		{
			ItemStack nstack = Utils.insertStackIntoInventory(outputCap, stack, false);
			if(nstack.getCount()!=stack.getCount()) {
				inventory.setStackInSlot(3, nstack);
				return true;
			}
			
		}
		return false;
	}
	public static ItemStack insertToOutput(IItemHandlerModifiable inv, int slot, ItemStack in) {
		ItemStack is = inv.getStackInSlot(slot);
		if (is.isEmpty()) {
			inv.setStackInSlot(slot, in.split(Math.min(inv.getSlotLimit(slot), in.getMaxStackSize())));
		} else if (ItemHandlerHelper.canItemStacksStack(in, is)) {
			int limit = Math.min(inv.getSlotLimit(slot), is.getMaxStackSize());
			limit -= is.getCount();
			limit = Math.min(limit, in.getCount());
			is.grow(limit);
			in.shrink(limit);
		}
		return in;
	}
	public static <T> List<T> readList(FriendlyByteBuf buffer, Function<FriendlyByteBuf, T> func) {
		int cnt = buffer.readVarInt();
		List<T> nums = new ArrayList<>(cnt);
		for (int i = 0; i < cnt; i++)
			nums.add(func.apply(buffer));
		return nums;
	}
	public static <T> void writeList(FriendlyByteBuf buffer, Collection<T> elms, BiConsumer<T, FriendlyByteBuf> func) {
		buffer.writeVarInt(elms.size());
		elms.forEach(e -> func.accept(e, buffer));
	}
}

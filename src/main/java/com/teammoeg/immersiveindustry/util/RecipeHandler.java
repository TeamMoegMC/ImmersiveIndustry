/*
 * Copyright (c) 2024 TeamMoeg
 *
 * This file is part of Caupona.
 *
 * Caupona is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Caupona is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Specially, we allow this software to be used alongside with closed source software Minecraft(R) and Forge or other modloader.
 * Any mods or plugins can also use apis provided by forge or com.teammoeg.caupona.api without using GPL or open source.
 *
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.immersiveindustry.util;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.items.IItemHandlerModifiable;

public class RecipeHandler<T extends Recipe<?>>{
	private int process;
	private int processMax;
	private ResourceLocation lastRecipe;
	private boolean recipeTested=false;
	private RecipeProcessResult<T> recipeResultCache;
	private final BiFunction<ResourceLocation,T,Integer> getTimes;
	public ResourceLocation getLastRecipe() {
		return lastRecipe;
	}
	public RecipeHandler(BiFunction<ResourceLocation,T,Integer> getTimes) {
		this.getTimes=getTimes;
	}
	public void onContainerChanged() {
		//System.out.println("revalidate needed");
		recipeTested=false;
	}
	public boolean shouldTestRecipe() {
		return !recipeTested;
	}
	public boolean setRecipe(RecipeProcessResult<T> recipeObj) {
		//System.out.println("revalidate return "+recipe);
		recipeResultCache=recipeObj;
		boolean res=false;
		if (recipeObj!= null) {
			if(processMax==0||!recipeObj.getId().equals(lastRecipe)) {
				process=processMax=getTimes.apply(lastRecipe, recipeObj.recipe());
				lastRecipe=recipeObj.getId();
				res= true;
			}
		}else {
			process=processMax=0;
			lastRecipe=null;
			res=true;
		}
		recipeTested=true;
		return res;
	}
	public RecipeProcessResult<T> getRecipeResultCache() {
		/*if(recipeResultCache==null) {
			recipeResultCache=generator.apply(lastRecipe);
		}*/
		return recipeResultCache;
	}

	public boolean shouldTickProcess() {
		return processMax > 0;
	}
	public boolean tickProcess(int num) {
		if (processMax > 0) {
			if(process>0) {
				process-=num;
				if(process<=0) {
					process=0;
					recipeTested=false;
				}
				return true;
			}
		}
		return false;
	}
	public boolean isRecipeFinished() {
		return processMax>0&&process<=0;
	}
	public void endProcess() {
		recipeResultCache=null;
		process=processMax=0;
	}
	public void resetProgress() {
		process=processMax;
	}
	public void readCustomNBT(CompoundTag nbt, boolean isClient) {
		process=nbt.getInt("process");
		processMax=nbt.getInt("processMax");
		if (!isClient) {
			recipeResultCache=null;
			if(nbt.contains("lastRecipe"))
				lastRecipe=new ResourceLocation(nbt.getString("lastRecipe"));
			else
				lastRecipe=null;
		}

	}
	public void writeCustomNBT(CompoundTag nbt, boolean isClient) {
		nbt.putInt("process",process);
		nbt.putInt("processMax",processMax);
		if (!isClient) {
			if(lastRecipe!=null)
				nbt.putString("lastRecipe", lastRecipe.toString());
		}

	}
	public int getProcess() {
		return process;
	}
	public int getProcessMax() {
		return processMax;
	}
	public int getFinishedProgress() {
		return processMax-process;
	}
	public float getProgressRatio() {
		if(processMax<=0)
			return 0;
		return getFinishedProgress()*1f/processMax;
	}
	public IItemHandlerModifiable createItemHanlderWrapper(IItemHandlerModifiable wrapped,int inputSlots) {
		return new IItemHandlerModifiable() {

			@Override
			public int getSlots() {
				return wrapped.getSlots();
			}

			@Override
			public @NotNull ItemStack getStackInSlot(int slot) {
				return wrapped.getStackInSlot(slot);
			}

			@Override
			public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
				int incount=stack.getCount();
				ItemStack out=wrapped.insertItem(slot, stack, simulate);
				if(!simulate&&slot<inputSlots) {
					if(out.getCount()!=incount) {
						onContainerChanged();
					}
				}
				return out;
			}

			@Override
			public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
				ItemStack out=wrapped.extractItem(slot, amount, simulate);
				if(!simulate&&slot<inputSlots&&!out.isEmpty()) {
					onContainerChanged();
				}
				return out;
			}

			@Override
			public int getSlotLimit(int slot) {
				return wrapped.getSlotLimit(slot);
			}

			@Override
			public boolean isItemValid(int slot, @NotNull ItemStack stack) {
				return wrapped.isItemValid(slot, stack);
			}

			@Override
			public void setStackInSlot(int slot, @NotNull ItemStack stack) {
				wrapped.setStackInSlot(slot, stack);
			}
		};
	}

}

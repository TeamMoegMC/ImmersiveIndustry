package com.teammoeg.immersiveindustry.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

/**
 * Matches a tank against the fluids a recipe needs.
 *
 * <p>IE's {@code FluidTagInput} is gone in 1.21; {@link SizedFluidIngredient} carries the same
 * information ({@code ingredient()} for the tag/stack matcher, {@code amount()} for the size),
 * so the simulation logic is unchanged apart from those two accessors.
 */
public class FluidRecipeSimulator {

	public FluidRecipeSimulator() {

	}

	public static List<List<FluidStack>> expand(SizedFluidIngredient... ft) {
		List<List<FluidStack>> li = new ArrayList<>(ft.length);
		for (SizedFluidIngredient t : ft) {
			li.add(Arrays.asList(t.getFluids()));
		}
		return li;
	}

	public static FluidRecipeProcessResult test(IFluidHandler ft, SizedFluidIngredient... ftis) {
		List<FluidStack> li = new ArrayList<>(ftis.length);
		List<FluidStack> all = new ArrayList<>(ft.getTanks());
		for (int i = 0; i < ft.getTanks(); i++) {
			all.add(ft.getFluidInTank(i).copy());
		}
		for (SizedFluidIngredient fti : ftis) {
			int cnt = fti.amount();
			for (FluidStack fs : all) {
				if (fti.test(fs)) {
					if (fs.getAmount() >= cnt) {
						li.add(fs.copyWithAmount(cnt));
						fs.setAmount(fs.getAmount() - cnt);
						cnt = 0;
					} else {
						li.add(fs.copy());
						cnt -= fs.getAmount();
						fs.setAmount(0);

					}
				}
				if (cnt <= 0) break;
			}
			if (cnt > 0) return null;
		}
		return new FluidRecipeProcessResult(li);
	}

}

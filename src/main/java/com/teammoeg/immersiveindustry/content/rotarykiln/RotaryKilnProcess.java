package com.teammoeg.immersiveindustry.content.rotarykiln;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class RotaryKilnProcess {

	int process;                                    
	int processMax;
	int powerUsage;
	ResourceLocation recipeId;
	RotaryKilnRecipe recipeCache;
	public RotaryKilnProcess(RotaryKilnRecipe recipe) {
		process=0;
		setRecipe(recipe);
	}
	
	public RotaryKilnProcess(CompoundTag tag) {
		load(tag);
	}

	public RotaryKilnRecipe getRecipe(Level level) {
		if(recipeCache==null) {
			RotaryKilnRecipe rcp=RotaryKilnRecipe.recipeList.getById(level, recipeId);
			setRecipe(rcp);
		}
		return recipeCache;
	}
	public void setRecipe(RotaryKilnRecipe recipe) {
		recipeId=recipe.getId();
		recipeCache=recipe;
		processMax=recipe.time;
		powerUsage=recipe.tickEnergy;
	}
	public CompoundTag save() {
		CompoundTag tag=new CompoundTag();
		tag.putInt("process", process);
		tag.putInt("processMax", processMax);
		tag.putInt("power", powerUsage);
		tag.putString("recipe", recipeId.toString());		
		return tag;
	}
	public void load(CompoundTag tag) {
		process=tag.getInt("process");
		processMax=tag.getInt("processMax");
		powerUsage=tag.getInt("power");
		ResourceLocation newId=new ResourceLocation(tag.getString("recipe"));
		if(!newId.equals(recipeId))
			recipeCache=null;
	}
}

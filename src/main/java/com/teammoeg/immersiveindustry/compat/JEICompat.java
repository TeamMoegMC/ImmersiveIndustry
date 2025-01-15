/*
 * Copyright (c) 2021 TeamMoeg
 *
 * This file is part of Immersive Industry.
 *
 * Immersive Industry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Immersive Industry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Immersive Industry. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.immersiveindustry.compat;

import com.teammoeg.immersiveindustry.IIContent;
import com.teammoeg.immersiveindustry.IIMain;
import com.teammoeg.immersiveindustry.content.carkiln.CarKilnCategory;
import com.teammoeg.immersiveindustry.content.carkiln.CarKilnRecipe;
import com.teammoeg.immersiveindustry.content.carkiln.CarKilnScreen;
import com.teammoeg.immersiveindustry.content.crucible.CrucibleCategory;
import com.teammoeg.immersiveindustry.content.crucible.CrucibleRecipe;
import com.teammoeg.immersiveindustry.content.crucible.CrucibleScreen;
import com.teammoeg.immersiveindustry.content.electrolyzer.*;
import com.teammoeg.immersiveindustry.content.rotarykiln.RotaryKilnCategory;
import com.teammoeg.immersiveindustry.content.rotarykiln.RotaryKilnRecipe;
import com.teammoeg.immersiveindustry.content.rotarykiln.RotaryKilnScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.stream.Collectors;

@JeiPlugin
public class JEICompat implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(IIMain.MODID, "jei_plugin");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(IIContent.IIBlocks.burning_chamber.get()), CrucibleCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(IIContent.IIBlocks.electrolyzer.get()), ElectrolyzerCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(IIContent.IIMultiblocks.IND_ELE.blockItem().get()), IndustrialElectrolyzerCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(IIContent.IIMultiblocks.ROTARY_KILN.blockItem().get()), RotaryKilnCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(IIContent.IIMultiblocks.CAR_KILN.blockItem().get()), CarKilnCategory.UID);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientLevel world = Minecraft.getInstance().level;
        checkNotNull(world, "minecraft world");
        //RecipeManager recipeManager = world.getRecipeManager();
        registration.addRecipes(CrucibleCategory.UID,new ArrayList<>(CrucibleRecipe.recipeList.values()));
        registration.addRecipes(ElectrolyzerCategory.UID,ElectrolyzerRecipe.recipeList.getRecipes(world).stream().filter(r->!r.flag).filter(r->r.inputs.length<2).collect(Collectors.toList()));
        registration.addRecipes(IndustrialElectrolyzerCategory.UID,ElectrolyzerRecipe.recipeList.getRecipes(world).stream().filter(r -> r.inputs.length < 3).collect(Collectors.toList()));
        registration.addRecipes(RotaryKilnCategory.UID,new ArrayList<>(RotaryKilnRecipe.recipeList.getRecipes(world)));
        registration.addRecipes(CarKilnCategory.UID,CarKilnRecipe.recipeList);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new CrucibleCategory(guiHelper),
                new ElectrolyzerCategory(guiHelper),
                new IndustrialElectrolyzerCategory(guiHelper),
                new RotaryKilnCategory(guiHelper),
                new CarKilnCategory(guiHelper)
        );
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registry) {
        registry.addRecipeClickArea(ElectrolyzerScreen.class, 76, 35, 21, 25, ElectrolyzerCategory.UID);
        registry.addRecipeClickArea(IndustrialElectrolyzerScreen.class, 76, 35,21, 25, IndustrialElectrolyzerCategory.UID);
        registry.addRecipeClickArea(CrucibleScreen.class, 76, 14, 19, 25, CrucibleCategory.UID);
        registry.addRecipeClickArea(RotaryKilnScreen.class, 92, 41, 35, 18, RotaryKilnCategory.UID);
        registry.addRecipeClickArea(CarKilnScreen.class, 82, 25, 40, 18, CarKilnCategory.UID);
    }

    public static <T> void checkNotNull(@Nullable T object, String name) {
        if (object == null) {
            throw new NullPointerException(name + " must not be null.");
        }
    }
}

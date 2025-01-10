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

package com.teammoeg.immersiveindustry.data;

import com.teammoeg.immersiveindustry.content.carkiln.CarKilnRecipe;
import com.teammoeg.immersiveindustry.content.crucible.CrucibleRecipe;
import com.teammoeg.immersiveindustry.content.electrolyzer.ElectrolyzerRecipe;
import com.teammoeg.immersiveindustry.content.rotarykiln.RotaryKilnRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class IIRecipeReloadListener implements ResourceManagerReloadListener {

    public IIRecipeReloadListener() {
 
    }

	@Override
	public void onResourceManagerReload(ResourceManager pResourceManager) {
        /*if (dataPackRegistries != null) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                Iterator<ServerWorld> it = server.getWorlds().iterator();
                // Should only be false when no players are loaded, so the data will be synced on login
                if (it.hasNext())
                    ApiUtils.addFutureServerTask(it.next(),
                            () -> StaticTemplateManager.syncMultiblockTemplates(PacketDistributor.ALL.noArg(), true)
                    );
            }
        }*/
    }

    RecipeManager clientRecipeManager;


    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRecipesUpdated(RecipesUpdatedEvent event) {
        clientRecipeManager = event.getRecipeManager();
        if (!Minecraft.getInstance().isSingleplayer())
            buildRecipeLists(clientRecipeManager);
    }

    public static void buildRecipeLists(RecipeManager recipeManager) {
        Collection<Recipe<?>> recipes = recipeManager.getRecipes();
        if (recipes.size() == 0)
            return;
        CrucibleRecipe.recipeList = filterRecipes(recipes, CrucibleRecipe.class, CrucibleRecipe.TYPE);
        ElectrolyzerRecipe.recipeList = filterRecipes(recipes, ElectrolyzerRecipe.class, ElectrolyzerRecipe.TYPE);
        RotaryKilnRecipe.recipeList = filterRecipes(recipes, RotaryKilnRecipe.class, RotaryKilnRecipe.TYPE);
        CarKilnRecipe.recipeList = filterRecipes(recipes, CarKilnRecipe.class, CarKilnRecipe.TYPE).values().stream().sorted(Comparator.<CarKilnRecipe>comparingInt(e -> e.inputs.length).reversed().thenComparing(Comparator.<CarKilnRecipe>comparingInt(e->e.getInputAmount()).reversed())).collect(Collectors.toList());
//        for(CarKilnRecipe r:CarKilnRecipe.recipeList)
//        	System.out.println(r.inputs.length);
    }

    static <R extends Recipe<?>> Map<ResourceLocation, R> filterRecipes(Collection<Recipe<?>> recipes, Class<R> recipeClass, RegistryObject<RecipeType<R>> recipeType) {
        return recipes.stream()
                .filter(iRecipe -> iRecipe.getType() == recipeType.get())
                .map(recipeClass::cast)
                .collect(Collectors.toMap(recipe -> recipe.getId(), recipe -> recipe));
    }

 
}

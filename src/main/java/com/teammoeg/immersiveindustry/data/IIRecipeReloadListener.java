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

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.utils.TagUtils;
import blusunrize.immersiveengineering.common.blocks.multiblocks.StaticTemplateManager;
import com.teammoeg.immersiveindustry.content.carkiln.CarKilnRecipe;
import com.teammoeg.immersiveindustry.content.crucible.CrucibleRecipe;
import com.teammoeg.immersiveindustry.content.electrolyzer.ElectrolyzerRecipe;
import com.teammoeg.immersiveindustry.content.rotarykiln.RotaryKilnRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

public class IIRecipeReloadListener implements IResourceManagerReloadListener {
    private final DataPackRegistries dataPackRegistries;

    public IIRecipeReloadListener(DataPackRegistries dataPackRegistries) {
        this.dataPackRegistries = dataPackRegistries;
    }

    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
        if (dataPackRegistries != null) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                Iterator<ServerWorld> it = server.getWorlds().iterator();
                // Should only be false when no players are loaded, so the data will be synced on login
                if (it.hasNext())
                    ApiUtils.addFutureServerTask(it.next(),
                            () -> StaticTemplateManager.syncMultiblockTemplates(PacketDistributor.ALL.noArg(), true)
                    );
            }
        }
    }

    RecipeManager clientRecipeManager;

    @SubscribeEvent
    public void onTagsUpdated(TagsUpdatedEvent event) {
        if (clientRecipeManager != null)
            TagUtils.setTagCollectionGetters(ItemTags::getCollection, BlockTags::getCollection);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRecipesUpdated(RecipesUpdatedEvent event) {
        clientRecipeManager = event.getRecipeManager();
        if (!Minecraft.getInstance().isSingleplayer())
            buildRecipeLists(clientRecipeManager);
    }

    public static void buildRecipeLists(RecipeManager recipeManager) {
        Collection<IRecipe<?>> recipes = recipeManager.getRecipes();
        if (recipes.size() == 0)
            return;
        CrucibleRecipe.recipeList = filterRecipes(recipes, CrucibleRecipe.class, CrucibleRecipe.TYPE);
        ElectrolyzerRecipe.recipeList = filterRecipes(recipes, ElectrolyzerRecipe.class, ElectrolyzerRecipe.TYPE);
        RotaryKilnRecipe.recipeList = filterRecipes(recipes, RotaryKilnRecipe.class, RotaryKilnRecipe.TYPE);
        CarKilnRecipe.recipeList = filterRecipes(recipes, CarKilnRecipe.class, CarKilnRecipe.TYPE).values().stream().sorted(Comparator.<CarKilnRecipe>comparingInt(e -> e.inputs.length).reversed().thenComparing(Comparator.<CarKilnRecipe>comparingInt(e->e.getInputAmount()).reversed())).collect(Collectors.toList());
//        for(CarKilnRecipe r:CarKilnRecipe.recipeList)
//        	System.out.println(r.inputs.length);
    }

    static <R extends IRecipe<?>> Map<ResourceLocation, R> filterRecipes(Collection<IRecipe<?>> recipes, Class<R> recipeClass, IRecipeType<R> recipeType) {
        return recipes.stream()
                .filter(iRecipe -> iRecipe.getType() == recipeType)
                .map(recipeClass::cast)
                .collect(Collectors.toMap(recipe -> recipe.getId(), recipe -> recipe));
    }
}

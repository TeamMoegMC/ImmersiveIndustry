package com.teammoeg.immersiveindustry.compat;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.teammoeg.immersiveindustry.util.AccessableInfoArea;
import com.teammoeg.immersiveindustry.util.IIContainerScreen;

import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IClickableIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.renderer.Rect2i;

public class JEIGuiContainerHandler<T extends IIContainerScreen<?>> implements IGuiContainerHandler<T> {
	int xPos,yPos,width,height;
	RecipeType<?>[] recipeTypes;
	IIngredientManager manager;
	public JEIGuiContainerHandler(IIngredientManager manager,int xPos, int yPos, int width, int height, RecipeType<?>... type) {
		super();
		this.xPos = xPos;
		this.yPos = yPos;
		this.width = width;
		this.height = height;
		this.recipeTypes = type;
		this.manager=manager;
	}

	@Override
	public Collection<IGuiClickableArea> getGuiClickableAreas(T containerScreen, double mouseX, double mouseY) {
		IGuiClickableArea clickableArea = IGuiClickableArea.createBasic(xPos, yPos, width, height, recipeTypes);
		return List.of(clickableArea);
	}

	@Override
	public Optional<IClickableIngredient<?>> getClickableIngredientUnderMouse(T containerScreen, double mouseX, double mouseY) {
		AccessableInfoArea infoarea=containerScreen.getHoveredStack((int)mouseX, (int)mouseY);
		if(infoarea!=null) {
			Object stack=infoarea.getStack();
			if(stack==null)
				return Optional.empty();
			return manager.createTypedIngredient(stack).map(
				t -> new IClickableIngredient<Object>() {

					@Override
					public ITypedIngredient<Object> getTypedIngredient() {
						return t;
					}

					@Override
					public Rect2i getArea() {
						return infoarea.getArea();
					}

				});
		}
		return Optional.empty();
	}
}

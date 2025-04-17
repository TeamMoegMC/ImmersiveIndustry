package com.teammoeg.immersiveindustry.util;

import net.minecraft.client.renderer.Rect2i;

public interface AccessableInfoArea<T> {

	Rect2i getArea();

	T getStack();

}
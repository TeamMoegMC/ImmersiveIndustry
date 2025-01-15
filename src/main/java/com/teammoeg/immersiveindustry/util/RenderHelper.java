package com.teammoeg.immersiveindustry.util;

import java.util.function.Function;

import org.joml.Quaternionf;

import net.minecraft.Util;
import net.minecraft.core.Direction;

public class RenderHelper {
	public static final Function<Direction, Quaternionf> DIR_TO_FACING = Util
		.memoize(dir -> new Quaternionf().rotateAxis(-(float) (dir.toYRot() / 180 * Math.PI), 0, 1, 0));
	public RenderHelper() {
	}

}

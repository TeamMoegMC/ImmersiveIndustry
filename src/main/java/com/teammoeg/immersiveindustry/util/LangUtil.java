package com.teammoeg.immersiveindustry.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class LangUtil {

	private LangUtil() {
		
	}
	public static MutableComponent translate(String str,Object...objects ) {
		return Component.translatable(str, objects);
	}
	public static MutableComponent str(String str) {
		return Component.literal(str);
	}
}

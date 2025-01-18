package com.teammoeg.immersiveindustry.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.teammoeg.immersiveindustry.IIMain;

import blusunrize.immersiveengineering.common.blocks.multiblocks.IEMultiblocks;

@Mixin(IEMultiblocks.class)
public class IEInitMixin {
	@Inject(at=@At("TAIL"),method="init",remap=false,require=1)
	private static void II$modConstruction(CallbackInfo cbi) {
		IIMain.ieInit();
		//System.out.println("IEInit called");
	}
}

package com.teammoeg.immersiveindustry;

import com.teammoeg.immersiveindustry.content.carkiln.CarKilnRenderer;
import com.teammoeg.immersiveindustry.content.electrolyzer.IndustrialElectrolyzerRenderer;
import com.teammoeg.immersiveindustry.content.rotarykiln.RotaryKilnCallback;
import com.teammoeg.immersiveindustry.content.rotarykiln.RotaryKilnRenderer;
import com.teammoeg.immersiveindustry.util.DynamicBlockModelReference;

import blusunrize.immersiveengineering.api.client.ieobj.IEOBJCallbacks;
import net.minecraft.resources.ResourceLocation;

public class ClientProxy {
	public static void setup() {
		IndustrialElectrolyzerRenderer.ELECTRODES = DynamicBlockModelReference.getModelCached(IIMain.MODID, "block/multiblocks/industrial_electrode").register();
		RotaryKilnRenderer.ROLL= DynamicBlockModelReference.getModelCached(IIMain.MODID, "block/multiblocks/rotary_kiln_rotor").register();
		CarKilnRenderer.PARTS= DynamicBlockModelReference.getModelCached(new ResourceLocation(IIMain.MODID, "block/multiblocks/car_kiln_anim")).register();
		IEOBJCallbacks.register(new ResourceLocation(IIMain.MODID,"rotary_kiln"), RotaryKilnCallback.INSTANCE);
	}

}

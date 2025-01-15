package com.teammoeg.immersiveindustry;

import com.teammoeg.immersiveindustry.content.carkiln.CarKilnRenderer;
import com.teammoeg.immersiveindustry.content.electrolyzer.IndustrialElectrolyzerRenderer;
import com.teammoeg.immersiveindustry.content.rotarykiln.RotaryKilnRenderer;
import com.teammoeg.immersiveindustry.util.DynamicBlockModelReference;

import blusunrize.immersiveengineering.client.render.tile.DynamicModel;
import net.minecraft.resources.ResourceLocation;

public class ClientProxy {
	public static void setup() {
		IndustrialElectrolyzerRenderer.ELECTRODES = DynamicBlockModelReference.getModelCached(IIMain.MODID, "block/multiblocks/industrial_electrode");
		RotaryKilnRenderer.ROLL= DynamicBlockModelReference.getModelCached(IIMain.MODID, "block/multiblocks/rotary_kiln_rotor");
		CarKilnRenderer.PARTS= DynamicBlockModelReference.getModelCached(new ResourceLocation(IIMain.MODID, "block/multiblocks/car_kiln_anim.obj"));
	}

}

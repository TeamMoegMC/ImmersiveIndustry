package com.teammoeg.immersiveindustry;

import com.teammoeg.immersiveindustry.content.carklin.CarKilnRenderer;
import com.teammoeg.immersiveindustry.content.electrolyzer.IndustrialElectrolyzerRenderer;
import com.teammoeg.immersiveindustry.content.klin.RotaryKilnRenderer;

import blusunrize.immersiveengineering.client.render.tile.DynamicModel;
import blusunrize.immersiveengineering.client.render.tile.DynamicModel.ModelType;
import net.minecraft.util.ResourceLocation;

public class ClientProxy {
	public static void setup() {
		IndustrialElectrolyzerRenderer.ELECTRODES = DynamicModel.createSided(
				new ResourceLocation(IIMain.MODID, "block/multiblocks/industrial_electrode.obj"),
				"industrial_electrode", ModelType.IE_OBJ
		);
		RotaryKilnRenderer.ROLL= DynamicModel.createSided(
				new ResourceLocation(IIMain.MODID, "block/multiblocks/rotary_kiln_rotor.obj"),
				"rotary_kiln_rotor", ModelType.IE_OBJ
		);
		CarKilnRenderer.PARTS= DynamicModel.createSided(
				new ResourceLocation(IIMain.MODID, "block/multiblocks/car_kiln_anim.obj"),
				"car_kiln_animate", ModelType.IE_OBJ
		);
	}

}

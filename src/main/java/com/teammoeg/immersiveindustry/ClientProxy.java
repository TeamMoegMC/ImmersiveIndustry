package com.teammoeg.immersiveindustry;

import blusunrize.immersiveengineering.client.render.tile.DynamicModel;
import blusunrize.immersiveengineering.client.render.tile.DynamicModel.ModelType;
import com.teammoeg.immersiveindustry.content.carkiln.CarKilnRenderer;
import com.teammoeg.immersiveindustry.content.electrolyzer.IndustrialElectrolyzerRenderer;
import com.teammoeg.immersiveindustry.content.rotarykiln.RotaryKilnRenderer;
import net.minecraft.resources.ResourceLocation;

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

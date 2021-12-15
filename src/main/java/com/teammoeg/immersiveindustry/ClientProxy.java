package com.teammoeg.immersiveindustry;

import blusunrize.immersiveengineering.client.render.tile.DynamicModel;
import blusunrize.immersiveengineering.client.render.tile.DynamicModel.ModelType;
import com.teammoeg.immersiveindustry.content.electrolyzer.IndustrialElectrolyzerRenderer;
import net.minecraft.util.ResourceLocation;

public class ClientProxy {
	public static void setup() {
		IndustrialElectrolyzerRenderer.ELECTRODES = DynamicModel.createSided(
				new ResourceLocation(IIMain.MODID, "block/multiblocks/industrial_electrode.obj"),
				"industrial_electrode", ModelType.IE_OBJ
		);
		;
	}
}

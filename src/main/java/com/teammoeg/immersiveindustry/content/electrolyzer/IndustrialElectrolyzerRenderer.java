package com.teammoeg.immersiveindustry.content.electrolyzer;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teammoeg.immersiveindustry.IIContent.IIMultiblocks;

import blusunrize.immersiveengineering.api.IEProperties.IEObjState;
import blusunrize.immersiveengineering.api.IEProperties.Model;
import blusunrize.immersiveengineering.api.IEProperties.VisibilityList;
import blusunrize.immersiveengineering.api.utils.client.SinglePropertyModelData;
import blusunrize.immersiveengineering.client.render.tile.DynamicModel;
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class IndustrialElectrolyzerRenderer extends TileEntityRenderer<IndustrialElectrolyzerBlockEntity> {
    public IndustrialElectrolyzerRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }
    public static DynamicModel<Direction> ELECTRODES;
	@Override
    public void render(IndustrialElectrolyzerBlockEntity te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		if(!te.formed||te.isDummy()||!te.getWorldNonnull().isBlockLoaded(te.getPos()))
			return;
		List<String> renderedParts = new ArrayList<>();
		if(!te.getInventory().get(3).isEmpty()) {
			renderedParts.add("anode1");
			renderedParts.add("anode2");
			renderedParts.add("anode3");
			renderedParts.add("anode4");
		}
		if(!te.getInventory().get(4).isEmpty()) {
			renderedParts.add("anode5");
			renderedParts.add("anode6");
			renderedParts.add("anode7");
			renderedParts.add("anode8");
		}
		if(renderedParts.isEmpty())
			return;
		BlockPos blockPos = te.getPos();
		BlockState state = te.getWorld().getBlockState(blockPos);
		if(state.getBlock()!=IIMultiblocks.industrial_electrolyzer)
			return;
		IEObjState objState = new IEObjState(VisibilityList.show(renderedParts));

		matrixStack.push();
		List<BakedQuad> quads = ELECTRODES.getNullQuads(te.getFacing(), state, new SinglePropertyModelData<>(objState, Model.IE_OBJ_STATE));
		RenderUtils.renderModelTESRFast(quads, bufferIn.getBuffer(RenderType.getSolid()), matrixStack, combinedLightIn, combinedOverlayIn);
		matrixStack.pop();
    }
}

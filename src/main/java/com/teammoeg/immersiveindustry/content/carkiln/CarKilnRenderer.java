package com.teammoeg.immersiveindustry.content.carkiln;

import blusunrize.immersiveengineering.api.IEProperties.IEObjState;
import blusunrize.immersiveengineering.api.IEProperties.Model;
import blusunrize.immersiveengineering.api.IEProperties.VisibilityList;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.client.models.obj.callback.DynamicSubmodelCallbacks;
import blusunrize.immersiveengineering.client.render.tile.DynamicModel;
import blusunrize.immersiveengineering.client.utils.RenderUtils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.immersiveindustry.IIContent.IIMultiblocks;
import com.teammoeg.immersiveindustry.content.rotarykiln.RotaryKilnState;
import com.teammoeg.immersiveindustry.util.DynamicBlockModelReference;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.model.data.ModelData;

public class CarKilnRenderer implements BlockEntityRenderer<MultiblockBlockEntityMaster<CarKilnState>> {
	public CarKilnRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
	}

	public static DynamicBlockModelReference PARTS;
	private static final ModelData gate= ModelData.builder().with(DynamicSubmodelCallbacks.getProperty(),VisibilityList.show("inletBoard")).build();
	private static final ModelData trolley=ModelData.builder().with(DynamicSubmodelCallbacks.getProperty(),VisibilityList.show("trolleyFloor1","trolleyFloor2")).build();
	private static final ModelData s1=ModelData.builder().with(DynamicSubmodelCallbacks.getProperty(),VisibilityList.show("shelf2")).build();
	private static final ModelData s2=ModelData.builder().with(DynamicSubmodelCallbacks.getProperty(),VisibilityList.show("shelf1","shelf2")).build();

	@Override
	public void render(MultiblockBlockEntityMaster<CarKilnState> pBlockEntity, float pPartialTick, PoseStack matrixStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
		Level l=pBlockEntity.getHelper().getContext().getLevel().getRawLevel();
		Direction d=pBlockEntity.getHelper().getContext().getLevel().getOrientation().front();
		matrixStack.pushPose();
		if(te.pos<24) {
			matrixStack.translate(0,1.75, 0);
		}else {
			matrixStack.translate(0,1.75-(te.pos-24)/16D,0);
		}
		RenderUtils.renderModelTESRFast(PARTS.apply(gate), pBuffer.getBuffer(RenderType.solid()), matrixStack, pPackedOverlay, pPackedOverlay);
		matrixStack.popPose();
		matrixStack.pushPose();
		if(te.pos<=24){
			double delta=te.pos/16D-1.5;
			matrixStack.translate(delta*d.getXOffset(),0,delta*d.getZOffset());
		}
		int titem=te.modelState;
		if(titem>0) {
			if(titem>16)
				RenderUtils.renderModelTESRFast(PARTS.getNullQuads(d, state, new SinglePropertyModelData<>(s2, Model.IE_OBJ_STATE)), bufferIn.getBuffer(RenderType.getSolid()), matrixStack, combinedLightIn, combinedOverlayIn);
			else
				RenderUtils.renderModelTESRFast(PARTS.getNullQuads(d, state, new SinglePropertyModelData<>(s1, Model.IE_OBJ_STATE)), bufferIn.getBuffer(RenderType.getSolid()), matrixStack, combinedLightIn, combinedOverlayIn);
		}
		RenderUtils.renderModelTESRFast(PARTS.getNullQuads(d, state, new SinglePropertyModelData<>(trolley, Model.IE_OBJ_STATE)), bufferIn.getBuffer(RenderType.getSolid()), matrixStack, combinedLightIn, combinedOverlayIn);
		matrixStack.popPose();
	}
}

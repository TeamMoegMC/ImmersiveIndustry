package com.teammoeg.immersiveindustry.content.carkiln;

import blusunrize.immersiveengineering.api.IEProperties.VisibilityList;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.client.models.obj.callback.DynamicSubmodelCallbacks;
import blusunrize.immersiveengineering.client.utils.RenderUtils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.immersiveindustry.util.DynamicBlockModelReference;
import com.teammoeg.immersiveindustry.util.RenderHelper;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.util.Lazy;

public class CarKilnRenderer implements BlockEntityRenderer<MultiblockBlockEntityMaster<CarKilnState>> {
	public CarKilnRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
	}

	public static DynamicBlockModelReference PARTS;
	private static final Lazy<ModelData> gate= Lazy.of(()->ModelData.builder().with(DynamicSubmodelCallbacks.getProperty(),VisibilityList.show("inletBoard")).build());
	private static final Lazy<ModelData> trolley=Lazy.of(()->ModelData.builder().with(DynamicSubmodelCallbacks.getProperty(),VisibilityList.show("trolleyFloor1","trolleyFloor2")).build());
	private static final Lazy<ModelData> s1=Lazy.of(()->ModelData.builder().with(DynamicSubmodelCallbacks.getProperty(),VisibilityList.show("shelf2")).build());
	private static final Lazy<ModelData> s2=Lazy.of(()->ModelData.builder().with(DynamicSubmodelCallbacks.getProperty(),VisibilityList.show("shelf1","shelf2")).build());

	@Override
	public void render(MultiblockBlockEntityMaster<CarKilnState> pBlockEntity, float pPartialTick, PoseStack matrixStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
		Level l=pBlockEntity.getHelper().getContext().getLevel().getRawLevel();
		Direction d=pBlockEntity.getHelper().getContext().getLevel().getOrientation().front();
		int pos=pBlockEntity.getHelper().getState().pos;
		matrixStack.pushPose();
		matrixStack.mulPose(RenderHelper.DIR_TO_FACING.apply(d.getOpposite()));
		matrixStack.pushPose();
		if(pos<24) {
			matrixStack.translate(0,1.75, 0);
		}else {
			matrixStack.translate(0,1.75-(pos-24)/16D,0);
		}
		// weird issue, model is shifted, so we fix it
		//matrixStack.translate(0, 0, -2);
		RenderUtils.renderModelTESRFast(PARTS.apply(gate.get()), pBuffer.getBuffer(RenderType.solid()), matrixStack, pPackedLight, pPackedOverlay);
		matrixStack.popPose();
		matrixStack.pushPose();
		if(pos<=24){
			double delta=pos/16D-1.5;
			matrixStack.translate(delta*d.getStepX(),0,delta*d.getStepZ());
		}
		int titem=pBlockEntity.getHelper().getState().maxProcessCount;

		// weird issue, model is shifted, so we fix it
		//matrixStack.translate(0, 0, -2);
		if(titem>0) {
			if(titem>16)
				RenderUtils.renderModelTESRFast(PARTS.apply(s2.get()),pBuffer.getBuffer(RenderType.solid()), matrixStack, pPackedLight, pPackedOverlay);
			else
				RenderUtils.renderModelTESRFast(PARTS.apply(s1.get()),pBuffer.getBuffer(RenderType.solid()), matrixStack, pPackedLight, pPackedOverlay);
		}
		RenderUtils.renderModelTESRFast(PARTS.apply(trolley.get()), pBuffer.getBuffer(RenderType.solid()), matrixStack, pPackedLight, pPackedOverlay);
		matrixStack.popPose();
		matrixStack.popPose();
	}
}

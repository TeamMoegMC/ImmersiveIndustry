package com.teammoeg.immersiveindustry.content.electrolyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.immersiveindustry.util.DynamicBlockModelReference;
import com.teammoeg.immersiveindustry.util.RenderHelper;

import blusunrize.immersiveengineering.api.IEProperties.VisibilityList;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.client.models.obj.callback.DynamicSubmodelCallbacks;
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraftforge.client.model.data.ModelData;

public class IndustrialElectrolyzerRenderer implements BlockEntityRenderer<MultiblockBlockEntityMaster<IndustrialElectrolyzerState>> {
	public IndustrialElectrolyzerRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
    }
    public static DynamicBlockModelReference ELECTRODES;
    public static final Function<Integer,ModelData> getData=Util.memoize((t)->{
    	List<String> renderedParts = new ArrayList<>();
		if((t&1)==1) {
			renderedParts.add("anode1");
			renderedParts.add("anode2");
			renderedParts.add("anode3");
			renderedParts.add("anode4");
		}
		if((t&2)==2) {
			renderedParts.add("anode5");
			renderedParts.add("anode6");
			renderedParts.add("anode7");
			renderedParts.add("anode8");
		}
    	return ModelData.builder().with(DynamicSubmodelCallbacks.getProperty(), VisibilityList.show(renderedParts)).build();
    });
	@Override
    public void render(MultiblockBlockEntityMaster<IndustrialElectrolyzerState> te, float partialTicks, PoseStack matrixStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
		IndustrialElectrolyzerState state=te.getHelper().getState();
		int type=state.hasElectrode1?1:0;
		type+=state.hasElectrode2?2:0;
		if(type==0)return;
		matrixStack.pushPose();
		matrixStack.mulPose(RenderHelper.DIR_TO_FACING.apply(te.getHelper().getContext().getLevel().getOrientation().front()));
		// translate the position by x-=1, z-=1
		matrixStack.translate(-1, 0, -1);
		List<BakedQuad> quads = ELECTRODES.apply(getData.apply(type));
		RenderUtils.renderModelTESRFast(quads, pBuffer.getBuffer(RenderType.solid()), matrixStack, pPackedLight, pPackedOverlay);
		matrixStack.popPose();
    }
}

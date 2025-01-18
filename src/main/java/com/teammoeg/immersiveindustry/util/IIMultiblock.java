package com.teammoeg.immersiveindustry.util;

import java.util.List;
import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teammoeg.immersiveindustry.IIMain;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks.MultiblockManualData;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.client.utils.BasicClientProperties;
import blusunrize.immersiveengineering.client.utils.IERenderTypes;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public abstract class IIMultiblock extends IETemplateMultiblock {
	DynamicBlockModelReference dm;
	public IIMultiblock(ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin, BlockPos size,
			MultiblockRegistration<?> logic) {
		super(loc, masterFromOrigin, triggerFromOrigin, size, logic);
		dm=DynamicBlockModelReference.getModelCached(IIMain.MODID, "block/"+loc.getPath()).register();
	}

	@Override
	public void initializeClient(Consumer<MultiblockManualData> consumer)
	{
		consumer.accept(new BasicClientProperties(this) {

			@Override
			public void renderFormedStructure(PoseStack transform, MultiBufferSource bufferSource) {
				transform.pushPose();
				BlockPos offset = getMasterFromOriginOffset();
				transform.translate(offset.getX(), offset.getY(), offset.getZ());
				List<BakedQuad> nullQuads = dm.getAllQuads();
				VertexConsumer buffer = bufferSource.getBuffer(IERenderTypes.TRANSLUCENT_FULLBRIGHT);
				nullQuads.forEach(quad -> buffer.putBulkData(
						transform.last(), quad, 1, 1, 1, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY
				));
				transform.popPose();
			}
			
		});
	}

}

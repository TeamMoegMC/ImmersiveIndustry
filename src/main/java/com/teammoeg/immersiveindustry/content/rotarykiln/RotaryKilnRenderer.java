package com.teammoeg.immersiveindustry.content.rotarykiln;

import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.client.utils.RenderUtils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.immersiveindustry.util.DynamicBlockModelReference;
import com.teammoeg.immersiveindustry.util.RenderHelper;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import java.util.List;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class RotaryKilnRenderer implements BlockEntityRenderer<MultiblockBlockEntityMaster<RotaryKilnState>> {
	public RotaryKilnRenderer(BlockEntityRenderDispatcher rendererDispatcherIn) {
	}
	public static DynamicBlockModelReference ROLL;
	Quaternionf rotateH=new Quaternionf().rotateAxis((float) (-2f/180*Math.PI), new Vector3f(1,0,0));
	@Override
	public void render(MultiblockBlockEntityMaster<RotaryKilnState> te, float pPartialTick, PoseStack matrixStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
		Direction d=te.getHelper().getContext().getLevel().getOrientation().front();
		matrixStack.pushPose();
		int dx=0,dz=0;
		switch(d) {
		case NORTH:;dx=-1;dz=-1;break;
		case EAST:;dx=-1;break;
		case WEST:;dz=-1;break;
		}
		matrixStack.mulPose(RenderHelper.DIR_TO_FACING.apply(d));
		matrixStack.translate(.5+dx,-0.1875,-2.375+dz);
		matrixStack.mulPose(rotateH);
		matrixStack.translate(0,0.875,-0.5);
		matrixStack.mulPose(new Quaternionf().rotateAxis((float) (te.getHelper().getState().angle/180*Math.PI), new Vector3f(0,0,1)));
		matrixStack.translate(-1,-0.0625,0);
		List<BakedQuad> quads = ROLL.getAllQuads();
		RenderUtils.renderModelTESRFast(quads, pBuffer.getBuffer(RenderType.solid()), matrixStack, pPackedLight, pPackedOverlay);
		matrixStack.popPose();
	}
}

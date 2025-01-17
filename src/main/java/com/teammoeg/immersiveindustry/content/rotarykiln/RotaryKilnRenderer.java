package com.teammoeg.immersiveindustry.content.rotarykiln;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.immersiveindustry.util.DynamicBlockModelReference;
import com.teammoeg.immersiveindustry.util.RenderHelper;

import blusunrize.immersiveengineering.api.IEProperties.VisibilityList;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.client.models.obj.callback.DynamicSubmodelCallbacks;
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import blusunrize.immersiveengineering.common.config.IEClientConfig;
import net.minecraft.Util;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.util.Lazy;

public class RotaryKilnRenderer implements BlockEntityRenderer<MultiblockBlockEntityMaster<RotaryKilnState>> {
	public RotaryKilnRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
	}
	public static DynamicBlockModelReference ROLL;
	Quaternionf rotateH=new Quaternionf().rotateAxis((float) (2f/180*Math.PI), new Vector3f(1,0,0));
	Lazy<ModelData> data=Lazy.of(()->ModelData.builder().with(DynamicSubmodelCallbacks.getProperty(), VisibilityList.show(Arrays.asList("rotorInEnd"))).build());
	@Override
	public void render(MultiblockBlockEntityMaster<RotaryKilnState> te, float pPartialTick, PoseStack matrixStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
		Direction d=te.getHelper().getContext().getLevel().getOrientation().front().getOpposite();
		BlockPos lightPos=te.getBlockPos().above(2);
		matrixStack.pushPose();
		float dx=0,dz=0;
		switch(d) {
		case SOUTH:;break;
		case NORTH:dx=-1;dz=-4;break;
		case EAST:;dx=2;dz=-3;break;
		case WEST:;dz=-2;dx=-3.625f;break;
		}
		//matrixStack.translate(0, 2, 0);
		
		
		
		matrixStack.translate(1, 0, 6);
		matrixStack.translate(.5+dx,-0.1875,-2.625+dz);
		
		matrixStack.mulPose(rotateH);
		matrixStack.translate(0,0.875,-0.5);
		
		matrixStack.rotateAround(new Quaternionf().rotateAxis((float) (te.getHelper().getState().angle*1f/180*Math.PI), new Vector3f(0,0,1)),.5f,.5f,.5f);
		matrixStack.translate(-1,-0.0625,0);
		List<BakedQuad> quads = ROLL.apply(data.get());
		
		int calculatedLight =LevelRenderer.getLightColor(te.getLevel(), lightPos);
		
		matrixStack.rotateAround(RenderHelper.DIR_TO_FACING.apply(d),.5f,.5f,.5f);
		RenderUtils.renderModelTESRFast(quads, pBuffer.getBuffer(RenderType.solid()), matrixStack, calculatedLight, pPackedOverlay);
		matrixStack.popPose();
	}
	@Override
	public int getViewDistance()
	{
		double increase = IEClientConfig.increasedTileRenderdistance.get();
		return (int)(BlockEntityRenderer.super.getViewDistance()*increase);
	}
}

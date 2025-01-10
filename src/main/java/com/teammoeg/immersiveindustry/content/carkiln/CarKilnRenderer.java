package com.teammoeg.immersiveindustry.content.carkiln;

import blusunrize.immersiveengineering.api.IEProperties.IEObjState;
import blusunrize.immersiveengineering.api.IEProperties.Model;
import blusunrize.immersiveengineering.api.IEProperties.VisibilityList;
import blusunrize.immersiveengineering.api.utils.client.SinglePropertyModelData;
import blusunrize.immersiveengineering.client.render.tile.DynamicModel;
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.teammoeg.immersiveindustry.IIContent.IIMultiblocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class CarKilnRenderer extends TileEntityRenderer<CarKilnBlockEntity> {
	public CarKilnRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	public static DynamicModel<Direction> PARTS;
	private static final IEObjState gate= new IEObjState(VisibilityList.show("inletBoard"));
	private static final IEObjState trolley=new IEObjState(VisibilityList.show("trolleyFloor1","trolleyFloor2"));
	private static final IEObjState s1=new IEObjState(VisibilityList.show("shelf2"));
	private static final IEObjState s2=new IEObjState(VisibilityList.show("shelf1","shelf2"));
	
	@Override
    public void render(CarKilnBlockEntity te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		if(!te.formed||te.isDummy()||!te.getWorldNonnull().isBlockLoaded(te.getPos()))
			return;
		BlockPos blockPos = te.getPos();
		BlockState state = te.getWorld().getBlockState(blockPos);
		if(state.getBlock()!=IIMultiblocks.car_kiln)
			return;
		Direction d=te.getFacing();
		matrixStack.push();
		if(te.pos<24) {
			matrixStack.translate(0,1.75, 0);
		}else {
			matrixStack.translate(0,1.75-(te.pos-24)/16D,0);
		}
		RenderUtils.renderModelTESRFast(PARTS.getNullQuads(d, state, new SinglePropertyModelData<>(gate, Model.IE_OBJ_STATE)), bufferIn.getBuffer(RenderType.getSolid()), matrixStack, combinedLightIn, combinedOverlayIn);
		matrixStack.pop();
		matrixStack.push();
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
		matrixStack.pop();
    }
}

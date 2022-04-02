package com.teammoeg.immersiveindustry.content.carklin;

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

public class CarKilnRenderer extends TileEntityRenderer<CarKilnTileEntity> {
    public CarKilnRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }
    public static DynamicModel<Direction> PARTS;
    private static IEObjState gate;
    private static IEObjState trolley;
    static {
    	gate=new IEObjState(VisibilityList.show("inletBoard"));
    	trolley=new IEObjState(VisibilityList.show("trolleyFloor1","trolleyFloor2"));
    }
	@Override
    public void render(CarKilnTileEntity te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		if(!te.formed||te.isDummy()||!te.getWorldNonnull().isBlockLoaded(te.getPos()))
			return;
		BlockPos blockPos = te.getPos();
		BlockState state = te.getWorld().getBlockState(blockPos);
		if(state.getBlock()!=IIMultiblocks.car_kiln)
			return;
		Direction d=te.getFacing();
		matrixStack.push();
		List<BakedQuad> quads = PARTS.getNullQuads(d, state, new SinglePropertyModelData<>(gate, Model.IE_OBJ_STATE));
		if(te.pos<23) {
			matrixStack.translate(0,1.75, 0);
		}else {
			matrixStack.translate(0,1.75-(te.pos-23)/16D,0);
		}
		RenderUtils.renderModelTESRFast(quads, bufferIn.getBuffer(RenderType.getSolid()), matrixStack, combinedLightIn, combinedOverlayIn);
		matrixStack.pop();
		matrixStack.push();
		List<BakedQuad> quads2 = PARTS.getNullQuads(d, state, new SinglePropertyModelData<>(trolley, Model.IE_OBJ_STATE));
		if(te.pos>23) {
			matrixStack.translate(0, 0, 0);
		}else {
			matrixStack.translate(0,0,1.4375-te.pos/16D);
		}
		RenderUtils.renderModelTESRFast(quads2, bufferIn.getBuffer(RenderType.getSolid()), matrixStack, combinedLightIn, combinedOverlayIn);
		matrixStack.pop();
    }
}

package com.teammoeg.immersiveindustry.content.rotarykiln;

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
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

import java.util.List;

public class RotaryKilnRenderer extends TileEntityRenderer<RotaryKilnBlockEntity> {
	public RotaryKilnRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	public static DynamicModel<Direction> ROLL;

	@Override
	public void render(RotaryKilnBlockEntity te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		if (!te.formed || te.isDummy() || !te.getWorldNonnull().isBlockLoaded(te.getPos()))
			return;
		BlockPos blockPos = te.getPos();
		BlockState state = te.getWorld().getBlockState(blockPos);
		if(state.getBlock()!=IIMultiblocks.rotary_kiln)
			return;
		Direction d=te.getFacing();
		matrixStack.push();
		int deg=0,dx=0,dz=0;
		switch(d) {
		case NORTH:deg=180;dx=-1;dz=-1;break;
		case EAST:deg=90;dx=-1;break;
		case WEST:deg=-90;dz=-1;break;
		}
		matrixStack.rotate(new Quaternion(0,deg,0,true));
		matrixStack.translate(.5+dx,-0.1875,-2.375+dz);
		matrixStack.rotate(new Quaternion(new Vector3f(1,0,0),-2,true));
		matrixStack.translate(0,0.875,-0.5);
		matrixStack.rotate(new Quaternion(new Vector3f(0,0,1),te.angle,true));
		matrixStack.translate(-1,-0.0625,0);
		List<BakedQuad> quads = ROLL.getNullQuads(Direction.SOUTH, state);
		RenderUtils.renderModelTESRFast(quads, bufferIn.getBuffer(RenderType.getSolid()), matrixStack, combinedLightIn, combinedOverlayIn);
		matrixStack.pop();
    }
}

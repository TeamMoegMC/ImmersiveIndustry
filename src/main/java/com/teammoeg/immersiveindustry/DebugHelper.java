package com.teammoeg.immersiveindustry;

import java.util.OptionalDouble;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;

public class DebugHelper {
	public static RenderType BOLD_LINE_TYPE;
	static {
		RenderType.SteamTurbineState renderState;
		renderState = RenderStateAccess.getState();
		BOLD_LINE_TYPE = RenderType.makeType("ii_line_bold", DefaultVertexFormats.POSITION_COLOR, GL11.GL_LINES,
				128, renderState);
	}
	//hack to access render state protected members
	public static class RenderStateAccess extends RenderState {
		public static RenderType.SteamTurbineState getState() {
			return RenderType.State.getBuilder().line(new RenderState.LineState(OptionalDouble.of(1)))//this is line width
					.layer(VIEW_OFFSET_Z_LAYERING).target(MAIN_TARGET).writeMask(COLOR_DEPTH_WRITE).build(true);
		}

		public RenderStateAccess(String p_i225973_1_, Runnable p_i225973_2_, Runnable p_i225973_3_) {
			super(p_i225973_1_, p_i225973_2_, p_i225973_3_);
		}

	}
	//draw a line from start to end by color, ABSOLUTE POSITION
	public static void drawLine(MatrixStack matrixStack,float startX,float startY,float startZ,float endX,float endY,float endZ) {
		IVertexBuilder vertexBuilderLines = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource().getBuffer(BOLD_LINE_TYPE);
		drawLine(matrixStack.getLast().getMatrix(),vertexBuilderLines,startX,startY,startZ,endX,endY,endZ);
	}

	private static void drawLine(Matrix4f mat,IVertexBuilder renderBuffer,float startX,float startY,float startZ,float endX,float endY,float endZ) {
		renderBuffer.pos(mat,startX,startY,startZ)
				.color(255,0,0,0)
				.endVertex();
		renderBuffer.pos(mat,endX,endY,endZ)
				.color(255,0,0,0)
				.endVertex();
	}
}

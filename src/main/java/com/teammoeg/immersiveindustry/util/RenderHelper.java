package com.teammoeg.immersiveindustry.util;

import java.util.function.Function;

import blusunrize.immersiveengineering.client.utils.IERenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import net.minecraft.Util;
import net.minecraft.core.Direction;

public class RenderHelper {
	public static final Function<Direction, Quaternionf> DIR_TO_FACING = Util
		.memoize(dir -> new Quaternionf().rotateAxis(-(float) (dir.toYRot() / 180 * Math.PI), 0, 1, 0));
	public RenderHelper() {
	}



	//Create a vertical indicator to display block placement direction
	public static void drawVerticalIndicator(PoseStack.Pose pose, MultiBufferSource buffers, float y, Direction facing, Direction hitSide) {
		Matrix4f mat = pose.pose();
		Matrix3f matN = pose.normal();

		VertexConsumer builder = buffers.getBuffer(RenderType.lines());

		float cx = 0.5f;
		float cz = 0.5f;
		float rOuter = 0.35f;
		float rInner = 0.15f;

		float blackR = 0.0f, blackG = 0.0f, blackB = 0.0f, blackA = 0.5f;

		float orangeR = 1.0f, orangeG = 0.6f, orangeB = 0.0f, orangeA = 0.8f;


		drawSquare(builder, mat, matN, y, cx, cz, rOuter, blackR, blackG, blackB, blackA);

		if (facing != hitSide) {
			builder.vertex(mat, cx - rInner, y, cz - rInner).color(orangeR, orangeG, orangeB, orangeA).normal(matN, 0, 1, 0).endVertex();
			builder.vertex(mat, cx + rInner, y, cz + rInner).color(orangeR, orangeG, orangeB, orangeA).normal(matN, 0, 1, 0).endVertex();


			builder.vertex(mat, cx + rInner, y, cz - rInner).color(orangeR, orangeG, orangeB, orangeA).normal(matN, 0, 1, 0).endVertex();
			builder.vertex(mat, cx - rInner, y, cz + rInner).color(orangeR, orangeG, orangeB, orangeA).normal(matN, 0, 1, 0).endVertex();
		} else {
			drawSquare(builder, mat, matN, y, cx, cz, rInner, orangeR, orangeG, orangeB, orangeA);
		}
	}


	private static void drawSquare(VertexConsumer builder, Matrix4f mat, Matrix3f matN, float y, float cx, float cz, float r, float red, float gr, float bl, float al) {

		builder.vertex(mat, cx - r, y, cz - r).color(red, gr, bl, al).normal(matN, 0, 1, 0).endVertex();
		builder.vertex(mat, cx + r, y, cz - r).color(red, gr, bl, al).normal(matN, 0, 1, 0).endVertex();

		builder.vertex(mat, cx + r, y, cz - r).color(red, gr, bl, al).normal(matN, 0, 1, 0).endVertex();
		builder.vertex(mat, cx + r, y, cz + r).color(red, gr, bl, al).normal(matN, 0, 1, 0).endVertex();

		builder.vertex(mat, cx + r, y, cz + r).color(red, gr, bl, al).normal(matN, 0, 1, 0).endVertex();
		builder.vertex(mat, cx - r, y, cz + r).color(red, gr, bl, al).normal(matN, 0, 1, 0).endVertex();

		builder.vertex(mat, cx - r, y, cz + r).color(red, gr, bl, al).normal(matN, 0, 1, 0).endVertex();
		builder.vertex(mat, cx - r, y, cz - r).color(red, gr, bl, al).normal(matN, 0, 1, 0).endVertex();
	}

}

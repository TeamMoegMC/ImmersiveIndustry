package com.teammoeg.immersiveindustry;

import blusunrize.immersiveengineering.client.BlockOverlayUtils;
import blusunrize.immersiveengineering.client.utils.IERenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teammoeg.immersiveindustry.content.IDirectionPropertyBlock;
import com.teammoeg.immersiveindustry.util.RenderHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@Mod.EventBusSubscriber(modid = IIMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void renderAdditionalBlockBounds(RenderHighlightEvent.Block event)
    {
        if(event.getTarget().getType()== HitResult.Type.BLOCK && event.getCamera().getEntity() instanceof Player player)
        {
            BlockHitResult rtr = event.getTarget();
            BlockPos pos = rtr.getBlockPos();
            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
            Block block = Block.byItem(stack.getItem());

            if (!stack.isEmpty() && Block.byItem(stack.getItem()) instanceof IDirectionPropertyBlock dirBlock
                    && rtr.getDirection().getAxis() == Direction.Axis.Y) {

                PoseStack transform = event.getPoseStack();
                Vec3 renderView = event.getCamera().getPosition();
                transform.pushPose();

                transform.translate(-renderView.x, -renderView.y, -renderView.z);
                transform.translate(pos.getX(), pos.getY(), pos.getZ());

                Level world = player.level();
                Direction hitSide = rtr.getDirection();
                VoxelShape shape = world.getBlockState(pos).getBlockSupportShape(world, pos);
                AABB targetedBB = shape.isEmpty() ? new AABB(0,0,0,1,1,1) : shape.bounds();

                MultiBufferSource buffers = event.getMultiBufferSource();
                float eps = 0.002F;
                float y = (float) (hitSide == Direction.DOWN ? targetedBB.minY - eps : targetedBB.maxY + eps);

                BlockPlaceContext context = new BlockPlaceContext(new UseOnContext(player, InteractionHand.MAIN_HAND, rtr));


                BlockState predictedState = block.getStateForPlacement(context);

                if (predictedState != null) {

                    Property<Direction> prop = dirBlock.getDirectionProperty();


                    if (predictedState.hasProperty(prop)) {
                        Direction facing = predictedState.getValue(prop);

                        if (facing.getAxis() == Direction.Axis.Y) {
                            RenderHelper.drawVerticalIndicator(transform.last(), buffers, y, facing, hitSide);

                        } else {
                            Matrix4f mat = transform.last().pose();
                            Matrix3f matN = transform.last().normal();

                            VertexConsumer lineBuilder = buffers.getBuffer(IERenderTypes.LINES);
                            float sqrt2Half = (float) (Math.sqrt(2) / 2);

                            lineBuilder.vertex(mat, 0 - eps, y, 0 - eps).color(0, 0, 0, 0.4F).normal(matN, sqrt2Half, 0, sqrt2Half).endVertex();
                            lineBuilder.vertex(mat, 1 + eps, y, 1 + eps).color(0, 0, 0, 0.4F).normal(matN, sqrt2Half, 0, sqrt2Half).endVertex();
                            lineBuilder.vertex(mat, 0 - eps, y, 1 + eps).color(0, 0, 0, 0.4F).normal(matN, sqrt2Half, 0, -sqrt2Half).endVertex();
                            lineBuilder.vertex(mat, 1 + eps, y, 0 - eps).color(0, 0, 0, 0.4F).normal(matN, sqrt2Half, 0, -sqrt2Half).endVertex();


                            Vec3 dir = new Vec3(facing.getStepX(), 0, facing.getStepZ());
                            BlockOverlayUtils.drawBlockOverlayArrow(transform.last(), buffers, dir, hitSide, targetedBB);
                        }
                    }
                }
                transform.popPose();
            }
        }
    }
}

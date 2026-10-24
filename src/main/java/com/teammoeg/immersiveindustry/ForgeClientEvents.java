package com.teammoeg.immersiveindustry;

import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = IIMain.MODID, value = Dist.CLIENT)
public class ForgeClientEvents {
    
    @SubscribeEvent
    @SuppressWarnings("ExtractMethodRecommender")
    public static void renderMultiblockInHandBound(RenderLevelStageEvent event) {
        if(event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) return;
        if(ForgeEvents.multiblockBound == null) return;
        var hitResult = Minecraft.getInstance().hitResult;
        if(!(hitResult instanceof BlockHitResult blockHitResult) || Objects.requireNonNull(Minecraft.getInstance().level).getBlockState(blockHitResult.getBlockPos()).isAir()) return;
        var player = Objects.requireNonNull(Minecraft.getInstance().player);
        var poseStack = event.getPoseStack();
        var camera = event.getCamera();
        var vec3 = camera.getPosition();
        // Same anchor the placement uses, so the hologram is exactly where the machine lands.
        var pos = ForgeEvents.getPlacementOrigin(blockHitResult.getBlockPos(), player.getDirection());
        double d0 = vec3.x();
        double d1 = vec3.y();
        double d2 = vec3.z();
        poseStack.pushPose();
        poseStack.translate((double)pos.getX() - d0, (double)pos.getY() - d1, (double)pos.getZ() - d2);
        var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        LevelRenderer.renderLineBox(event.getPoseStack(),bufferSource.getBuffer(RenderType.LINES),ForgeEvents.multiblockBound,1,1,1,1);
        poseStack.popPose();
    }
}

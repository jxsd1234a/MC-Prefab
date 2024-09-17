package com.wuest.prefab.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wuest.prefab.Prefab;
import com.wuest.prefab.proxy.ClientProxy;
import com.wuest.prefab.structures.render.StructureRenderHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugRenderer.class)
public class RenderIndicatorMixin {
    @Inject(method = "render", at = @At(value = "TAIL"))
    public void renderWorldLast(PoseStack posestack, MultiBufferSource.BufferSource vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        Minecraft prefabIndicatorMinecraft = Minecraft.getInstance();

        if (prefabIndicatorMinecraft.player != null && (!prefabIndicatorMinecraft.player.isCrouching())) {

            StructureRenderHandler.renderClickedBlock(prefabIndicatorMinecraft.level, posestack, cameraX, cameraY, cameraZ);

            VertexConsumer prefabBuffer = vertexConsumers.getBuffer(ClientProxy.PREVIEW_LAYER);

            StructureRenderHandler.newRenderPlayerLook(prefabIndicatorMinecraft.player, posestack, prefabBuffer, cameraX, cameraY, cameraZ);
        }

        // It there are structure scanners; run the rendering for them now.
        if (Prefab.proxy.structureScanners != null && Prefab.proxy.structureScanners.size() != 0) {
            StructureRenderHandler.renderScanningBoxes(posestack, cameraX, cameraY, cameraZ);
        }
    }
}

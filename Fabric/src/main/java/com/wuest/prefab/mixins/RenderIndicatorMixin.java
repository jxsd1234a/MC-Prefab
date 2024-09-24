package com.wuest.prefab.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.prefab.ClientModRegistryBase;
import com.prefab.PrefabClientBase;
import com.wuest.prefab.structures.render.StructureRenderHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugRenderer.class)
public class RenderIndicatorMixin {
    @Inject(method = "render", at = @At(value = "TAIL"))
    public void renderWorldLast(PoseStack matrices, MultiBufferSource.BufferSource vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        Minecraft prefabIndicatorMinecraft = Minecraft.getInstance();

        if (prefabIndicatorMinecraft.player != null && (!prefabIndicatorMinecraft.player.isCrouching())) {
            StructureRenderHandler.RenderTest(prefabIndicatorMinecraft.level, matrices, vertexConsumers, (float)cameraX, (float)cameraY, (float)cameraZ);

            VertexConsumer prefabBuffer = vertexConsumers.getBuffer(PrefabClientBase.PREVIEW_LAYER);

            StructureRenderHandler.newRenderPlayerLook(prefabIndicatorMinecraft.player, matrices, prefabBuffer, cameraX, cameraY, cameraZ);
        }

        // It there are structure scanners; run the rendering for them now.
        if (ClientModRegistryBase.structureScanners != null && !ClientModRegistryBase.structureScanners.isEmpty()) {
            StructureRenderHandler.renderScanningBoxes(matrices, vertexConsumers, (float)cameraX, (float)cameraY, (float)cameraZ);
        }
    }
}

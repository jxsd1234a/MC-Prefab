package com.prefab.mixins;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.prefab.ClientModRegistryBase;
import com.prefab.PrefabClientBase;
import com.prefab.structures.render.StructureRenderHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugRenderer.class)
public class RenderIndicatorMixin {
    @Unique
    private MultiBufferSource.BufferSource previewBufferSource = MultiBufferSource.immediate(new ByteBufferBuilder(PrefabClientBase.PREVIEW_LAYER.bufferSize()));

    @Inject(method = "render", at = @At(value = "TAIL"))
    public void renderWorldLast(PoseStack matrices, MultiBufferSource.BufferSource vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        Minecraft prefabIndicatorMinecraft = Minecraft.getInstance();

        if (prefabIndicatorMinecraft.player != null && (!prefabIndicatorMinecraft.player.isCrouching())) {
            StructureRenderHandler.RenderTest(prefabIndicatorMinecraft.level, matrices, vertexConsumers, (float)cameraX, (float)cameraY, (float)cameraZ);

            VertexConsumer prefabBuffer = this.previewBufferSource.getBuffer(PrefabClientBase.PREVIEW_LAYER);

            StructureRenderHandler.newRenderPlayerLook(prefabIndicatorMinecraft.player, matrices, prefabBuffer, cameraX, cameraY, cameraZ);

            previewBufferSource.endBatch(PrefabClientBase.PREVIEW_LAYER);
        }

        // It there are structure scanners; run the rendering for them now.
        if (ClientModRegistryBase.structureScanners != null && !ClientModRegistryBase.structureScanners.isEmpty()) {
            StructureRenderHandler.renderScanningBoxes(matrices, vertexConsumers, (float)cameraX, (float)cameraY, (float)cameraZ);
        }
    }
}

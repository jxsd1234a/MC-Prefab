package com.wuest.prefab.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wuest.prefab.structures.render.StructureRenderHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class ClientWorldMixin {
    @Inject(method = "renderLevel", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/GameRenderer;renderHand:Z"))
    public void renderWorldLast(float p_109090_, long p_109091_, CallbackInfo ci) {
        Minecraft prefabMinecraft = Minecraft.getInstance();

        if (prefabMinecraft.player != null && (!prefabMinecraft.player.isCrouching())) {
            ////StructureRenderHandler.renderPlayerLook(prefabMinecraft.player, prefabMinecraft.hitResult, matrix, ci);
        }
    }
}

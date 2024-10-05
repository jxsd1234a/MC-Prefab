package com.prefab.structures.render;

import com.mojang.blaze3d.vertex.*;
import com.mojang.text2speech.Narrator;
import com.prefab.ClientModRegistryBase;
import com.prefab.PrefabBase;
import com.prefab.Triple;
import com.prefab.blocks.BlockStructureScanner;
import com.prefab.config.StructureScannerConfig;
import com.prefab.gui.GuiLangKeys;
import com.prefab.structures.base.BuildBlock;
import com.prefab.structures.base.Structure;
import com.prefab.structures.config.StructureConfiguration;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author WuestMan
 * This class was inspired from Botania's AstrolabePreviewHandler.
 * <a href="http://botaniamod.net/license.php">...</a>
 */
@SuppressWarnings({"WeakerAccess", "ConstantConditions"})
public class StructureRenderHandler {
    // player's overlapping on structures and other things.
    public static StructureConfiguration currentConfiguration;
    public static Structure currentStructure;
    public static boolean showedMessage = false;
    private static int dimension;
    private static HashMap<Integer, Integer> stateColor;
    private static HashMap<Integer, Triple<Float, Float, Float>> colorRGB;
    private static Minecraft mcInstance;
    private static final Direction[] DIRECTIONS = Direction.values();
    private static HashMap<Integer, ArrayList<List<BakedQuad>>> blockModelQuads;

    /**
     * Resets the structure to show in the world.
     *
     * @param structure     The structure to show in the world, pass null to clear out the client.
     * @param configuration The configuration for this structure.
     */
    public static void setStructure(Structure structure, StructureConfiguration configuration) {
        StructureRenderHandler.currentStructure = structure;
        StructureRenderHandler.currentConfiguration = configuration;
        StructureRenderHandler.showedMessage = false;
        StructureRenderHandler.stateColor = new HashMap<>(40000, 1);
        StructureRenderHandler.colorRGB = new HashMap<>(40000, 1);
        StructureRenderHandler.blockModelQuads = new HashMap<>(40000, 1);

        StructureRenderHandler.mcInstance = Minecraft.getInstance();

        if (StructureRenderHandler.mcInstance.level != null) {
            StructureRenderHandler.dimension = StructureRenderHandler.mcInstance.level.dimensionType().logicalHeight();
        }
    }

    public static void RenderTest(Level worldIn, PoseStack matrixStack, MultiBufferSource multiBufferSource, float cameraX, float cameraY, float cameraZ) {
        if (StructureRenderHandler.currentStructure != null
                && StructureRenderHandler.dimension == Minecraft.getInstance().player.level().dimensionType().logicalHeight()
                && StructureRenderHandler.currentConfiguration != null
                && PrefabBase.serverConfiguration.enableStructurePreview) {
            BlockPos originalPos = StructureRenderHandler.currentConfiguration.pos.above();

            float blockXOffset = originalPos.getX();
            float blockZOffset = originalPos.getZ();
            float blockStartYOffset = originalPos.getY();

            StructureRenderHandler.drawBox(
                    matrixStack,
                    multiBufferSource,
                    blockXOffset,
                    blockZOffset,
                    blockStartYOffset,
                    cameraX,
                    cameraY,
                    cameraZ,
                    1,
                    1,
                    1);
        }
    }

    public static void drawBox(
            PoseStack matrixStack,
            MultiBufferSource multiBufferSource,
            float blockXOffset,
            float blockZOffset,
            float blockStartYOffset,
            float cameraX,
            float cameraY,
            float cameraZ,
            int xLength,
            int zLength,
            int height) {

        Matrix4f matrix4f = matrixStack.last().pose();

        float translatedX = blockXOffset - cameraX;
        float translatedY = (float) (blockStartYOffset - cameraY + .02);
        float translatedYEnd = (float) (translatedY + height - .02);
        float translatedZ = blockZOffset - cameraZ;

        // Draw the verticals of the box.
        VertexConsumer bufferBuilder = multiBufferSource.getBuffer(RenderType.debugLineStrip(2.0));
        bufferBuilder.addVertex(matrix4f, translatedX, translatedY, translatedZ).setColor(1.0F, 1.0F, 0.0F, 1.0F);
        bufferBuilder.addVertex(matrix4f, translatedX, translatedYEnd, translatedZ).setColor(1.0F, 1.0F, 0.0F, 1.0F);

        bufferBuilder = multiBufferSource.getBuffer(RenderType.debugLineStrip(2.0));
        bufferBuilder.addVertex(matrix4f, translatedX + xLength, translatedY, translatedZ).setColor(1.0F, 1.0F, 0.0F, 1.0F);
        bufferBuilder.addVertex(matrix4f, translatedX + xLength, translatedYEnd, translatedZ).setColor(1.0F, 1.0F, 0.0F, 1.0F);

        bufferBuilder = multiBufferSource.getBuffer(RenderType.debugLineStrip(2.0));
        bufferBuilder.addVertex(matrix4f, translatedX, translatedY, translatedZ + zLength).setColor(1.0F, 1.0F, 0.0F, 1.0F);
        bufferBuilder.addVertex(matrix4f, translatedX, translatedYEnd, translatedZ + zLength).setColor(1.0F, 1.0F, 0.0F, 1.0F);

        bufferBuilder = multiBufferSource.getBuffer(RenderType.debugLineStrip(2.0));
        bufferBuilder.addVertex(matrix4f, translatedX + xLength, translatedY, translatedZ + zLength).setColor(1.0F, 1.0F, 0.0F, 1.0F);
        bufferBuilder.addVertex(matrix4f, translatedX + xLength, translatedYEnd, translatedZ + zLength).setColor(1.0F, 1.0F, 0.0F, 1.0F);

        // Draw bottom horizontals.
        bufferBuilder = multiBufferSource.getBuffer(RenderType.debugLineStrip(2.0));

        bufferBuilder.addVertex(matrix4f, translatedX, translatedY, translatedZ).setColor(1.0F, 1.0F, 0.0F, 1.0F);
        bufferBuilder.addVertex(matrix4f, translatedX, translatedY, translatedZ + zLength).setColor(1.0F, 1.0F, 0.0F, 1.0F);

        bufferBuilder.addVertex(matrix4f, translatedX + xLength, translatedY, translatedZ + zLength).setColor(1.0F, 1.0F, 0.0F, 1.0F);
        bufferBuilder.addVertex(matrix4f, translatedX + xLength, translatedY, translatedZ).setColor(1.0F, 1.0F, 0.0F, 1.0F);

        bufferBuilder.addVertex(matrix4f, translatedX, translatedY, translatedZ).setColor(1.0F, 1.0F, 0.0F, 1.0F);
        bufferBuilder.addVertex(matrix4f, translatedX + xLength, translatedY, translatedZ).setColor(1.0F, 1.0F, 0.0F, 1.0F);

        bufferBuilder.addVertex(matrix4f, translatedX + xLength, translatedY, translatedZ + zLength).setColor(1.0F, 1.0F, 0.0F, 1.0F);
        bufferBuilder.addVertex(matrix4f, translatedX, translatedY, translatedZ + zLength).setColor(1.0F, 1.0F, 0.0F, 1.0F);

        // Draw top horizontals
        bufferBuilder = multiBufferSource.getBuffer(RenderType.debugLineStrip(2.0));

        bufferBuilder.addVertex(matrix4f, translatedX, translatedYEnd, translatedZ).setColor(1.0F, 1.0F, 0.0F, 1.0F);
        bufferBuilder.addVertex(matrix4f, translatedX, translatedYEnd, translatedZ + zLength).setColor(1.0F, 1.0F, 0.0F, 1.0F);

        bufferBuilder.addVertex(matrix4f, translatedX + xLength, translatedYEnd, translatedZ + zLength).setColor(1.0F, 1.0F, 0.0F, 1.0F);
        bufferBuilder.addVertex(matrix4f, translatedX + xLength, translatedYEnd, translatedZ).setColor(1.0F, 1.0F, 0.0F, 1.0F);

        bufferBuilder.addVertex(matrix4f, translatedX, translatedYEnd, translatedZ).setColor(1.0F, 1.0F, 0.0F, 1.0F);
        bufferBuilder.addVertex(matrix4f, translatedX + xLength, translatedYEnd, translatedZ).setColor(1.0F, 1.0F, 0.0F, 1.0F);

        bufferBuilder.addVertex(matrix4f, translatedX + xLength, translatedYEnd, translatedZ + zLength).setColor(1.0F, 1.0F, 0.0F, 1.0F);
        bufferBuilder.addVertex(matrix4f, translatedX, translatedYEnd, translatedZ + zLength).setColor(1.0F, 1.0F, 0.0F, 1.0F);
    }

    public static void renderScanningBoxes(PoseStack matrixStack,
                                           MultiBufferSource multiBufferSource,
                                           float cameraX,
                                           float cameraY,
                                           float cameraZ) {
        for (int i = 0; i < ClientModRegistryBase.structureScanners.size(); i++) {
            StructureScannerConfig config = ClientModRegistryBase.structureScanners.get(i);

            BlockPos pos = config.blockPos;
            boolean removeConfig = pos == null;

            // Make sure the block exists in the world at the block pos.
            if (pos != null) {
                removeConfig = !(Minecraft.getInstance().level.getBlockState(pos.relative(Direction.UP)).getBlock() instanceof BlockStructureScanner);
            }

            if (removeConfig) {
                ClientModRegistryBase.structureScanners.remove(i);
                i--;
                continue;
            }

            Direction leftDirection = config.direction.getCounterClockWise();

            BlockPos startingPosition = config.blockPos
                    .relative(leftDirection, config.blocksToTheLeft)
                    .relative(Direction.DOWN, config.blocksDown)
                    .relative(config.direction, config.blocksParallel);

            int xLength = config.blocksWide;
            int zLength = config.blocksLong;

            // Based on direction, width and length may be need to be modified;

            switch (config.direction) {
                case NORTH: {
                    zLength = -zLength;
                    startingPosition = startingPosition.relative(config.direction.getOpposite());
                    break;
                }

                case EAST: {
                    int tempWidth = xLength;
                    xLength = zLength;
                    zLength = tempWidth;
                    break;
                }

                case SOUTH: {
                    xLength = -xLength;
                    startingPosition = startingPosition.relative(config.direction.getCounterClockWise());
                    break;
                }

                case WEST: {
                    int tempLength = zLength;
                    zLength = -xLength;
                    xLength = -tempLength;

                    startingPosition = startingPosition.relative(config.direction.getOpposite());
                    startingPosition = startingPosition.relative(config.direction.getCounterClockWise());
                    break;
                }
            }

            StructureRenderHandler.drawBox(
                    matrixStack,
                    multiBufferSource,
                    startingPosition.getX(),
                    startingPosition.getZ(),
                    startingPosition.getY(),
                    cameraX,
                    cameraY,
                    cameraZ,
                    xLength,
                    zLength,
                    config.blocksTall);
        }
    }

    public static void basicBlockRenderExample(Player player, PoseStack poseStack, BufferBuilder buffer, double cameraX, double cameraY, double cameraZ) {
        if (StructureRenderHandler.currentStructure != null
                && StructureRenderHandler.dimension == player.level().dimensionType().logicalHeight()
                && StructureRenderHandler.currentConfiguration != null
                && PrefabBase.serverConfiguration.enableStructurePreview) {

            Level world = player.level();

            try {
                BlockState state = Blocks.REDSTONE_BLOCK.defaultBlockState();
                BlockPos blockPos = StructureRenderHandler.currentConfiguration.pos.relative(Direction.SOUTH, 2).above(2);
                BlockRenderDispatcher brd = StructureRenderHandler.mcInstance.getBlockRenderer();
                BakedModel blockModel = brd.getBlockModel(state);
                ModelBlockRenderer modelBlockRenderer = brd.getModelRenderer();
                int color = StructureRenderHandler.mcInstance.getBlockColors().getColor(state, null, null, 0);
                float r = (float) (color >> 16 & 255) / 255.0F;
                float g = (float) (color >> 8 & 255) / 255.0F;
                float b = (float) (color & 255) / 255.0F;

                // Translate the pose properly...maybe
                PoseStack.Pose originalPose = poseStack.poseStack.peekLast();
                float scaleValue = blockPos.getY() + 1.3F;
                PoseStack.Pose lastPose = new PoseStack.Pose(originalPose);

                lastPose.pose().translate((float) -cameraX, (float) -cameraY, (float) -cameraZ);
                lastPose.pose().translate(blockPos.getX(), blockPos.getY(), blockPos.getZ());

                StructureRenderHandler.renderModel(lastPose, buffer, state, blockModel, r, g, b, 0xF000F0, OverlayTexture.NO_OVERLAY, state.hashCode());
            } catch (Exception ex) {
                PrefabBase.logger.error(ex);
            }
        }
    }

    public static void newRenderPlayerLook(Player player, PoseStack poseStack, VertexConsumer buffer, double cameraX, double cameraY, double cameraZ) {
        if (StructureRenderHandler.currentStructure != null
                && StructureRenderHandler.dimension == player.level().dimensionType().logicalHeight()
                && StructureRenderHandler.currentConfiguration != null
                && PrefabBase.serverConfiguration.enableStructurePreview) {

            Level world = player.level();

            Camera camera = StructureRenderHandler.mcInstance.getEntityRenderDispatcher().camera;
            double renderPosX = camera.getPosition().x();
            double renderPosY = camera.getPosition().y();
            double renderPosZ = camera.getPosition().z();

            BlockRenderDispatcher brd = StructureRenderHandler.mcInstance.getBlockRenderer();

            Vec3 cameraPosition = new Vec3(cameraX, cameraY, cameraZ);
            Direction playerViewDirection = player.getNearestViewDirection();
            Vec3 playerViewVector = player.getViewVector(1.0F);

            ArrayList<BuildBlock> buildBlocks = StructureRenderHandler.currentStructure.getBlocks();

            PoseStack.Pose originalPose = poseStack.poseStack.peekLast();
            try {
                for (BuildBlock buildBlock : buildBlocks) {
                    StructureRenderHandler.processBuildBlockForRendering(buildBlock, world, player, cameraPosition, playerViewDirection, playerViewVector, originalPose,
                            renderPosX, renderPosY, renderPosZ, brd, buffer);
                }

                if (!StructureRenderHandler.showedMessage) {
                    Minecraft mc = Minecraft.getInstance();

                    // Stop narrator from continuing narrating what was in the structure GUI
                    Narrator.getNarrator().clear();

                    MutableComponent message = Component.translatable(GuiLangKeys.GUI_PREVIEW_NOTICE);
                    message.setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));

                    mc.gui.getChat().addMessage(message);

                    message = Component.translatable(GuiLangKeys.GUI_BLOCK_CLICKED);
                    message.setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
                    mc.gui.getChat().addMessage(message);

                    StructureRenderHandler.showedMessage = true;
                }
            } catch (Exception ex) {
                PrefabBase.logger.error(ex);
            }
        }
    }

    private static void processBuildBlockForRendering(
            BuildBlock buildBlock,
            Level world,
            Player player,
            Vec3 cameraPosition,
            Direction playerViewDirection,
            Vec3 playerViewVector,
            PoseStack.Pose originalPose,
            double renderPosX,
            double renderPosY,
            double renderPosZ,
            BlockRenderDispatcher brd,
            VertexConsumer buffer) {
        Block foundBlock = buildBlock.getBlockState() != null ? buildBlock.getBlockState().getBlock() : BuiltInRegistries.BLOCK.get(buildBlock.getResourceLocation());

        if (foundBlock != null) {
            // In order to get the proper relative position I also need the structure's original facing.
            if (buildBlock.blockPos == null) {
                buildBlock.blockPos = buildBlock.getStartingPosition().getRelativePosition(
                        StructureRenderHandler.currentConfiguration.pos,
                        StructureRenderHandler.currentStructure.getClearSpace().getShape().getDirection(),
                        StructureRenderHandler.currentConfiguration.houseFacing);
            }

            BlockPos buildBlockPos = buildBlock.blockPos;

            // Don't render this block if it's going to overlay a non-air/water block.
            BlockState targetBlock = world.getBlockState(buildBlockPos);

            if (targetBlock.getBlock() != Blocks.AIR && targetBlock.getBlock() != Blocks.WATER) {
                return;
            }

            if (buildBlock.centerOfBlock == null) {
                buildBlock.centerOfBlock = Vec3.atCenterOf(buildBlockPos);
            }

            Vec3 vectorBetweenPlayerAndBlock = new Vec3(
                    buildBlockPos.getX() - player.getX(),
                    buildBlockPos.getY() - player.getEyeY(),
                    buildBlockPos.getZ() - player.getZ());

            vectorBetweenPlayerAndBlock.normalize();

            BlockHitResult hitResult = Shapes.block().clip(cameraPosition, buildBlock.centerOfBlock, buildBlockPos);

            // Note: The hit direction is in reference to the Block's point of view, not the player.
            if (hitResult == null || (hitResult.getDirection() != Direction.UP && hitResult.getDirection() != Direction.DOWN
                    && hitResult.getDirection() == playerViewDirection)) {
                // Never hit the block in the first place, or it's behind them so continue.
                return;
            }

            // Calculate the "line" between the block and the player's view.
            // This is the same way that Ender men determine if a player is looking at them.
            // This avoids using "Frustum" as it's finicky and prone to change with Minecraft's rendering changes.
            double lineBetweenPlayerViewBlock = playerViewVector.normalize().dot(vectorBetweenPlayerAndBlock);
            double result = 1.0 - 0.025 / vectorBetweenPlayerAndBlock.length();
            boolean boolCheck = lineBetweenPlayerViewBlock > result;

            if (!boolCheck) {
                return;
            }

            if (buildBlock.getBlockState() == null) {
                // Get the unique block state for this block.
                BlockState blockState = foundBlock.defaultBlockState();
                buildBlock = BuildBlock.SetBlockState(
                        StructureRenderHandler.currentConfiguration,
                        player.level(),
                        StructureRenderHandler.currentConfiguration.pos,
                        buildBlock,
                        foundBlock,
                        blockState,
                        StructureRenderHandler.currentStructure);
            }

            StructureRenderHandler.renderBlockAt(originalPose, buffer, buildBlock.getBlockState(), buildBlockPos, buildBlock.hashCode(),
                    renderPosX, renderPosY, renderPosZ, brd, world);

            // Render the sub-block if there is any.
            if (buildBlock.getSubBlock() != null) {
                BuildBlock subBuildBlock = buildBlock.getSubBlock();

                Block foundSubBlock = subBuildBlock.getBlockState() != null ? subBuildBlock.getBlockState().getBlock() : BuiltInRegistries.BLOCK.get(subBuildBlock.getResourceLocation());

                if (subBuildBlock.getBlockState() == null) {
                    BlockState subBlockState = foundSubBlock.defaultBlockState();

                    subBuildBlock = BuildBlock.SetBlockState(
                            StructureRenderHandler.currentConfiguration,
                            player.level(),
                            StructureRenderHandler.currentConfiguration.pos,
                            buildBlock.getSubBlock(),
                            foundSubBlock,
                            subBlockState,
                            StructureRenderHandler.currentStructure);
                }

                if (subBuildBlock.blockPos == null) {
                    subBuildBlock.blockPos = subBuildBlock.getStartingPosition().getRelativePosition(
                            StructureRenderHandler.currentConfiguration.pos,
                            StructureRenderHandler.currentStructure.getClearSpace().getShape().getDirection(),
                            StructureRenderHandler.currentConfiguration.houseFacing);
                }

                StructureRenderHandler.renderBlockAt(originalPose, buffer, subBuildBlock.getBlockState(), subBuildBlock.blockPos, subBuildBlock.hashCode(),
                        renderPosX, renderPosY, renderPosZ, brd, world);
            }
        }
    }

    private static void renderBlockAt(PoseStack.Pose originalPose, VertexConsumer buffer, BlockState state, BlockPos pos, int buildBlockHash,
                                      double renderPosX, double renderPosY, double renderPosZ, BlockRenderDispatcher brd, Level level) {
        if (state.getRenderShape() != RenderShape.INVISIBLE && state.getRenderShape() == RenderShape.MODEL) {
            PoseStack.Pose lastPose = new PoseStack.Pose(originalPose);
            lastPose.pose().translate((float) -renderPosX, (float) -renderPosY, (float) -renderPosZ);
            lastPose.pose().translate(pos.getX(), pos.getY(), pos.getZ());

            // Get these values out of the saved hashmaps if possible.
            int color;
            Triple<Float, Float, Float> colorRGB;

            ModelBlockRenderer modelBlockRenderer = brd.getModelRenderer();

            // Don't get this out of a hashmap or anything like that, it's already in one.
            BakedModel model = brd.getBlockModel(state);

            int blockStateHash = state.hashCode();
            color = StructureRenderHandler.stateColor.computeIfAbsent(blockStateHash, x -> StructureRenderHandler.mcInstance.getBlockColors().getColor(state, null, null, 0));
            colorRGB = StructureRenderHandler.colorRGB.computeIfAbsent(blockStateHash, x -> {
                float r = (float) (color >> 16 & 255) / 255.0F;
                float g = (float) (color >> 8 & 255) / 255.0F;
                float b = (float) (color & 255) / 255.0F;

                return new Triple<>(r, g, b);
            });

            // Always use entity translucent layer so blending is turned on
            try {
                // Use our own rendering method because it's faster and allocates way less memory.
                StructureRenderHandler.renderModel(lastPose, buffer, state, model, colorRGB.getFirst(), colorRGB.getSecond(), colorRGB.getThird(), 0xF000F0, OverlayTexture.NO_OVERLAY, blockStateHash);
            } catch (Exception ex) {
                System.out.println("System Exception: " + ex.getMessage());
            }
        }
    }

    public static void renderModel(PoseStack.Pose pose, VertexConsumer vertexConsumer, @Nullable BlockState blockState, BakedModel bakedModel, float f, float g, float h, int i, int j, int blockStateHash) {
        RandomSource randomSource = RandomSource.create();
        long l = 42L;

        if (!StructureRenderHandler.blockModelQuads.containsKey(blockStateHash)) {
            // Render the quads like normal then add them to the hash set for the next pass.
            ArrayList<List<BakedQuad>> bakedQuads = new ArrayList<>();

            for (Direction direction : DIRECTIONS) {
                randomSource.setSeed(42L);

                List<BakedQuad> quadList = bakedModel.getQuads(blockState, direction, randomSource);
                StructureRenderHandler.renderQuadList(pose, vertexConsumer, f, g, h, quadList, i, j);
                bakedQuads.add(quadList);
            }

            randomSource.setSeed(42L);

            List<BakedQuad> quadList = bakedModel.getQuads(blockState, null, randomSource);
            StructureRenderHandler.renderQuadList(pose, vertexConsumer, f, g, h, quadList, i, j);

            bakedQuads.add(quadList);
            StructureRenderHandler.blockModelQuads.put(blockStateHash, bakedQuads);

            return;
        }

        // Render the cached baked quads.
        ArrayList<List<BakedQuad>> bakedQuads = StructureRenderHandler.blockModelQuads.get(blockStateHash);

        for (List<BakedQuad> quadList : bakedQuads) {
            StructureRenderHandler.renderQuadList(pose, vertexConsumer, f, g, h, quadList, i, j);
        }
    }

    private static void renderQuadList(PoseStack.Pose pose, VertexConsumer vertexConsumer, float f, float g, float h, List<BakedQuad> list, int i, int j) {
        BakedQuad bakedQuad;
        float k;
        float l;
        float m;

        for (BakedQuad quad : list) {
            if (quad.isTinted()) {
                k = Mth.clamp(f, 0.0F, 1.0F);
                l = Mth.clamp(g, 0.0F, 1.0F);
                m = Mth.clamp(h, 0.0F, 1.0F);
            } else {
                k = 1.0F;
                l = 1.0F;
                m = 1.0F;
            }

            StructureRenderHandler.putBulkData(vertexConsumer, pose, quad, k, l, m, i, j);
        }
    }

    private static void putBulkData(VertexConsumer vertexConsumer, PoseStack.Pose pose, BakedQuad bakedQuad, float k, float l, float m, int i, int j) {
        int[] js = bakedQuad.getVertices();
        Vec3i vec3i = bakedQuad.getDirection().getNormal();
        Matrix4f matrix4f = pose.pose();
        Vector3f vector3f = pose.transformNormal((float) vec3i.getX(), (float) vec3i.getY(), (float) vec3i.getZ(), new Vector3f());
        int trimmedLength = js.length / 8;
        int baseColor = (int) (255.0F);

        for (int counter = 0; counter < trimmedLength; ++counter) {
            float betterO = Float.intBitsToFloat(js[counter * 8]);
            float betterP = Float.intBitsToFloat(js[(counter * 8) + 1]);
            float betterQ = Float.intBitsToFloat(js[(counter * 8) + 2]);

            float u = 1.0F * k * 255.0F;
            float v = 1.0F * l * 255.0F;
            float w = 1.0F * m * 255.0F;
            float betterT = Float.intBitsToFloat(js[(counter * 8) + 4]);
            float betterZ = Float.intBitsToFloat(js[(counter * 8) + 5]);

            int x = FastColor.ARGB32.color(baseColor, (int) u, (int) v, (int) w);

            Vector3f vector3f2 = matrix4f.transformPosition(betterO, betterP, betterQ, new Vector3f());
            vertexConsumer.addVertex(vector3f2.x(), vector3f2.y(), vector3f2.z(), x, betterT, betterZ, j, i, vector3f.x(), vector3f.y(), vector3f.z());
        }
    }
}

package com.wuest.prefab.structures.events;

import com.prefab.ModRegistryBase;
import com.prefab.PrefabBase;
import com.prefab.network.ServerToClientTypes;
import com.wuest.prefab.Prefab;
import com.prefab.Tuple;
import com.prefab.config.EntityPlayerConfiguration;
import com.wuest.prefab.config.ModConfiguration;
import com.prefab.network.message.TagMessage;
import com.prefab.structures.base.BuildBlock;
import com.prefab.structures.base.BuildEntity;
import com.prefab.structures.base.BuildingMethods;
import com.prefab.structures.base.Structure;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.*;

/**
 * This is the structure event handler.
 *
 * @author WuestMan
 */
public final class StructureEventHandler {
    /**
     * Contains a hashmap for the structures to build and for whom.
     */
    public static HashMap<Player, ArrayList<Structure>> structuresToBuild = new HashMap<>();

    public static ArrayList<Tuple<Structure, BuildEntity>> entitiesToGenerate = new ArrayList<>();

    public static int ticksSinceLastEntitiesGenerated = 0;

    public static void registerStructureServerSideEvents() {
        StructureEventHandler.playerJoinedServer();

        StructureEventHandler.serverStarted();

        StructureEventHandler.serverStopped();

        StructureEventHandler.serverTick();
    }

    private static void playerJoinedServer() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, serverWorld) -> {
            if (entity instanceof ServerPlayer) {
                StructureEventHandler.playerLoggedIn((ServerPlayer) entity, serverWorld);
            }
        });
    }

    private static void serverTick() {
        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            StructureEventHandler.onServerTick();
        });
    }

    private static void serverStarted() {
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            EntityPlayerConfiguration.playerTagData.clear();
        });
    }

    private static void serverStopped() {
        ServerLifecycleEvents.SERVER_STOPPED.register((server) -> {
            EntityPlayerConfiguration.playerTagData.clear();
        });
    }

    /**
     * This event is used to determine if the player should be given the starting house item when they log in.
     */
    public static void playerLoggedIn(ServerPlayer player, ServerLevel serverWorld) {
        EntityPlayerConfiguration playerConfig = EntityPlayerConfiguration.loadFromEntity(player);

        ModConfiguration.StartingItemOptions startingItem = Prefab.serverConfiguration.startingItem;

        if (!playerConfig.givenHouseBuilder && startingItem != null
            && Prefab.serverConfiguration.newPlayersGetStartingItem) {
            ItemStack stack = ItemStack.EMPTY;

            switch (startingItem) {
                case StartingHouse: {
                    stack = new ItemStack(ModRegistryBase.House);
                    break;
                }

                case ModerateHouse: {
                    stack = new ItemStack(ModRegistryBase.HouseImproved);
                    break;
                }
            }

            if (!stack.isEmpty()) {
                System.out.println(player.getDisplayName().getString() + " joined the game for the first time. Giving them starting item.");

                player.getInventory().add(stack);
                player.containerMenu.broadcastChanges();

                // Make sure to set the tag for this player; so they don't get the item again.
                playerConfig.givenHouseBuilder = true;

                //playerConfig.saveToCache(player);
            }
        }

        // Send the tag to the client.
        TagMessage tagMessage = new TagMessage();
        tagMessage.setMessageTag(playerConfig.createPlayerTag());

        PrefabBase.networkWrapper.sendToClient(ServerToClientTypes.PLAYER_CONFIG_SYNC, player, tagMessage);
    }

    /**
     * This event is primarily used to build 100 blocks for any queued structures for all players.
     */
    public static void onServerTick() {
        ArrayList<Player> playersToRemove = new ArrayList<Player>();

        StructureEventHandler.ticksSinceLastEntitiesGenerated++;

        if (StructureEventHandler.entitiesToGenerate.size() > 0) {
            StructureEventHandler.ticksSinceLastEntitiesGenerated++;

            if (StructureEventHandler.ticksSinceLastEntitiesGenerated > 40) {
                // Process any entities.
                StructureEventHandler.processStructureEntities();

                StructureEventHandler.ticksSinceLastEntitiesGenerated = 0;
            }
        }

        if (StructureEventHandler.structuresToBuild.size() > 0) {
            for (Map.Entry<Player, ArrayList<Structure>> entry : StructureEventHandler.structuresToBuild.entrySet()) {
                ArrayList<Structure> structuresToRemove = new ArrayList<>();

                // Build the first 100 blocks of each structure for this player.
                for (Structure structure : entry.getValue()) {
                    if (!structure.entitiesRemoved) {
                        // Go through each block and find any entities there. If there are any; kill them if they aren't players.
                        // If there is a player there...they will probably die anyways.....
                        for (BlockPos clearedPos : structure.clearedBlockPos) {
                            AABB axisPos = Shapes.block().bounds().move(clearedPos);

                            List<Entity> list = structure.world.getEntities(null, axisPos);

                            if (!list.isEmpty()) {
                                for (Entity entity : list) {
                                    // Don't kill living entities.
                                    if (!(entity instanceof LivingEntity)) {
                                        if (entity instanceof HangingEntity) {
                                            structure.BeforeHangingEntityRemoved((HangingEntity) entity);
                                        }

                                        entity.remove(Entity.RemovalReason.DISCARDED);
                                    }
                                }
                            }
                        }

                        structure.entitiesRemoved = true;
                    }

                    if (structure.airBlocks.size() > 0) {
                        structure.hasAirBlocks = true;
                    }

                    for (int i = 0; i < 100; i++) {
                        i = StructureEventHandler.setBlock(i, structure, structuresToRemove);
                    }

                    // After building the blocks for this tick, find waterlogged blocks and remove them.
                    StructureEventHandler.removeWaterLogging(structure);
                }

                // Update the list of structures to remove this structure since it's done building.
                StructureEventHandler.removeStructuresFromList(structuresToRemove, entry);

                if (entry.getValue().size() == 0) {
                    playersToRemove.add(entry.getKey());
                }
            }
        }

        // Remove each player that has their structure's built.
        for (Player player : playersToRemove) {
            StructureEventHandler.structuresToBuild.remove(player);
        }

    }

    private static int setBlock(int i, Structure structure, ArrayList<Structure> structuresToRemove) {
        // Structure clearing happens before anything else.
        // Don't bother clearing the area for water-based structures
        // Anything which should be air will be air
        if (structure.clearedBlockPos.size() > 0 && !structure.hasAirBlocks) {
            BlockPos currentPos = structure.clearedBlockPos.get(0);
            structure.clearedBlockPos.remove(0);

            BlockState clearBlockState = structure.world.getBlockState(currentPos);

            // If this block is not specifically air then set it to air.
            // This will also break other mod's logic blocks but they would probably be broken due to structure
            // generation anyways.
            if (clearBlockState.getBlock() != Blocks.AIR) {
                structure.BeforeClearSpaceBlockReplaced(currentPos);

                for (Direction adjacentBlock : Direction.values()) {
                    BlockPos tempPos = currentPos.relative(adjacentBlock);
                    BlockState foundState = structure.world.getBlockState(tempPos);
                    Block foundBlock = foundState.getBlock();

                    // Check if this block is one that is attached to a facing, if it is, remove it first.
                    if (foundBlock instanceof TorchBlock
                            || foundBlock instanceof SignBlock
                            || foundBlock instanceof LeverBlock
                            || foundBlock instanceof ButtonBlock
                            || foundBlock instanceof BedBlock
                            || foundBlock instanceof CarpetBlock
                            || foundBlock instanceof FlowerPotBlock
                            || foundBlock instanceof SugarCaneBlock
                            || foundBlock instanceof BasePressurePlateBlock
                            || foundBlock instanceof DoorBlock
                            || foundBlock instanceof LadderBlock
                            || foundBlock instanceof VineBlock
                            || foundBlock instanceof RedStoneWireBlock
                            || foundBlock instanceof DiodeBlock
                            || foundBlock instanceof AbstractBannerBlock
                            || foundBlock instanceof LanternBlock
                            || foundBlock instanceof BaseRailBlock) {
                        structure.BeforeClearSpaceBlockReplaced(currentPos);

                        if (!(foundBlock instanceof BedBlock)) {
                            structure.world.removeBlock(tempPos, false);
                        } else if (foundBlock instanceof DoorBlock) {
                            // Make sure to remove both parts before going on.
                            DoubleBlockHalf currentHalf = foundState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);

                            BlockPos otherHalfPos = currentHalf == DoubleBlockHalf.LOWER
                                    ? tempPos.above() : tempPos.below();

                            structure.world.setBlock(tempPos, Blocks.AIR.defaultBlockState(), 35);
                            structure.world.setBlock(otherHalfPos, Blocks.AIR.defaultBlockState(), 35);

                        } else {
                            // Found a bed, try to find the other part of the bed and remove it.
                            for (Direction currentDirection : Direction.values()) {
                                BlockPos bedPos = tempPos.relative(currentDirection);
                                BlockState bedState = structure.world.getBlockState(bedPos);

                                if (bedState.getBlock() instanceof BedBlock) {
                                    // found the other part of the bed. Remove the current block and this one.
                                    structure.world.setBlock(tempPos, Blocks.AIR.defaultBlockState(), 35);
                                    structure.world.setBlock(bedPos, Blocks.AIR.defaultBlockState(), 35);
                                    break;
                                }
                            }
                        }
                    }
                }

                structure.world.removeBlock(currentPos, false);
            } else {
                // This is just an air block, move onto the next block don't need to wait for the next tick.
                i--;
            }

            return i;
        }

        BuildBlock currentBlock = null;

        if (structure.priorityOneBlocks.size() > 0) {
            currentBlock = structure.priorityOneBlocks.get(0);
            structure.priorityOneBlocks.remove(0);
        } else {
            // There are no more blocks to set.
            structuresToRemove.add(structure);
            return 999;
        }

        BlockState state = currentBlock.getBlockState();

        BlockPos setBlockPos = currentBlock.getStartingPosition().getRelativePosition(structure.originalPos,
                structure.getClearSpace().getShape().getDirection(), structure.configuration.houseFacing);

        BuildingMethods.ReplaceBlock(structure.world, setBlockPos, state, 2);

        // After placing the initial block, set the sub-block. This needs to happen as the list isn't always in the
        // correct order.
        if (currentBlock.getSubBlock() != null) {
            BuildBlock subBlock = currentBlock.getSubBlock();

            BuildingMethods.ReplaceBlock(structure.world, subBlock.getStartingPosition().getRelativePosition(structure.originalPos,
                    structure.getClearSpace().getShape().getDirection(), structure.configuration.houseFacing), subBlock.getBlockState());
        }

        return i;
    }

    private static void removeStructuresFromList(ArrayList<Structure> structuresToRemove, Map.Entry<Player, ArrayList<Structure>> entry) {
        for (Structure structure : structuresToRemove) {
            StructureEventHandler.removeWaterLogging(structure);

            for (BuildEntity buildEntity : structure.entities) {
                Optional<EntityType<?>> entityType = EntityType.byString(buildEntity.getEntityResourceString());

                if (entityType.isPresent()) {
                    StructureEventHandler.entitiesToGenerate.add(new Tuple<>(structure, buildEntity));
                }
            }

            // This structure is done building. Do any post-building operations.
            entry.getValue().remove(structure);
        }
    }

    private static void processStructureEntities() {
        for (Tuple<Structure, BuildEntity> entityRecords : StructureEventHandler.entitiesToGenerate) {
            BuildEntity buildEntity = entityRecords.second;
            Structure structure = entityRecords.first;

            Optional<EntityType<?>> entityType = EntityType.byString(buildEntity.getEntityResourceString());

            if (entityType.isPresent()) {
                Entity entity = entityType.get().create(structure.world);

                if (entity != null) {
                    CompoundTag tagCompound = buildEntity.getEntityDataTag();
                    BlockPos entityPos = buildEntity.getStartingPosition().getRelativePosition(structure.originalPos,
                            structure.getClearSpace().getShape().getDirection(), structure.configuration.houseFacing);

                    if (tagCompound != null) {
                        if (tagCompound.hasUUID("UUID")) {
                            tagCompound.putUUID("UUID", UUID.randomUUID());
                        }

                        tagCompound = StructureEventHandler.updateTagDueToVersionUpdate(entity, tagCompound);

                        ListTag nbttaglist = new ListTag();
                        nbttaglist.add(DoubleTag.valueOf(entityPos.getX()));
                        nbttaglist.add(DoubleTag.valueOf(entityPos.getY()));
                        nbttaglist.add(DoubleTag.valueOf(entityPos.getZ()));
                        tagCompound.put("Pos", nbttaglist);

                        entity.load(tagCompound);
                    }

                    // Set item frame facing and rotation here.
                    if (entity instanceof ItemFrame) {
                        entity = StructureEventHandler.setItemFrameFacingAndRotation((ItemFrame) entity, buildEntity, entityPos, structure);
                    } else if (entity instanceof Painting) {
                        entity = StructureEventHandler.setPaintingFacingAndRotation((Painting) entity, buildEntity, entityPos, structure);
                    } else if (entity instanceof AbstractMinecart) {
                        // Minecarts need to be slightly higher to account for the rails; otherwise they will fall through the rail and the block below the rail.
                        buildEntity.entityYAxisOffset = buildEntity.entityYAxisOffset + .2;
                        entity = StructureEventHandler.setEntityFacingAndRotation(entity, buildEntity, entityPos, structure);
                    } else {
                        // All other entities
                        entity = StructureEventHandler.setEntityFacingAndRotation(entity, buildEntity, entityPos, structure);
                    }

                    structure.world.addFreshEntity(entity);
                }
            }
        }

        // All entities generated; clear out the list.
        StructureEventHandler.entitiesToGenerate.clear();
    }

    private static void removeWaterLogging(Structure structure) {
        if (structure.hasAirBlocks) {
            for (BlockPos currentPos : structure.allBlockPositions) {
                BlockState currentState = structure.world.getBlockState(currentPos);

                if (currentState.hasProperty(BlockStateProperties.WATERLOGGED)) {
                    // This is a water loggable block and there were air blocks, make sure that it's no longer water logged.
                    currentState = currentState.setValue((BlockStateProperties.WATERLOGGED), false);
                    structure.world.setBlock(currentPos, currentState, 3);
                } else if (currentState.getBlock() == Blocks.WATER) {
                    structure.world.setBlock(currentPos, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
    }

    private static Entity setPaintingFacingAndRotation(Painting entity, BuildEntity buildEntity, BlockPos entityPos, Structure structure) {
        float yaw = entity.getYRot();
        Rotation rotation = Rotation.NONE;
        double x_axis_offset = 0;
        double z_axis_offset = 0;
        Direction facing = entity.getDirection();
        double y_axis_offset = buildEntity.entityYAxisOffset * -1;

        Direction structureDirection = structure.getClearSpace().getShape().getDirection();
        Direction configurationDirection = structure.configuration.houseFacing.getOpposite();

        if (configurationDirection == structureDirection.getOpposite()) {
            rotation = Rotation.CLOCKWISE_180;
            facing = facing.getOpposite();
        } else if (configurationDirection == structureDirection.getClockWise()) {
            rotation = Rotation.CLOCKWISE_90;
            facing = facing.getClockWise();
        } else if (configurationDirection == structureDirection.getCounterClockWise()) {
            rotation = Rotation.COUNTERCLOCKWISE_90;
            facing = facing.getCounterClockWise();
        }

        int paintingBlockWidth = (int) (entity.getBbWidth() / 16);
        int paintingBlockHeight = (int) (entity.getBbHeight() / 16);

        if ((paintingBlockHeight > paintingBlockWidth || paintingBlockHeight > 1)
                && !(paintingBlockWidth == 4 && paintingBlockHeight == 3)) {
            y_axis_offset--;
        }

        yaw = entity.rotate(rotation);

        CompoundTag compound = new CompoundTag();
        ((HangingEntity) entity).addAdditionalSaveData(compound);
        compound.putByte("facing", (byte) facing.get2DDataValue());
        ((HangingEntity) entity).readAdditionalSaveData(compound);
        StructureEventHandler.updateEntityHangingBoundingBox(entity);

        entity.moveTo(entityPos.getX() + x_axis_offset, entityPos.getY() + y_axis_offset, entityPos.getZ() + z_axis_offset, yaw,
                entity.getXRot());

        StructureEventHandler.updateEntityHangingBoundingBox(entity);
        ChunkAccess chunk = structure.world.getChunkAt(entityPos);

        chunk.setUnsaved(true);

        return entity;
    }

    private static Entity setItemFrameFacingAndRotation(ItemFrame frame, BuildEntity buildEntity, BlockPos entityPos, Structure structure) {
        float yaw = frame.getYRot();
        Rotation rotation = Rotation.NONE;
        double x_axis_offset = buildEntity.entityXAxisOffset;
        double z_axis_offset = buildEntity.entityZAxisOffset;
        Direction facing = frame.getDirection();
        double y_axis_offset = buildEntity.entityYAxisOffset;
        x_axis_offset = x_axis_offset * -1;
        z_axis_offset = z_axis_offset * -1;

        Direction structureDirection = structure.getClearSpace().getShape().getDirection();
        Direction configurationDirection = structure.configuration.houseFacing.getOpposite();

        if (facing != Direction.UP && facing != Direction.DOWN) {
            if (configurationDirection == structureDirection.getOpposite()) {
                rotation = Rotation.CLOCKWISE_180;
                facing = facing.getOpposite();
            } else if (configurationDirection == structureDirection.getClockWise()) {
                rotation = Rotation.CLOCKWISE_90;
                facing = facing.getClockWise();
            } else if (configurationDirection == structureDirection.getCounterClockWise()) {
                rotation = Rotation.COUNTERCLOCKWISE_90;
                facing = facing.getCounterClockWise();
            } else {
                x_axis_offset = 0;
                z_axis_offset = 0;
            }
        }

        yaw = frame.rotate(rotation);

        CompoundTag compound = new CompoundTag();
        ((HangingEntity) frame).addAdditionalSaveData(compound);
        compound.putByte("Facing", (byte) facing.get3DDataValue());
        ((HangingEntity) frame).readAdditionalSaveData(compound);
        StructureEventHandler.updateEntityHangingBoundingBox(frame);

        frame.moveTo(entityPos.getX() + x_axis_offset, entityPos.getY() + y_axis_offset, entityPos.getZ() + z_axis_offset, yaw,
                frame.getXRot());

        StructureEventHandler.updateEntityHangingBoundingBox(frame);
        ChunkAccess chunk = structure.world.getChunkAt(entityPos);

        chunk.setUnsaved(true);

        return frame;
    }

    private static Entity setEntityFacingAndRotation(Entity entity, BuildEntity buildEntity, BlockPos entityPos, Structure structure) {
        float yaw = entity.getYRot();
        Rotation rotation = Rotation.NONE;
        double x_axis_offset = buildEntity.entityXAxisOffset;
        double z_axis_offset = buildEntity.entityZAxisOffset;
        Direction facing = structure.getClearSpace().getShape().getDirection();
        double y_axis_offset = buildEntity.entityYAxisOffset;
        Direction configurationDirection = structure.configuration.houseFacing.getOpposite();

        if (configurationDirection == facing.getOpposite()) {
            rotation = Rotation.CLOCKWISE_180;
            x_axis_offset = x_axis_offset * -1;
            z_axis_offset = z_axis_offset * -1;
        } else if (configurationDirection == facing.getClockWise()) {
            rotation = Rotation.CLOCKWISE_90;
            x_axis_offset = x_axis_offset * -1;
            z_axis_offset = z_axis_offset * -1;
        } else if (configurationDirection == facing.getCounterClockWise()) {
            rotation = Rotation.COUNTERCLOCKWISE_90;
            x_axis_offset = x_axis_offset * -1;
            z_axis_offset = z_axis_offset * -1;
        } else {
            x_axis_offset = 0;
            z_axis_offset = 0;
        }

        yaw = entity.rotate(rotation);

        entity.moveTo(entityPos.getX() + x_axis_offset, entityPos.getY() + y_axis_offset, entityPos.getZ() + z_axis_offset, yaw,
                entity.getXRot());

        return entity;
    }

    private static void updateEntityHangingBoundingBox(HangingEntity entity) {
        double d0 = (double) entity.getPos().getX() + 0.5D;
        double d1 = (double) entity.getPos().getY() + 0.5D;
        double d2 = (double) entity.getPos().getZ() + 0.5D;
        double d3 = 0.46875D;
        double d4 = entity.getBbWidth() % 32 == 0 ? 0.5D : 0.0D;
        double d5 = entity.getBbHeight() % 32 == 0 ? 0.5D : 0.0D;
        Direction horizontal = entity.getDirection();
        d0 = d0 - (double) horizontal.getStepX() * 0.46875D;
        d2 = d2 - (double) horizontal.getStepZ() * 0.46875D;
        d1 = d1 + d5;
        Direction direction = horizontal == Direction.DOWN || horizontal == Direction.UP ? horizontal.getOpposite() : horizontal.getCounterClockWise();
        d0 = d0 + d4 * (double) direction.getStepX();
        d2 = d2 + d4 * (double) direction.getStepZ();

        // The function call below set the following fields from the "entity" class. posX, posY, posZ.
        // This will probably have to change when the mappings get updated.
        entity.setPosRaw(d0, d1, d2);
        double d6 = entity.getBbWidth();
        double d7 = entity.getBbHeight();
        double d8 = entity.getBbWidth();

        if (horizontal.getAxis() == Direction.Axis.Z) {
            d8 = 1.0D;
        } else {
            d6 = 1.0D;
        }

        d6 = d6 / 32.0D;
        d7 = d7 / 32.0D;
        d8 = d8 / 32.0D;
        entity.setBoundingBox(new AABB(d0 - d6, d1 - d7, d2 - d8, d0 + d6, d1 + d7, d2 + d8));
    }

    private static CompoundTag updateTagDueToVersionUpdate(Entity entity, CompoundTag compoundTag) {
        if (entity instanceof Painting) {
            // In MC 1.19 some tags changed so convert them now.
            if (compoundTag.contains("Facing")) {
                byte facingByte = compoundTag.getByte("Facing");
                compoundTag.putByte("facing", facingByte);
            }

            if (compoundTag.contains("Motive")) {
                String motiveData = compoundTag.getString("Motive");
                compoundTag.putString("variant", motiveData);
            }
        }

        return compoundTag;
    }
}

package com.prefab;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public interface IEventCaller {
    boolean canBreakBlock(ServerLevel world, Player player, BlockState blockState, BlockPos blockPos);
}

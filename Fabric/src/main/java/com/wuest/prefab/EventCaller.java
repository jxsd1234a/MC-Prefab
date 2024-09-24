package com.wuest.prefab;

import com.prefab.IEventCaller;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public class EventCaller implements IEventCaller {

    @Override
    public boolean canBreakBlock(ServerLevel world, Player player, BlockState blockState, BlockPos blockPos) {
        return PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(world, player, blockPos, blockState, null);
    }
}

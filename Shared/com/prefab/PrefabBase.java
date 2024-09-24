package com.prefab;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class PrefabBase {
	/**
	 * This is the ModID
	 */
	public static final String MODID = "prefab";

	public static Logger logger;
	/**
	 * This is used to determine if the mod is currently being debugged.
	 */
	public static boolean isDebug = false;

	/**
	 * Simulates an air block that blocks movement and cannot be moved.
	 * Basically a GLASS block, but NOT glass.
	 */
	public static final Supplier<BlockBehaviour.Properties> SeeThroughImmovable = ()->BlockBehaviour.Properties.of().noOcclusion().isViewBlocking(Blocks::never).pushReaction(PushReaction.IGNORE).sound(SoundType.STONE);

	static {
		PrefabBase.logger = LogManager.getLogger("Prefab");
		PrefabBase.isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().contains("-agentlib:jdwp");
	}

}
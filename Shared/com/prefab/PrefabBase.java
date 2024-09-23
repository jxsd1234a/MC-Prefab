package com.prefab;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

	static {
		PrefabBase.logger = LogManager.getLogger("Prefab");
		PrefabBase.isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().contains("-agentlib:jdwp");
	}

}
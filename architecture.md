# Architecture

This repository is broken down into separate mod-loader folders with the majority of the code split off into a separate `Shared` folder.

To make changes for a specific mod-loader, open the appropriate sub-folder with IntelliJ and the project will load like any normal. There will be two main source sets, the one in the mod-loader folder and the one in the `Shared` folder.

## Package naming conventions

All packages (I.E. folders) in the `Shared` folder must be in the src -> com -> prefab folder.

All packages in the mod-loader folders must be in the `mod-loader folder` -> src -> main -> java -> com -> prefab -> `mod-loader-name` folder.

**Example**: Fabric/src/main/java/com/prefab/fabric/Prefab.java

While it may not make ense to have the mod-loader name twice in the folder name, it makes sense for the package name so we can avoid class name collisions. This allows us to have a `BlockBoundary.java` in the `Shared` blocks directory and the same named file in the `Fabric` and `NeoForge` folders.

## Shared folder rules

This `Shared` folder is intended to be mod-loader agnostic and should _never_ reference a mod-loader class/interface/variable/enum/object and should only reference it's own constructs, base Java, general dependent library, or core Minecraft constructs.

This avoids us having to duplicate code all over the place when the difference between the various mod-loaders are minimal.

If we need to use a mod-loader specific method in an otherwise generic process, it is best to create an interface which is then implemented in the mod-loader projects as `Mod-Loader Events`

### Example of Mod-Loader Events

This specific example uses the `IEventCaller` interface (which is something we created for this mod) to call call specific mod-loader events when needed. In the single case we started with, it was to determine if the current player can break the current block when placing a structure, before actually placing the structure.

This was necessary as each mod-loader had specific logic built-in for this type of event so it was convienent to use.

#### Shared Folder Code

IEventCaller

```java
package com.prefab;

public interface IEventCaller {
    boolean canBreakBlock(ServerLevel world, Player player, BlockState blockState, BlockPos blockPos);
}
```

PrefabBase

```java

package com.prefab;

public class PrefabBase {
  // Must of the class has been snipped for example purposes....

  /**
    * Used to call mod-loader specific events/methods (such as whether or not a player can break a block)
  */
  public static IEventCaller eventCaller;
}

```

BuildingMethods

```java

package com.prefab.structures.base;
public class BuildingMethods {
  // Must of the class has been snipped for example purposes....

  public static Triple<Boolean, BlockState, BlockPos> CheckBuildSpaceForAllowedBlockReplacement(ServerLevel world, BlockPos startBlockPos, BlockPos endBlockPos, Player player) {
    if (!world.isClientSide) {
      // Check each block in the space to be cleared if it's protected from
      // breaking or placing, if it is return false.
      for (BlockPos currentPos : BlockPos.betweenClosed(startBlockPos, endBlockPos)) {
        BlockState blockState = world.getBlockState(currentPos);

        // First check to see if this is a spawn protected block.
        if (world.getServer().isUnderSpawnProtection(world, currentPos, player)) {
          // This block is protected by vanilla spawn protection. Don't allow building here.
          return new Triple<>(false, blockState, currentPos);
        }

        if (!world.isEmptyBlock(currentPos)) {
          /*

            The line below this block comment is where we call the custom event caller


          */
          if (!PrefabBase.eventCaller.canBreakBlock(world, player, world.getBlockState(currentPos), currentPos)) {
            return new Triple<>(false, blockState, currentPos);
          }
        }

        // A hardness of less than 0 is unbreakable.
        if (blockState.getDestroySpeed(world, currentPos) < 0.0f) {
          // This is bedrock or some other type of unbreakable block. Don't allow this block to be broken by a structure.
          return new Triple<>(false, blockState, currentPos);
        }
      }
    }

    return new Triple<>(true, null, null);
  }
}

```

#### Mod-Loader Code

This example show the `Fabric` implementation, but the NeoForge implementation is very similar.

Prefab

```java
package com.prefab.fabric;

public class Prefab implements ModInitializer {
    // Must of the class has been snipped for example purposes....

    @Override
    public void onInitialize() {
      // Must of the method has been snipped for example purposes....

      // This is where we set the mod-loader specific implementation of the IEventCaller.
      PrefabBase.eventCaller = new EventCaller();
    }
}

```

EventCaller

```java
package com.prefab.fabric;

public class EventCaller implements IEventCaller {

    @Override
    public boolean canBreakBlock(ServerLevel world, Player player, BlockState blockState, BlockPos blockPos) {
        return PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(world, player, blockPos, blockState, null);
    }
}
```

## Github Actions

There are some github actions implemented which will automatically build any commits to Main as well as some specific sub-branches. These actions will also be triggered on Pull Requests.

These actions will build both `Fabric` and `NeoForge` mod-loader projects to ensure that any changes will not adversely affect the other.

package net.livingcivilizations.command;

import com.mojang.brigadier.CommandDispatcher;
import net.livingcivilizations.LivingCivilization;
import net.livingcivilizations.entity.HumanEntity;
import net.livingcivilizations.entity.HumanEntityTypes;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public final class LivingCivilizationCommands {
	private static final int MAX_SPAWN_SEARCH_DEPTH = 32;
	/** Same edge inset BlockCollisions uses before it expands the cursor by one block. */
	private static final double COLLISION_CURSOR_EPSILON = 1.0E-7D;

	private LivingCivilizationCommands() {
	}

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> registerCommands(dispatcher));
	}

	static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("livingciv")
				.requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
				.then(Commands.literal("spawn")
						.then(Commands.literal("human").executes(context -> spawnHuman(context.getSource())))));
	}

	static boolean supportsSpawnFloor(boolean sturdyTop, boolean taggedSand) {
		return sturdyTop || taggedSand;
	}

	private static boolean columnInValidBounds(ServerLevel level, BlockPos floorPos, BlockPos spawnPos, BlockPos headPos) {
		return level.isInValidBounds(floorPos)
				&& level.isInValidBounds(spawnPos)
				&& level.isInValidBounds(headPos);
	}

	/**
	 * Loaded-only check for every column in the BlockCollisions cursor.
	 * {@code isLoaded} rejects invalid bounds and does not add a chunk ticket.
	 * The 26.3 rectangle helpers on {@code LevelReader} are deprecated.
	 */
	private static boolean collisionFootprintLoaded(ServerLevel level, BlockPos spawnPos, AABB box) {
		int minBlockX = Mth.floor(box.minX - COLLISION_CURSOR_EPSILON) - 1;
		int maxBlockX = Mth.floor(box.maxX + COLLISION_CURSOR_EPSILON) + 1;
		int minBlockZ = Mth.floor(box.minZ - COLLISION_CURSOR_EPSILON) - 1;
		int maxBlockZ = Mth.floor(box.maxZ + COLLISION_CURSOR_EPSILON) + 1;
		int y = spawnPos.getY();
		for (int x = minBlockX; x <= maxBlockX; x++) {
			for (int z = minBlockZ; z <= maxBlockZ; z++) {
				if (!level.isLoaded(new BlockPos(x, y, z))) {
					return false;
				}
			}
		}
		return true;
	}

	private static int spawnHuman(CommandSourceStack source) {
		ServerPlayer player = source.getPlayer();
		if (player == null) {
			source.sendFailure(Component.literal("This command must be run by a player."));
			return 0;
		}

		ServerLevel level = player.level();
		BlockPos origin = player.blockPosition();
		List<Direction> directions = List.of(
				player.getDirection(),
				player.getDirection().getClockWise(),
				player.getDirection().getOpposite(),
				player.getDirection().getCounterClockWise()
		);

		for (Direction direction : directions) {
			for (int distance = 1; distance <= 2; distance++) {
				for (int drop = 0; drop <= MAX_SPAWN_SEARCH_DEPTH; drop++) {
					BlockPos spawnPos = origin.relative(direction, distance).below(drop);
					BlockPos floorPos = spawnPos.below();
					BlockPos headPos = spawnPos.above();
					if (!level.getWorldBorder().isWithinBounds(spawnPos)
							|| !columnInValidBounds(level, floorPos, spawnPos, headPos)) {
						continue;
					}

					HumanEntity human = HumanEntityTypes.human().create(level, EntitySpawnReason.COMMAND);
					if (human == null) {
						continue;
					}
					human.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
					human.setYRot(player.getYRot());
					human.setXRot(0.0F);
					if (!collisionFootprintLoaded(level, spawnPos, human.getBoundingBox())) {
						continue;
					}

					BlockState floor = level.getBlockState(floorPos);
					if (!supportsSpawnFloor(floor.isFaceSturdy(level, floorPos, Direction.UP), floor.is(BlockTags.SAND))
							|| !level.getBlockState(spawnPos).getCollisionShape(level, spawnPos).isEmpty()
							|| !level.getBlockState(headPos).getCollisionShape(level, headPos).isEmpty()) {
						continue;
					}

					if (!level.noCollision(human) || !level.addFreshEntity(human)) {
						continue;
					}

					source.sendSuccess(() -> Component.literal("Spawned a human NPC."), true);
					LivingCivilization.debug("entity", "Spawned human entity at " + spawnPos);
					return 1;
				}
			}
		}

		source.sendFailure(Component.literal("No safe nearby position was found; no blocks were changed."));
		return 0;
	}
}
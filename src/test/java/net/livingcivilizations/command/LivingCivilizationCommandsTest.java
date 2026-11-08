package net.livingcivilizations.command;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.livingcivilizations.MinecraftTestBootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class LivingCivilizationCommandsTest {
	@BeforeAll
	static void bootstrapMinecraft() {
		MinecraftTestBootstrap.initialize();
	}

	@Test
	void registersTheHumanSpawnCommandPath() {
		CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
		LivingCivilizationCommands.registerCommands(dispatcher);

		assertNotNull(dispatcher.getRoot().getChild("livingciv"));
		assertNotNull(dispatcher.getRoot().getChild("livingciv").getChild("spawn"));
		assertNotNull(dispatcher.getRoot().getChild("livingciv").getChild("spawn").getChild("human"));
	}

	@Test
	void spawnFloorPolicyAcceptsSturdyAndTaggedSandButRejectsUnsupportedBlocks() {
		assertTrue(LivingCivilizationCommands.supportsSpawnFloor(true, false));
		assertTrue(LivingCivilizationCommands.supportsSpawnFloor(false, true));
		assertFalse(LivingCivilizationCommands.supportsSpawnFloor(false, false));
	}
}
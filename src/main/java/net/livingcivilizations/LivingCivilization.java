package net.livingcivilizations;

import net.livingcivilizations.command.LivingCivilizationCommands;
import net.livingcivilizations.entity.HumanEntityTypes;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LivingCivilization implements ModInitializer {
	public static final String MOD_ID = "livingcivilizations";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static final boolean DEBUG_DIAGNOSTICS_ENABLED = Boolean.getBoolean("livingcivilizations.debug")
			|| "true".equalsIgnoreCase(System.getenv("LIVING_CIVILIZATIONS_DEBUG"));

	@Override
	public void onInitialize() {
		LOGGER.info("Living Civilization initialized (debug diagnostics: {})", DEBUG_DIAGNOSTICS_ENABLED);
		HumanEntityTypes.initialize();
		LivingCivilizationCommands.register();
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	public static void debug(String component, String message) {
		if (DEBUG_DIAGNOSTICS_ENABLED) {
			LOGGER.info("[{}] {}", component, message);
		}
	}

	public static void error(String component, String message, Throwable cause) {
		LOGGER.error("[{}] {}", component, message, cause);
	}
}
package net.livingcivilizations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.livingcivilizations.entity.HumanEntity;
import net.livingcivilizations.entity.HumanEntityTypes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

class LivingCivilizationTest {
	@BeforeAll
	static void bootstrapMinecraft() {
		MinecraftTestBootstrap.initialize();
	}

	@Test
	void humanEntityUsesStableRegistryIdentifier() {
		assertEquals("livingcivilizations:human", HumanEntityTypes.HUMAN_ID.toString());
		assertEquals(HumanEntityTypes.HUMAN_ID, HumanEntityTypes.HUMAN_KEY.identifier());
	}

	@Test
	void humanEntityDefinesRequiredAttributes() {
		AttributeSupplier attributes = HumanEntity.createAttributes().build();

		assertEquals(20.0, attributes.getBaseValue(Attributes.MAX_HEALTH));
		assertEquals(0.1, attributes.getBaseValue(Attributes.MOVEMENT_SPEED), 1.0E-6);
		assertEquals(16.0, attributes.getBaseValue(Attributes.FOLLOW_RANGE));
	}

	@Test
	void fabricMetadataKeepsCommonAndClientEntrypointsSeparate() throws Exception {
		try (InputStream input = getClass().getResourceAsStream("/fabric.mod.json")) {
			assertNotNull(input);
			JsonObject metadata = JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8)).getAsJsonObject();
			assertEquals("livingcivilizations", metadata.get("id").getAsString());
			assertEquals("Living Civilization", metadata.get("name").getAsString());
			assertEquals("net.livingcivilizations.LivingCivilization", metadata.getAsJsonObject("entrypoints")
					.getAsJsonArray("main").get(0).getAsString());
			assertEquals("net.livingcivilizations.client.LivingCivilizationClient", metadata.getAsJsonObject("entrypoints")
					.getAsJsonArray("client").get(0).getAsString());
		}
	}
}
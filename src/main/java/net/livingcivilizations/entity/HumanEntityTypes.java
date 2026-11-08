package net.livingcivilizations.entity;

import net.livingcivilizations.LivingCivilization;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class HumanEntityTypes {
	public static final Identifier HUMAN_ID = LivingCivilization.id("human");
	public static final ResourceKey<EntityType<?>> HUMAN_KEY = ResourceKey.create(Registries.ENTITY_TYPE, HUMAN_ID);

	private static EntityType<HumanEntity> human;

	private HumanEntityTypes() {
	}

	public static void initialize() {
		if (human != null) {
			return;
		}

		EntityType.Builder<HumanEntity> builder = EntityType.Builder.<HumanEntity>of(HumanEntity::new, MobCategory.CREATURE)
				.sized(0.6F, 1.8F)
				.clientTrackingRange(8)
				.updateInterval(3);

		human = Registry.register(BuiltInRegistries.ENTITY_TYPE, HUMAN_KEY, builder.build(HUMAN_KEY));
		FabricDefaultAttributeRegistry.register(human, HumanEntity.createAttributes());
		LivingCivilization.LOGGER.info("Registered human entity type {}", HUMAN_ID);
	}

	public static EntityType<HumanEntity> human() {
		if (human == null) {
			throw new IllegalStateException("Human entity type has not been initialized");
		}
		return human;
	}
}
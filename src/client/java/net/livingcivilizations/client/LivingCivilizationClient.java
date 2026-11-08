package net.livingcivilizations.client;

import net.livingcivilizations.entity.HumanEntityTypes;
import net.livingcivilizations.client.entity.HumanEntityModel;
import net.livingcivilizations.client.entity.HumanEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.renderer.entity.EntityRenderers;

public final class LivingCivilizationClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModelLayerRegistry.registerModelLayer(HumanEntityModel.LAYER, HumanEntityModel::createLayer);
		EntityRenderers.register(HumanEntityTypes.human(), HumanEntityRenderer::new);
	}
}
package net.livingcivilizations.client.entity;

import net.livingcivilizations.LivingCivilization;
import net.livingcivilizations.entity.HumanEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;

public final class HumanEntityRenderer extends MobRenderer<HumanEntity, HumanoidRenderState, HumanEntityModel> {
	private static final Identifier TEXTURE = LivingCivilization.id("textures/entity/human.png");

	public HumanEntityRenderer(EntityRendererProvider.Context context) {
		super(context, new HumanEntityModel(context.bakeLayer(HumanEntityModel.LAYER)), 0.4F);
	}

	@Override
	public HumanoidRenderState createRenderState() {
		return new HumanoidRenderState();
	}

	@Override
	public Identifier getTextureLocation(HumanoidRenderState state) {
		return TEXTURE;
	}
}
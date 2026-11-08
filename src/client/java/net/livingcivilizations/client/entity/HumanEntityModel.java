package net.livingcivilizations.client.entity;

import net.livingcivilizations.LivingCivilization;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;

public final class HumanEntityModel extends HumanoidModel<HumanoidRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(LivingCivilization.id("human"), "main");

	public HumanEntityModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition createLayer() {
		return LayerDefinition.create(HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F), 64, 64);
	}
}
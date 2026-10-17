package com.seojoon.carbontech.client.render;

import com.seojoon.carbontech.CarbonTech;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

/**
 * 카본 방패 3D 렌더러.
 *
 * 바닐라 방패는 BuiltinModelItemRenderer가 Items.SHIELD일 때만 3D 모델을 그린다.
 * 우리 방패는 Fabric의 BuiltinItemRendererRegistry에 이 렌더러를 등록해서,
 * 바닐라와 같은 방패 모델(ShieldEntityModel)에 우리 텍스처만 입혀 그린다.
 * 텍스처는 블록 아틀라스에 들어가야 하므로 assets/carbontech/atlases/blocks.json에 등록되어 있다.
 */
public final class CarbonShieldRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {

	private static final SpriteIdentifier TEXTURE =
			new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, CarbonTech.id("entity/carbon_shield"));

	private ShieldEntityModel model;
	private boolean logged = false;

	private ShieldEntityModel model() {
		if (model == null) {
			model = new ShieldEntityModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(EntityModelLayers.SHIELD));
		}
		return model;
	}

	@Override
	public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices,
	                   VertexConsumerProvider vertexConsumers, int light, int overlay) {
		ShieldEntityModel shield = model();
		if (!logged) {
			logged = true;
			CarbonTech.LOGGER.debug("카본 방패 스프라이트 = {} (missingno면 아틀라스 등록 실패)",
					TEXTURE.getSprite().getContents().getId());
		}
		matrices.push();
		matrices.scale(1.0f, -1.0f, -1.0f); // 바닐라와 동일: 엔티티 모델 좌표계 → 아이템 좌표계
		VertexConsumer consumer = TEXTURE.getSprite().getTextureSpecificVertexConsumer(
				ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, shield.getLayer(TEXTURE.getAtlasId()), true, stack.hasGlint()));
		shield.getHandle().render(matrices, consumer, light, overlay);
		shield.getPlate().render(matrices, consumer, light, overlay);
		matrices.pop();
	}
}

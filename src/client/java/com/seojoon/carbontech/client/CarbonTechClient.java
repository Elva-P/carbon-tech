package com.seojoon.carbontech.client;

import com.seojoon.carbontech.client.render.CarbonShieldRenderer;
import com.seojoon.carbontech.item.CarbonBowItem;
import com.seojoon.carbontech.item.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;

/**
 * 클라이언트 전용 진입점. 렌더링과 입력처럼 서버에는 없는 것들을 여기서 등록한다.
 */
public class CarbonTechClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// 카본 방패 3D 렌더러
		BuiltinItemRendererRegistry.INSTANCE.register(ModItems.CARBON_SHIELD, new CarbonShieldRenderer());

		// "blocking" 모델 프레디킷: 막는 중이면 1 → item/shield 모델의 오버라이드가 막기 자세 모델로 바꿈
		ModelPredicateProviderRegistry.register(ModItems.CARBON_SHIELD, Identifier.ofVanilla("blocking"),
				(stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0f : 0.0f);

		// 카본 활 당김 애니메이션: 바닐라는 Items.BOW에만 등록되어 있어 우리 활에 따로 등록한다.
		// pull = 당김 정도(0~1). 바닐라 공식(사용 틱/20)에 카본 활의 배율을 곱해 발사 타이밍과 그림을 일치시킨다.
		ModelPredicateProviderRegistry.register(ModItems.CARBON_BOW, Identifier.ofVanilla("pull"),
				(stack, world, entity, seed) -> entity == null || entity.getActiveItem() != stack ? 0.0f
						: (stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft()) * CarbonBowItem.DRAW_SPEED / 20.0f);
		ModelPredicateProviderRegistry.register(ModItems.CARBON_BOW, Identifier.ofVanilla("pulling"),
				(stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0f : 0.0f);

		// 고탄성 부츠 2단 점프 입력 감지
		DoubleJumpClient.register();
	}
}

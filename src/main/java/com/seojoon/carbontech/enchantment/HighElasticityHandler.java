package com.seojoon.carbontech.enchantment;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * 고탄성(High Elasticity) 인챈트의 부위별 효과 중 이벤트로 처리하는 것들.
 *
 *   레깅스: 신속 I 상시          → 서버 틱마다 짧은 상태효과를 계속 갱신
 *   부츠:   점프 강화 I 상시     → 위와 같음
 *   투구:   위에서 떨어지는 충격 공격(메이스 강타) 무효 → 데미지 허용 이벤트에서 취소
 *
 * 부츠의 낙하 피해 50% 감소와 갑옷의 넉백 80% 감소는 LivingEntityMixin에 있다.
 */
public final class HighElasticityHandler {
	private HighElasticityHandler() {}

	/** 몇 틱마다 상태효과를 갱신할지. 20틱 = 1초 */
	private static final int REFRESH_INTERVAL = 10;
	/** 갱신 간격보다 넉넉하게 줘야 효과가 깜빡이지 않는다 */
	private static final int EFFECT_DURATION = 40;

	/** 메이스 강타 데미지 타입. 상수 이름 대신 ID로 비교해 버전 변화에 덜 민감하게 */
	private static final RegistryKey<DamageType> MACE_SMASH =
			RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("mace_smash"));

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTicks() % REFRESH_INTERVAL != 0) return;
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				if (ModEnchantments.level(player, EquipmentSlot.LEGS, ModEnchantments.HIGH_ELASTICITY) > 0) {
					apply(player, StatusEffects.SPEED);
				}
				if (ModEnchantments.level(player, EquipmentSlot.FEET, ModEnchantments.HIGH_ELASTICITY) > 0) {
					apply(player, StatusEffects.JUMP_BOOST);
				}
			}
		});

		// 투구: 메이스 강타 무효화 (데미지 자체를 차단하므로 넉백도 없음)
		ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
			if (!source.isOf(MACE_SMASH)) return true;
			return ModEnchantments.level(entity, EquipmentSlot.HEAD, ModEnchantments.HIGH_ELASTICITY) <= 0;
		});
	}

	/** 레벨 I(amplifier 0), ambient=true(비컨처럼 은은한 표시), 파티클 없음, 아이콘 표시 */
	private static void apply(ServerPlayerEntity player, net.minecraft.registry.entry.RegistryEntry<net.minecraft.entity.effect.StatusEffect> effect) {
		StatusEffectInstance current = player.getStatusEffect(effect);
		// 이미 더 높은 레벨(물약 등)이 걸려 있으면 건드리지 않는다
		if (current != null && current.getAmplifier() > 0) return;
		player.addStatusEffect(new StatusEffectInstance(effect, EFFECT_DURATION, 0, true, false, true));
	}
}

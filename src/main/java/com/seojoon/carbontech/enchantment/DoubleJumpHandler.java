package com.seojoon.carbontech.enchantment;

import com.seojoon.carbontech.network.DoubleJumpPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

/**
 * 고탄성 부츠의 2단 점프 — 서버 쪽.
 *
 * 실제 점프(속도 변경)는 클라이언트가 즉시 처리해 반응이 빠르고,
 * 서버는 패킷을 받아 (1) 정말 고탄성 부츠를 신었는지 검증, (2) 낙하 거리 초기화, (3) 효과음/파티클을 담당한다.
 * 클라이언트 쪽 입력 감지는 src/client의 DoubleJumpClient에 있다.
 */
public final class DoubleJumpHandler {
	private DoubleJumpHandler() {}

	public static void register() {
		// 패킷 타입 등록 (클라이언트→서버 방향). 양쪽 모두 이 등록이 있어야 한다.
		PayloadTypeRegistry.playC2S().register(DoubleJumpPayload.ID, DoubleJumpPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(DoubleJumpPayload.ID, (payload, context) -> {
			ServerPlayerEntity player = context.player();
			if (ModEnchantments.level(player, EquipmentSlot.FEET, ModEnchantments.HIGH_ELASTICITY) <= 0) return;

			player.fallDistance = 0.0f;

			ServerWorld world = player.getServerWorld();
			world.playSound(null, player.getX(), player.getY(), player.getZ(),
					SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.PLAYERS, 0.5f, 1.6f);
			world.spawnParticles(ParticleTypes.CLOUD,
					player.getX(), player.getY(), player.getZ(),
					8, 0.2, 0.05, 0.2, 0.02);
		});
	}
}

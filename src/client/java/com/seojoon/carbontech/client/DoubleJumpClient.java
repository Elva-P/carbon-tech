package com.seojoon.carbontech.client;

import com.seojoon.carbontech.enchantment.ModEnchantments;
import com.seojoon.carbontech.network.DoubleJumpPayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;

/**
 * 고탄성 부츠의 2단 점프 — 클라이언트 쪽.
 *
 * 매 틱 점프 키를 감시해서 "공중에서, 키를 새로 눌렀고, 아직 2단 점프를 안 썼으면" 한 번 더 뛴다.
 * 땅에 닿거나 물/사다리에 닿으면 다시 사용 가능해진다.
 */
public final class DoubleJumpClient {
	private DoubleJumpClient() {}

	private static boolean jumpKeyWasPressed = false;
	private static boolean doubleJumpUsed = false;

	public static void register() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			ClientPlayerEntity player = client.player;
			if (player == null) return;

			boolean pressed = client.options.jumpKey.isPressed();

			// 땅/물/사다리에 닿으면 충전
			if (player.isOnGround() || player.isTouchingWater() || player.isInLava() || player.isClimbing()) {
				doubleJumpUsed = false;
			} else if (pressed && !jumpKeyWasPressed && !doubleJumpUsed && canDoubleJump(player)) {
				doubleJumpUsed = true;
				player.jump();              // 바닐라 점프 계산 그대로 (점프 강화 효과도 반영됨)
				player.fallDistance = 0.0f; // 클라이언트 쪽 낙하 거리도 초기화
				ClientPlayNetworking.send(new DoubleJumpPayload());
			}

			jumpKeyWasPressed = pressed;
		});
	}

	private static boolean canDoubleJump(ClientPlayerEntity player) {
		if (player.getAbilities().flying || player.isFallFlying() || player.hasVehicle() || player.isSpectator()) return false;
		return ModEnchantments.level(player, EquipmentSlot.FEET, ModEnchantments.HIGH_ELASTICITY) > 0;
	}
}

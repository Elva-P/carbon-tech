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
 * 규칙: "땅에서 점프로 떠오른 뒤" 공중에서 점프 키를 다시 누르면 한 번 더 뛴다.
 * 절벽에서 그냥 떨어진 경우에는 발동하지 않는다. 땅/물/사다리에 닿으면 초기화된다.
 *
 * 구분 방법: 매 틱 "땅에 있었는가"를 기억해 두고, 땅 → 공중으로 바뀐 순간에
 * 점프 키가 눌려 있었으면 점프, 아니면 낙하로 본다.
 */
public final class DoubleJumpClient {
	private DoubleJumpClient() {}

	private static boolean jumpKeyWasPressed = false;
	private static boolean wasOnGround = true;
	/** 이번 체공이 점프로 시작됐는가 (낙하면 false) */
	private static boolean airborneByJump = false;
	private static boolean doubleJumpUsed = false;

	public static void register() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			ClientPlayerEntity player = client.player;
			if (player == null) return;

			boolean pressed = client.options.jumpKey.isPressed();
			boolean grounded = player.isOnGround() || player.isTouchingWater() || player.isInLava() || player.isClimbing();

			if (grounded) {
				// 땅에 닿으면 충전. 다음 체공이 점프인지 낙하인지는 떠나는 순간 결정된다.
				doubleJumpUsed = false;
				airborneByJump = false;
			} else if (wasOnGround) {
				// 땅 → 공중 전환 순간: 점프 키가 눌려 있었으면 점프, 아니면 낙하
				airborneByJump = pressed || jumpKeyWasPressed;
			} else if (pressed && !jumpKeyWasPressed && airborneByJump && !doubleJumpUsed && canDoubleJump(player)) {
				doubleJumpUsed = true;
				player.jump();              // 바닐라 점프 계산 그대로 (점프 강화 효과도 반영됨)
				player.fallDistance = 0.0f; // 클라이언트 쪽 낙하 거리도 초기화
				ClientPlayNetworking.send(new DoubleJumpPayload());
			}

			wasOnGround = grounded;
			jumpKeyWasPressed = pressed;
		});
	}

	private static boolean canDoubleJump(ClientPlayerEntity player) {
		if (player.getAbilities().flying || player.isFallFlying() || player.hasVehicle() || player.isSpectator()) return false;
		return ModEnchantments.level(player, EquipmentSlot.FEET, ModEnchantments.HIGH_ELASTICITY) > 0;
	}
}

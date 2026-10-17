package com.seojoon.carbontech.network;

import com.seojoon.carbontech.CarbonTech;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/**
 * 클라이언트 → 서버: "나 지금 2단 점프했어" 알림.
 *
 * 점프 입력은 클라이언트만 알고, 낙하 거리 초기화와 효과음/파티클은 서버가 해야 하므로
 * 둘 사이에 이 작은 패킷이 오간다. 내용(필드)이 없는 빈 패킷이라 코덱은 unit이다.
 */
public record DoubleJumpPayload() implements CustomPayload {
	public static final Id<DoubleJumpPayload> ID = new Id<>(CarbonTech.id("double_jump"));
	public static final PacketCodec<RegistryByteBuf, DoubleJumpPayload> CODEC = PacketCodec.unit(new DoubleJumpPayload());

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}

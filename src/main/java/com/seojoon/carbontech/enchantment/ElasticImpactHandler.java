package com.seojoon.carbontech.enchantment;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * 탄성 충격(Elastic Impact) 인챈트: 블록 하나를 캐면 주변 블록까지 연쇄 파괴.
 *
 *   I   관통 채굴: 캔 블록 바로 뒤 1칸
 *   II  1x2 범위: 관통 + 위(수평 채굴 시) → 서서 걸을 수 있는 통로
 *   III 3x3 범위: 바라보는 면 기준 3x3 평면 (8칸)
 *
 * 규칙: 도구가 캘 수 있는 블록만(곡괭이는 돌/광석, 삽은 흙/모래), 웅크리면(Shift) 단일 채굴.
 * 추가 블록은 tryBreakBlock으로 "정상 채굴"하므로 드롭/행운/실크터치/내구도가 전부 바닐라대로 적용된다.
 */
public final class ElasticImpactHandler {
	private ElasticImpactHandler() {}

	/** tryBreakBlock이 다시 AFTER 이벤트를 부르므로, 연쇄 중임을 표시해 무한 재귀를 막는다 */
	private static final ThreadLocal<Boolean> CHAINING = ThreadLocal.withInitial(() -> false);

	public static void register() {
		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
			if (world.isClient() || CHAINING.get()) return;
			if (!(player instanceof ServerPlayerEntity serverPlayer)) return;
			if (serverPlayer.isSneaking()) return;

			ItemStack tool = serverPlayer.getMainHandStack();
			int level = ModEnchantments.level(world, ModEnchantments.ELASTIC_IMPACT, tool);
			if (level <= 0) return;

			// 플레이어가 블록의 어느 면을 보고 캤는지 알아낸다
			HitResult hit = serverPlayer.raycast(6.0, 0.0f, false);
			Direction face = hit instanceof BlockHitResult blockHit
					? blockHit.getSide()
					: Direction.getFacing(serverPlayer.getRotationVector()).getOpposite();

			CHAINING.set(true);
			try {
				for (BlockPos target : targets(pos, face, level, serverPlayer)) {
					if (tool.isEmpty()) break; // 도구가 부서지면 중단
					if (!canChainBreak(world, target, tool)) continue;
					serverPlayer.interactionManager.tryBreakBlock(target);
				}
			} finally {
				CHAINING.set(false);
			}
		});
	}

	private static List<BlockPos> targets(BlockPos origin, Direction face, int level, ServerPlayerEntity player) {
		List<BlockPos> list = new ArrayList<>();
		Direction inward = face.getOpposite(); // 플레이어가 보는 방향 = 블록 안쪽

		if (level >= 3) {
			// 바라보는 면에 수직인 3x3 평면
			Direction.Axis axis = face.getAxis();
			for (int a = -1; a <= 1; a++) {
				for (int b = -1; b <= 1; b++) {
					if (a == 0 && b == 0) continue;
					list.add(switch (axis) {
						case Y -> origin.add(a, 0, b);
						case X -> origin.add(0, a, b);
						case Z -> origin.add(a, b, 0);
					});
				}
			}
			return list;
		}

		// I, II: 관통 1칸
		list.add(origin.offset(inward));
		if (level >= 2) {
			// II: 수평으로 캘 때는 위 칸(통로), 바닥/천장을 캘 때는 플레이어 진행 방향 1칸
			if (face.getAxis().isHorizontal()) {
				list.add(origin.up());
			} else {
				list.add(origin.offset(player.getHorizontalFacing()));
			}
		}
		return list;
	}

	/** 공기/베드락/액체가 아니고, 들고 있는 도구가 '올바른 도구'인 블록만 연쇄 파괴 */
	private static boolean canChainBreak(World world, BlockPos pos, ItemStack tool) {
		BlockState state = world.getBlockState(pos);
		if (state.isAir() || !state.getFluidState().isEmpty()) return false;
		if (state.getHardness(world, pos) < 0) return false; // 베드락 등 파괴 불가
		return tool.isSuitableFor(state);
	}
}

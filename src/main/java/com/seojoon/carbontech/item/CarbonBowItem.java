package com.seojoon.carbontech.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * 카본 활. 탄소 섬유 시위의 탄성으로 바닐라(20틱)보다 30% 빨리 완전 당김(14틱)에 도달한다.
 * 내구도는 바닐라 활(384)의 2배, 모루 수리는 탄소 섬유.
 *
 * 바닐라 BowItem은 "사용한 틱 수 → 당김 정도"를 고정 공식(static)으로 계산하므로,
 * 발사 시점에 사용 틱 수를 DRAW_SPEED배로 부풀려서 넘기는 방식으로 속도를 올린다.
 * 당기는 애니메이션(pull 프레디킷)도 같은 배율을 쓰며, 클라이언트 진입점에 등록되어 있다.
 */
public class CarbonBowItem extends BowItem {
	/** 20틱 → 14틱: 1.43배 빠른 당김 */
	public static final float DRAW_SPEED = 20.0f / 14.0f;
	public static final int DURABILITY = 768;

	public CarbonBowItem(Settings settings) {
		super(settings);
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		int maxUseTime = this.getMaxUseTime(stack, user);
		int usedTicks = maxUseTime - remainingUseTicks;
		int scaledTicks = Math.round(usedTicks * DRAW_SPEED);
		super.onStoppedUsing(stack, world, user, maxUseTime - scaledTicks);
	}

	@Override
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return ingredient.isOf(ModItems.CARBON_FIBER);
	}
}

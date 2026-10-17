package com.seojoon.carbontech.item;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;

/**
 * 카본 방패. 바닐라 방패(내구도 336)의 2배인 672, 모루 수리는 판자 대신 카본 주괴.
 *
 * 바닐라는 방패 내구도 소모/도끼 무력화/3D 렌더링을 전부 Items.SHIELD에만 묶어 두었다.
 * 그래서 이 클래스 외에 PlayerEntityMixin(내구도·무력화)과 CarbonShieldRenderer(렌더링)가 함께 필요하다.
 */
public class CarbonShieldItem extends ShieldItem {
	public static final int DURABILITY = 672;

	public CarbonShieldItem(Settings settings) {
		super(settings);
	}

	@Override
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return ingredient.isOf(ModItems.CARBON_INGOT);
	}
}

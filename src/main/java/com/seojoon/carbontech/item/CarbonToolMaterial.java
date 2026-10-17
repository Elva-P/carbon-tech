package com.seojoon.carbontech.item;

import net.minecraft.block.Block;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

/**
 * 카본 도구 재료 (기획서 3-1).
 *
 * 바닐라 비교 (ToolMaterials.NETHERITE): 내구도 2031, 채굴 속도 9.0, 공격력 보너스 4.0, 마법 부여 15
 * 카본:                                   내구도 2031, 채굴 속도 9.9, 공격력 보너스 5.5, 마법 부여 15
 *
 * 최종 공격력 = 1(플레이어 기본) + 도구별 기본값 + 재료 보너스
 *   검:   1 + 4   + 5.5 = 10.5  (네더라이트 8)
 *   도끼: 1 + 5   + 5.5 = 11.5  (네더라이트 10)
 *   곡괭이: 1 + 1 + 5.5 = 7.5   (네더라이트 6)
 */
public enum CarbonToolMaterial implements ToolMaterial {
	INSTANCE;

	@Override
	public int getDurability() {
		return 2031;
	}

	/** 네더라이트(9.0)보다 10% 빠름 */
	@Override
	public float getMiningSpeedMultiplier() {
		return 9.9f;
	}

	@Override
	public float getAttackDamage() {
		return 5.5f;
	}

	/**
	 * "이 도구로 캘 수 없는 블록" 태그. 네더라이트와 같은 등급이면 같은 태그를 쓴다.
	 * (네더라이트 곡괭이로 못 캐는 블록은 카본 곡괭이로도 못 캔다)
	 */
	@Override
	public TagKey<Block> getInverseTag() {
		return BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
	}

	@Override
	public int getEnchantability() {
		return 15;
	}

	/** 모루 수리에 쓰는 재료 */
	@Override
	public Ingredient getRepairIngredient() {
		return Ingredient.ofItems(ModItems.CARBON_INGOT);
	}
}

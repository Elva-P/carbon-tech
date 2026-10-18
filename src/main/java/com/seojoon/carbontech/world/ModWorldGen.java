package com.seojoon.carbontech.world;

import com.seojoon.carbontech.CarbonTech;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;

/**
 * 광석을 월드에 생성시킨다.
 *
 * "어떤 모양으로, 얼마나 자주, 어느 높이에" 생성되는지는 전부 JSON으로 정의되어 있다:
 *   data/carbontech/worldgen/configured_feature/carbon_ore_*.json → 광맥 크기, 돌/심층암별 대체 블록
 *   data/carbontech/worldgen/placed_feature/carbon_ore_*.json     → 청크당 횟수, 높이 분포
 * 이 클래스는 그 placed_feature를 "오버월드의 모든 바이옴, 광석 생성 단계"에 붙이는 역할만 한다.
 */
public final class ModWorldGen {
	private ModWorldGen() {}

	/** 바닐라 1.21 다이아몬드와 같은 4종 배치: 소형 / 중형 / 대형(희귀) / 매립형 */
	private static final String[] PLACED = {"carbon_ore_small", "carbon_ore_medium", "carbon_ore_large", "carbon_ore_buried"};

	public static void initialize() {
		for (String name : PLACED) {
			BiomeModifications.addFeature(
					BiomeSelectors.foundInOverworld(),
					GenerationStep.Feature.UNDERGROUND_ORES,
					RegistryKey.of(RegistryKeys.PLACED_FEATURE, CarbonTech.id(name))
			);
		}
		CarbonTech.LOGGER.info("Carbon Tech 월드 생성 등록 완료");
	}
}

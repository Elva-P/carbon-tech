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
 *   data/carbontech/worldgen/configured_feature/carbon_ore.json  → 광맥 크기, 어떤 블록을 대체할지
 *   data/carbontech/worldgen/placed_feature/carbon_ore.json      → 청크당 횟수, 높이 범위
 * 이 클래스는 그 placed_feature를 "오버월드의 모든 바이옴, 광석 생성 단계"에 붙이는 역할만 한다.
 */
public final class ModWorldGen {
	private ModWorldGen() {}

	public static final RegistryKey<PlacedFeature> CARBON_ORE_PLACED =
			RegistryKey.of(RegistryKeys.PLACED_FEATURE, CarbonTech.id("carbon_ore"));

	public static void initialize() {
		BiomeModifications.addFeature(
				BiomeSelectors.foundInOverworld(),
				GenerationStep.Feature.UNDERGROUND_ORES,
				CARBON_ORE_PLACED
		);
		CarbonTech.LOGGER.info("Carbon Tech 월드 생성 등록 완료");
	}
}

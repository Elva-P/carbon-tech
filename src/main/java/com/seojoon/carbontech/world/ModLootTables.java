package com.seojoon.carbontech.world;

import com.seojoon.carbontech.CarbonTech;
import com.seojoon.carbontech.item.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;

/**
 * 바닐라 전리품 상자에 카본 업그레이드 형판을 섞는다.
 *
 * 네더라이트 형판이 요새 잔해에서 나오듯, 카본 형판은 심층의 고대 도시 상자에서 나온다.
 * 상자 하나당 30% 확률로 1장. (형판은 다이아몬드 7개 + 심층암으로 복제 가능)
 */
public final class ModLootTables {
	private ModLootTables() {}

	public static void initialize() {
		LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
			// source.isBuiltin(): 데이터팩이 덮어쓴 테이블이 아니라 바닐라 원본일 때만
			if (!source.isBuiltin()) return;
			if (!key.equals(LootTables.ANCIENT_CITY_CHEST)) return;

			tableBuilder.pool(LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1))
					.conditionally(RandomChanceLootCondition.builder(0.3f))
					.with(ItemEntry.builder(ModItems.CARBON_UPGRADE_SMITHING_TEMPLATE)));
		});
		CarbonTech.LOGGER.info("Carbon Tech 전리품 테이블 등록 완료");
	}
}

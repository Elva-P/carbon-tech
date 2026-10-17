package com.seojoon.carbontech;

import com.seojoon.carbontech.block.ModBlocks;
import com.seojoon.carbontech.enchantment.ModEnchantments;
import com.seojoon.carbontech.item.ModItemGroups;
import com.seojoon.carbontech.item.ModItems;
import com.seojoon.carbontech.world.ModLootTables;
import com.seojoon.carbontech.world.ModWorldGen;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 모드 진입점. fabric.mod.json의 "main" 엔트리포인트가 이 클래스를 가리킨다.
 * 게임이 모드를 불러올 때 onInitialize()가 한 번 호출되며,
 * 여기서 아이템/블록/인챈트 등 모든 등록 코드를 순서대로 부른다.
 */
public class CarbonTech implements ModInitializer {
	public static final String MOD_ID = "carbontech";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Carbon Tech 초기화 시작");

		ModItems.initialize();       // 1. 아이템
		ModBlocks.initialize();      // 2. 블록 (+ 블록아이템)
		ModItemGroups.initialize();  // 3. 크리에이티브 탭 (아이템/블록이 먼저 있어야 함)
		ModWorldGen.initialize();    // 4. 광석 월드 생성
		ModEnchantments.initialize(); // 5. 인챈트 효과 핸들러 (정의는 JSON)
		ModLootTables.initialize();   // 6. 고대 도시 상자에 업그레이드 형판
	}

	/** "carbontech:이름" 형태의 식별자를 만든다. 리소스 경로의 기준. */
	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}

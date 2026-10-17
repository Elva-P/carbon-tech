package com.seojoon.carbontech.block;

import com.seojoon.carbontech.CarbonTech;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.intprovider.UniformIntProvider;

/**
 * Carbon Tech의 모든 블록을 등록한다.
 *
 * 블록은 "월드에 놓이는 것"이고, 인벤토리에 들고 다니는 것은 별도의 BlockItem이다.
 * 그래서 블록 하나를 만들려면 블록 + 블록아이템 두 번 등록해야 한다.
 */
public final class ModBlocks {
	private ModBlocks() {}

	// ── 기획서 2-1: 카본 원광석 ───────────────────────────────────
	/**
	 * 심층암 다이아몬드 광석보다 조금 단단하다 (강도 5.0 vs 4.5).
	 * 캘 때 경험치 3~7 드롭. requiresTool()이 있어야 아래 needs_diamond_tool 태그가 작동한다.
	 */
	public static final Block CARBON_ORE = registerWithItem("carbon_ore",
			new ExperienceDroppingBlock(
					UniformIntProvider.create(3, 7),
					AbstractBlock.Settings.create()
							.strength(5.0f, 3.0f)          // (채굴 시간, 폭발 저항)
							.requiresTool()                // 맞는 도구 아니면 드롭 없음
							.sounds(BlockSoundGroup.DEEPSLATE)
			));

	/**
	 * 카본 블록: 주괴 9개 압축. 네더라이트 블록과 같은 강도(50 / 폭발 저항 1200), 비콘 베이스로 사용 가능.
	 * 비콘 베이스 여부는 코드가 아니라 data/minecraft/tags/block/beacon_base_blocks.json 태그가 결정한다.
	 */
	public static final Block CARBON_BLOCK = registerWithItem("carbon_block",
			new Block(AbstractBlock.Settings.create()
					.strength(50.0f, 1200.0f)
					.requiresTool()
					.sounds(BlockSoundGroup.NETHERITE)));

	/** 블록 레지스트리에 블록을 넣고, 같은 이름으로 블록아이템도 넣는다. */
	private static Block registerWithItem(String name, Block block) {
		Registry.register(Registries.BLOCK, CarbonTech.id(name), block);
		Registry.register(Registries.ITEM, CarbonTech.id(name), new BlockItem(block, new Item.Settings()));
		return block;
	}

	public static void initialize() {
		CarbonTech.LOGGER.info("Carbon Tech 블록 등록 완료");
	}
}

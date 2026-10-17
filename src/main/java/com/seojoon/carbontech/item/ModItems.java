package com.seojoon.carbontech.item;

import com.seojoon.carbontech.CarbonTech;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SmithingTemplateItem;
import net.minecraft.item.SwordItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * Carbon Tech의 모든 아이템을 한곳에서 등록한다.
 *
 * 등록 = "이 이름(carbontech:carbon_ingot)으로 이런 아이템이 존재한다"고 게임에 알려주는 것.
 * 등록된 이름은 텍스처/모델/언어/레시피 파일에서 그대로 쓰인다.
 */
public final class ModItems {
	private ModItems() {}

	// ── 기획서 2장: 뼈대 재료 ──────────────────────────────────────
	/** 카본 원광석을 채굴하면 나오는 원석. 고열 용광로에서 제련하면 주괴가 된다. */
	public static final Item RAW_CARBON = register("raw_carbon", new Item(new Item.Settings()));

	/** 카본 주괴. 모든 카본 장비의 핵심 재료. */
	public static final Item CARBON_INGOT = register("carbon_ingot", new Item(new Item.Settings()));

	/** 탄소 섬유. 주괴 1개 → 섬유 4개. */
	public static final Item CARBON_FIBER = register("carbon_fiber", new Item(new Item.Settings()));

	/** 탄소 막대. 섬유 2개를 세로로 배열. 나무 막대기 대신 모든 카본 도구의 손잡이. */
	public static final Item CARBON_STICK = register("carbon_stick", new Item(new Item.Settings()));

	// ── 기획서 3장: 무기 및 도구 ───────────────────────────────────
	// createAttributeModifiers(재료, 기본 공격력, 공격 속도 보정)
	// 공격 속도 = 4.0 + 보정값.  네더라이트 검 -2.4 → 1.6,  카본 검 -2.3 → 1.7
	private static final CarbonToolMaterial MAT = CarbonToolMaterial.INSTANCE;

	/** 카본 검: 공격력 10.5, 공격 속도 1.7 */
	public static final Item CARBON_SWORD = register("carbon_sword",
			new SwordItem(MAT, new Item.Settings()
					.attributeModifiers(SwordItem.createAttributeModifiers(MAT, 4, -2.3f))));

	/** 카본 곡괭이: 채굴 속도 9.9 (네더라이트 +10%), 공격 속도 1.3 */
	public static final Item CARBON_PICKAXE = register("carbon_pickaxe",
			new PickaxeItem(MAT, new Item.Settings()
					.attributeModifiers(PickaxeItem.createAttributeModifiers(MAT, 1.0f, -2.7f))));

	/** 카본 도끼: 공격력 11.5, 공격 속도 1.1 */
	public static final Item CARBON_AXE = register("carbon_axe",
			new AxeItem(MAT, new Item.Settings()
					.attributeModifiers(AxeItem.createAttributeModifiers(MAT, 5.0f, -2.9f))));

	/** 카본 삽 */
	public static final Item CARBON_SHOVEL = register("carbon_shovel",
			new ShovelItem(MAT, new Item.Settings()
					.attributeModifiers(ShovelItem.createAttributeModifiers(MAT, 1.5f, -2.9f))));

	/** 카본 괭이: 바닐라처럼 재료 보너스를 상쇄해 공격력 1로 고정 */
	public static final Item CARBON_HOE = register("carbon_hoe",
			new HoeItem(MAT, new Item.Settings()
					.attributeModifiers(HoeItem.createAttributeModifiers(MAT, -5.5f, 0.0f))));

	// ── 방어구 (수치는 네더라이트와 동일, 특수 효과는 인챈트로) ────────
	public static final Item CARBON_HELMET = registerArmor("carbon_helmet", ArmorItem.Type.HELMET);
	public static final Item CARBON_CHESTPLATE = registerArmor("carbon_chestplate", ArmorItem.Type.CHESTPLATE);
	public static final Item CARBON_LEGGINGS = registerArmor("carbon_leggings", ArmorItem.Type.LEGGINGS);
	public static final Item CARBON_BOOTS = registerArmor("carbon_boots", ArmorItem.Type.BOOTS);

	// ── 방패: 바닐라 방패 내구도의 2배, 카본 주괴로 수리 ──
	public static final Item CARBON_SHIELD = register("carbon_shield",
			new CarbonShieldItem(new Item.Settings().maxDamage(CarbonShieldItem.DURABILITY)));

	// ── 활: 30% 빠른 당김, 내구도 768, 탄소 섬유로 수리 ──
	public static final Item CARBON_BOW = register("carbon_bow",
			new CarbonBowItem(new Item.Settings().maxDamage(CarbonBowItem.DURABILITY)));

	// ── 대장간 형판: 네더라이트 장비 + 카본 주괴 → 카본 장비 (인챈트·내구도 유지) ──
	// 생성자 인자는 대장간 UI에 표시되는 안내 문구와 빈 슬롯 그림이다. 레시피는 data/carbontech/recipe/*_smithing.json
	public static final Item CARBON_UPGRADE_SMITHING_TEMPLATE = register("carbon_upgrade_smithing_template",
			new SmithingTemplateItem(
					Text.translatable("item.carbontech.smithing_template.carbon_upgrade.applies_to").formatted(Formatting.BLUE),
					Text.translatable("item.carbontech.smithing_template.carbon_upgrade.ingredients").formatted(Formatting.BLUE),
					Text.translatable("upgrade.carbontech.carbon_upgrade").formatted(Formatting.GRAY),
					Text.translatable("item.carbontech.smithing_template.carbon_upgrade.base_slot_description"),
					Text.translatable("item.carbontech.smithing_template.carbon_upgrade.additions_slot_description"),
					List.of(
							Identifier.ofVanilla("item/empty_armor_slot_helmet"),
							Identifier.ofVanilla("item/empty_slot_sword"),
							Identifier.ofVanilla("item/empty_armor_slot_chestplate"),
							Identifier.ofVanilla("item/empty_slot_pickaxe"),
							Identifier.ofVanilla("item/empty_armor_slot_leggings"),
							Identifier.ofVanilla("item/empty_slot_axe"),
							Identifier.ofVanilla("item/empty_armor_slot_boots"),
							Identifier.ofVanilla("item/empty_slot_hoe"),
							Identifier.ofVanilla("item/empty_slot_shovel")),
					List.of(Identifier.ofVanilla("item/empty_slot_ingot"))));

	private static Item registerArmor(String name, ArmorItem.Type type) {
		return register(name, new ArmorItem(CarbonArmorMaterial.CARBON, type, new Item.Settings()
				.maxDamage(type.getMaxDamage(CarbonArmorMaterial.DURABILITY_MULTIPLIER))));
	}

	/** 아이템 레지스트리에 (모드ID:이름) 키로 아이템을 넣고 그대로 돌려준다. */
	private static Item register(String name, Item item) {
		return Registry.register(Registries.ITEM, CarbonTech.id(name), item);
	}

	/**
	 * 이 메서드 자체는 아무 일도 하지 않는다.
	 * 단, 모드 초기화 시점에 호출되면 위의 static 필드들이 초기화되어 등록이 일어난다.
	 */
	public static void initialize() {
		CarbonTech.LOGGER.info("Carbon Tech 아이템 등록 완료");
	}
}

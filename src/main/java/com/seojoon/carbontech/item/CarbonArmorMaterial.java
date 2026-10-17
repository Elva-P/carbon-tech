package com.seojoon.carbontech.item;

import com.seojoon.carbontech.CarbonTech;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;

import java.util.EnumMap;
import java.util.List;

/**
 * 카본 방어구 재료.
 *
 * 1.21부터 ArmorMaterial은 enum이 아니라 레지스트리에 등록하는 record다.
 * 방어력/강인함/넉백 저항은 네더라이트와 동일하게 두고,
 * 차별점(신속, 점프, 낙하 감소, 넉백 감소)은 전부 인챈트(다음 단계)로 구현한다.
 *
 * 갑옷 텍스처 경로 (Layer의 id가 "carbontech:carbon"이면):
 *   assets/carbontech/textures/models/armor/carbon_layer_1.png  → 투구, 갑옷, 부츠
 *   assets/carbontech/textures/models/armor/carbon_layer_2.png  → 레깅스
 */
public final class CarbonArmorMaterial {
	private CarbonArmorMaterial() {}

	/** 네더라이트 방어구의 내구도 배수. 부위별 기본 내구도 × 37 = 투구 407, 갑옷 592, 레깅스 555, 부츠 481 */
	public static final int DURABILITY_MULTIPLIER = 37;

	public static final RegistryEntry<ArmorMaterial> CARBON = Registry.registerReference(
			Registries.ARMOR_MATERIAL,
			CarbonTech.id("carbon"),
			new ArmorMaterial(
					// 부위별 방어력 (네더라이트와 동일)
					Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
						map.put(ArmorItem.Type.BOOTS, 3);
						map.put(ArmorItem.Type.LEGGINGS, 6);
						map.put(ArmorItem.Type.CHESTPLATE, 8);
						map.put(ArmorItem.Type.HELMET, 3);
						map.put(ArmorItem.Type.BODY, 11); // 늑대 갑옷용 슬롯. 안 쓰지만 모든 Type에 값이 있어야 함
					}),
					15,                                        // 마법 부여 적합성
					SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE,    // 장착 소리
					() -> Ingredient.ofItems(ModItems.CARBON_INGOT),
					List.of(new ArmorMaterial.Layer(CarbonTech.id("carbon"))),
					3.0f,                                      // 강인함 (toughness)
					0.1f                                       // 넉백 저항
			));
}

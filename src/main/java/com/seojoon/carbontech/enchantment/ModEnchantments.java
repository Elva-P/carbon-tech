package com.seojoon.carbontech.enchantment;

import com.seojoon.carbontech.CarbonTech;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;

import java.util.List;

/**
 * 카본 전용 인챈트 (기획서 4장).
 *
 * 1.21부터 인챈트의 "정의"(이름, 최대 레벨, 적용 아이템, 비용)는 전부 JSON이다:
 *   data/carbontech/enchantment/*.json
 * 그래서 Java 쪽에는 레지스트리 키만 두고, JSON만으로 표현할 수 없는 효과를
 * 이벤트와 Mixin으로 구현한다.
 *
 *   고탄성 (high_elasticity) : 레깅스 신속 / 부츠 점프+낙하 50% / 투구 메이스 무효 / 갑옷 넉백 80% 감소  → Java
 *   충격 흡수 (absorption)   : 최종 데미지 5/10/15% 감소                                               → Java (Mixin)
 *   탄성 강화 (elastic_force): 레벨당 +1 데미지, 달리기 중 1.5배                                       → JSON만
 *   진동 흡수 (shock_absorption): 내구도 소모 15% 확률 무시                                            → JSON만
 *   탄성 충격 (elastic_impact): 관통 / 1x2 / 3x3 범위 채굴                                            → Java
 */
public final class ModEnchantments {
	private ModEnchantments() {}

	public static final RegistryKey<Enchantment> HIGH_ELASTICITY = of("high_elasticity");
	public static final RegistryKey<Enchantment> ABSORPTION = of("absorption");
	public static final RegistryKey<Enchantment> ELASTIC_FORCE = of("elastic_force");
	public static final RegistryKey<Enchantment> SHOCK_ABSORPTION = of("shock_absorption");
	public static final RegistryKey<Enchantment> ELASTIC_IMPACT = of("elastic_impact");

	/** 카본 전용 인챈트 전체 (크리에이티브 탭의 인챈트북 목록 등에 사용) */
	public static final List<RegistryKey<Enchantment>> ALL = List.of(
			HIGH_ELASTICITY, ABSORPTION, ELASTIC_FORCE, SHOCK_ABSORPTION, ELASTIC_IMPACT);

	private static RegistryKey<Enchantment> of(String name) {
		return RegistryKey.of(RegistryKeys.ENCHANTMENT, CarbonTech.id(name));
	}

	/** 월드의 동적 레지스트리에서 인챈트 엔트리를 찾는다. (인챈트는 월드/데이터팩마다 다를 수 있어 월드가 필요) */
	public static RegistryEntry<Enchantment> entry(World world, RegistryKey<Enchantment> key) {
		return world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(key).orElseThrow();
	}

	/** 아이템 하나에 붙은 인챈트 레벨 (없으면 0) */
	public static int level(World world, RegistryKey<Enchantment> key, ItemStack stack) {
		if (stack.isEmpty()) return 0;
		return EnchantmentHelper.getLevel(entry(world, key), stack);
	}

	/** 특정 장비 슬롯에 낀 아이템의 인챈트 레벨 (없으면 0) */
	public static int level(LivingEntity entity, EquipmentSlot slot, RegistryKey<Enchantment> key) {
		return level(entity.getWorld(), key, entity.getEquippedStack(slot));
	}

	public static void initialize() {
		HighElasticityHandler.register();
		ElasticImpactHandler.register();
		DoubleJumpHandler.register();
		CarbonTech.LOGGER.info("Carbon Tech 인챈트 핸들러 등록 완료");
	}
}

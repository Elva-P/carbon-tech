package com.seojoon.carbontech.item;

import com.seojoon.carbontech.CarbonTech;
import com.seojoon.carbontech.block.ModBlocks;
import com.seojoon.carbontech.enchantment.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;

/**
 * 크리에이티브 인벤토리에 "Carbon Tech" 탭을 하나 추가한다.
 * 모드 아이템을 /give 없이 바로 꺼내 볼 수 있어 테스트에 필수.
 */
public final class ModItemGroups {
	private ModItemGroups() {}

	public static final RegistryKey<ItemGroup> CARBON_TECH =
			RegistryKey.of(RegistryKeys.ITEM_GROUP, CarbonTech.id("carbon_tech"));

	public static void initialize() {
		Registry.register(Registries.ITEM_GROUP, CARBON_TECH, FabricItemGroup.builder()
				// 탭 아이콘: 카본 주괴
				.icon(() -> new ItemStack(ModItems.CARBON_INGOT))
				// 탭 이름: 언어 파일의 itemGroup.carbontech.carbon_tech 키를 쓴다
				.displayName(Text.translatable("itemGroup.carbontech.carbon_tech"))
				// 탭에 들어갈 아이템 목록 (표시 순서대로)
				.entries((displayContext, entries) -> {
					entries.add(ModBlocks.CARBON_ORE);
					entries.add(ModBlocks.DEEPSLATE_CARBON_ORE);
					entries.add(ModItems.RAW_CARBON);
					entries.add(ModItems.CARBON_INGOT);
					entries.add(ModItems.CARBON_FIBER);
					entries.add(ModItems.CARBON_STICK);
					entries.add(ModBlocks.CARBON_BLOCK);
					entries.add(ModItems.CARBON_SWORD);
					entries.add(ModItems.CARBON_PICKAXE);
					entries.add(ModItems.CARBON_AXE);
					entries.add(ModItems.CARBON_SHOVEL);
					entries.add(ModItems.CARBON_HOE);
					entries.add(ModItems.CARBON_HELMET);
					entries.add(ModItems.CARBON_CHESTPLATE);
					entries.add(ModItems.CARBON_LEGGINGS);
					entries.add(ModItems.CARBON_BOOTS);
					entries.add(ModItems.CARBON_SHIELD);
					entries.add(ModItems.CARBON_BOW);
					entries.add(ModItems.CARBON_UPGRADE_SMITHING_TEMPLATE);

					// 카본 전용 인챈트북: 인챈트마다 1레벨 ~ 최대 레벨까지 한 권씩
					// (인챈트는 월드별 동적 레지스트리라 displayContext.lookup()으로 찾아야 한다)
					displayContext.lookup().getOptionalWrapper(RegistryKeys.ENCHANTMENT).ifPresent(enchantments -> {
						for (RegistryKey<Enchantment> key : ModEnchantments.ALL) {
							enchantments.getOptional(key).ifPresent(entry -> {
								for (int level = entry.value().getMinLevel(); level <= entry.value().getMaxLevel(); level++) {
									entries.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(entry, level)));
								}
							});
						}
					});
				})
				.build());
	}
}

package com.seojoon.carbontech.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.seojoon.carbontech.CarbonTech;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;
import java.util.Set;

/**
 * 카본 장비 한정, 모루 합성 시 날카로움/효율/보호의 최대 레벨을 7로 올린다.
 *
 * 인챈트 최대 레벨은 아이템별이 아니라 인챈트 전체에 하나뿐이다.
 * 바닐라 정의를 7로 바꾸면 다이아몬드 검도 VII이 되므로,
 * 모루가 "합성 결과 레벨이 최대를 넘으면 최대로 자르는" 지점만 가로채서
 * 입력 아이템이 카본일 때 7을 돌려준다.
 *
 * 결과: 마법 부여대에서는 바닐라 상한(V/V/IV)까지만 나오고,
 *       모루에서 V + V → VI, VI + VI → VII 식으로 올릴 수 있다 (카본 장비만).
 */
@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin {

	private static final int CARBON_MAX_LEVEL = 7;

	private static final Set<RegistryKey<Enchantment>> BOOSTED = Set.of(
			Enchantments.SHARPNESS,
			Enchantments.EFFICIENCY,
			Enchantments.PROTECTION
	);

	/**
	 * updateResult() 안에서 호출되는 enchantment.getMaxLevel()을 감싼다.
	 * original.call(enchantment)이 바닐라 값이고, 조건에 맞으면 7로 바꿔 돌려준다.
	 */
	@WrapOperation(
			method = "updateResult",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I")
	)
	private int carbontech$raiseMaxLevelForCarbon(Enchantment enchantment, Operation<Integer> original) {
		int vanilla = original.call(enchantment);

		ForgingScreenHandlerAccessor self = (ForgingScreenHandlerAccessor) this;
		ItemStack input = self.carbontech$getInput().getStack(0);
		if (!isCarbonItem(input)) return vanilla;

		// 인챈트 객체 → 레지스트리 키 (인챈트는 월드별 동적 레지스트리라 플레이어의 월드에서 찾는다)
		Optional<RegistryKey<Enchantment>> key = self.carbontech$getPlayer().getWorld()
				.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getKey(enchantment);
		if (key.isPresent() && BOOSTED.contains(key.get())) {
			return Math.max(vanilla, CARBON_MAX_LEVEL);
		}
		return vanilla;
	}

	/** 아이템 ID의 네임스페이스가 carbontech면 카본 장비 */
	private static boolean isCarbonItem(ItemStack stack) {
		return !stack.isEmpty()
				&& Registries.ITEM.getId(stack.getItem()).getNamespace().equals(CarbonTech.MOD_ID);
	}
}

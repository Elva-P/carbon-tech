package com.seojoon.carbontech.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.seojoon.carbontech.enchantment.ModEnchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Mixin = 바닐라 클래스의 메서드에 우리 코드를 끼워 넣는 장치.
 * 이벤트 API가 없는 지점(최종 데미지 계산, 넉백 세기)을 여기서 고친다.
 *
 * 주의: Mixin 클래스는 직접 호출하거나 new 하지 않는다. 게임 로딩 시 LivingEntity에 합쳐진다.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	/**
	 * modifyAppliedDamage = 방어구, 저항 효과, 보호 인챈트까지 전부 적용한 "최종 데미지"를 계산하는 메서드.
	 * 그 반환값을 받아서 (1) 부츠 고탄성: 낙하 피해 50% 감소, (2) 충격 흡수: 레벨당 5% 감소를 적용한다.
	 */
	@ModifyReturnValue(method = "modifyAppliedDamage", at = @At("RETURN"))
	private float carbontech$reduceFinalDamage(float amount, DamageSource source) {
		LivingEntity self = (LivingEntity) (Object) this;
		if (amount <= 0.0f || !(self.getWorld() instanceof ServerWorld)) return amount;

		// 고탄성 부츠: 낙하 피해 50% 감소
		if (source.isIn(DamageTypeTags.IS_FALL)
				&& ModEnchantments.level(self, EquipmentSlot.FEET, ModEnchantments.HIGH_ELASTICITY) > 0) {
			amount *= 0.5f;
		}

		// 충격 흡수: 갑옷 + 레깅스 레벨 합산, 레벨당 5%, 최대 30%
		int absorption = ModEnchantments.level(self, EquipmentSlot.CHEST, ModEnchantments.ABSORPTION)
				+ ModEnchantments.level(self, EquipmentSlot.LEGS, ModEnchantments.ABSORPTION);
		if (absorption > 0) {
			float reduction = Math.min(0.05f * absorption, 0.30f);
			amount *= (1.0f - reduction);
		}
		return amount;
	}

	/**
	 * takeKnockback(strength, x, z)의 첫 번째 인자(세기)를 메서드 시작 시점에 바꾼다.
	 * 고탄성 갑옷: 넉백 80% 감소.
	 */
	@ModifyVariable(method = "takeKnockback", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private double carbontech$reduceKnockback(double strength) {
		LivingEntity self = (LivingEntity) (Object) this;
		if (self.getWorld().isClient()) return strength;
		if (ModEnchantments.level(self, EquipmentSlot.CHEST, ModEnchantments.HIGH_ELASTICITY) > 0) {
			return strength * 0.2;
		}
		return strength;
	}
}

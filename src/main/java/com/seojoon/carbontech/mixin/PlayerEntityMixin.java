package com.seojoon.carbontech.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.seojoon.carbontech.item.CarbonShieldItem;
import com.seojoon.carbontech.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 바닐라는 방패 내구도 소모와 도끼 무력화를 Items.SHIELD에만 적용한다.
 * 카본 방패도 같은 규칙을 따르도록 두 지점을 손본다.
 */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

	/** damageShield() 안의 activeItemStack.isOf(Items.SHIELD) 결과를 "카본 방패여도 true"로 */
	@ModifyExpressionValue(
			method = "damageShield",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z")
	)
	private boolean carbontech$carbonShieldTakesDamage(boolean original) {
		PlayerEntity self = (PlayerEntity) (Object) this;
		return original || self.getActiveItem().getItem() instanceof CarbonShieldItem;
	}

	/** 도끼에 맞아 방패가 무력화될 때: 바닐라는 5초, 카본 방패는 가벼워서 3초 */
	@Inject(method = "disableShield", at = @At("HEAD"))
	private void carbontech$disableCarbonShield(CallbackInfo ci) {
		PlayerEntity self = (PlayerEntity) (Object) this;
		if (self.getActiveItem().getItem() instanceof CarbonShieldItem) {
			self.getItemCooldownManager().set(ModItems.CARBON_SHIELD, 60);
		}
	}
}

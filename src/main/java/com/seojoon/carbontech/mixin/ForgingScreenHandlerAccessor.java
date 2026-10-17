package com.seojoon.carbontech.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ForgingScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Accessor Mixin = 바닐라 클래스의 private/protected 필드를 읽기 위한 "열쇠".
 * 모루(AnvilScreenHandler)의 부모 클래스가 가진 입력 슬롯과 플레이어 필드에 접근한다.
 */
@Mixin(ForgingScreenHandler.class)
public interface ForgingScreenHandlerAccessor {
	@Accessor("input")
	Inventory carbontech$getInput();

	@Accessor("player")
	PlayerEntity carbontech$getPlayer();
}

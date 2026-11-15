package com.wildfire.mixins;

import com.wildfire.main.WildfireGender;
import com.wildfire.main.WildfireHelper;
import com.wildfire.main.entitydata.PlayerConfig;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.living.ArmorStandEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStandEntity.class)
public abstract class ArmorStandEntityMixin {

	@Inject(method = "interactAt", at = @At("HEAD"))
	public void wildfiregender$equipArmorStandChestplate(PlayerEntity player, Vec3d interactionPoint, InteractionHand hand,
														 CallbackInfoReturnable<InteractionResult> cir) {
		if (player == null || player.world.isClient) return;

		ItemStack stack = player.getItemInHand(hand);
		Item item = stack.getItem();
		// Only apply to chestplates
		if (!(item instanceof ArmorItem) || ((ArmorItem) item).getSlot() != EquipmentSlot.CHEST) return;

		PlayerConfig playerConfig = WildfireGender.getPlayerById(player.getUuid());
		if (playerConfig == null) {
			if (stack.getNbt("WildfireGender") != null) {
				stack.removeNbt("WildfireGender");
			}
			return;
		}

		if (WildfireHelper.getArmorConfig(stack).armorStandsCopySettings()) {
			WildfireHelper.writeToNbt(player, playerConfig, stack);
		}
	}
}

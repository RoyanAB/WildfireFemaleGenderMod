package com.wildfire.mixins;

import com.wildfire.main.entitydata.EntityConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.living.ArmorStandEntity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({ArmorStandEntity.class, PlayerEntity.class})
public abstract class BreastPhysicsTickMixin {

	@Inject(method = "tick", at = @At("TAIL"))
	public void wildfiregender$tickBreastPhysics(CallbackInfo info) {
		LivingEntity entity = (LivingEntity) (Object) this;
		// Ignore ticks from the singleplayer integrated server
		if (!entity.world.isClient) return;

		EntityConfig cfg = EntityConfig.getEntity(entity);
		if (cfg == null) return;
		if (entity instanceof ArmorStandEntity) {
			cfg.readFromStack(entity.getEquipment(EquipmentSlot.CHEST));
		}
		cfg.tickBreastPhysics(entity);
	}
}

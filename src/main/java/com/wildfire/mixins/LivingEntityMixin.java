package com.wildfire.mixins;

import com.wildfire.main.WildfireGender;
import com.wildfire.main.entitydata.PlayerConfig;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Plays the configured female hurt sound alongside the vanilla hurt sound. This is done on both sides for consistency,
 * as the server broadcasts the sound to nearby clients while the client-side hook provides a fallback for servers that
 * do not have the mod installed.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	@Inject(method = "playHurtSound", at = @At("TAIL"))
	public void wildfiregender$genderHurtSound(DamageSource source, CallbackInfo ci) {
		LivingEntity self = (LivingEntity) (Object) this;
		if (!(self instanceof PlayerEntity)) return;
		this.wildfiregender$playGenderHurtSound((PlayerEntity) self);
	}

	@Unique
	private void wildfiregender$playGenderHurtSound(PlayerEntity player) {
		PlayerConfig genderPlayer = WildfireGender.getPlayerById(player.getUuid());
		if (genderPlayer == null || !genderPlayer.hasHurtSounds()) return;

		SoundEvent hurtSound = genderPlayer.getGender().getHurtSound();
		if (hurtSound != null) {
			float pitch = (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2F + 1.0F;
			player.playSound(hurtSound, 1f, pitch);
		}
	}
}

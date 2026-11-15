package com.wildfire.main;

import org.jetbrains.annotations.Nullable;

import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Formatting;

/**
 * The three genders supported by the mod.
 *
 * <p>Note that this lives in the common source set; localized display names are resolved on the client by
 * {@code com.wildfire.gui.GuiHelper#getGenderDisplayName}, as the server has no business rendering gender labels.</p>
 */
public enum Gender {
	FEMALE("wildfire_gender.label.female", Formatting.LIGHT_PURPLE, true, WildfireSounds.FEMALE_HURT),
	MALE("wildfire_gender.label.male", Formatting.BLUE, false, null),
	OTHER("wildfire_gender.label.other", Formatting.GREEN, true, WildfireSounds.FEMALE_HURT);

	private final String translationKey;
	private final Formatting color;
	private final boolean canHaveBreasts;
	private final @Nullable SoundEvent hurtSound;

	Gender(String translationKey, Formatting color, boolean canHaveBreasts, @Nullable SoundEvent hurtSound) {
		this.translationKey = translationKey;
		this.color = color;
		this.canHaveBreasts = canHaveBreasts;
		this.hurtSound = hurtSound;
	}

	public String getTranslationKey() {
		return this.translationKey;
	}

	public Formatting getColor() {
		return this.color;
	}

	public @Nullable SoundEvent getHurtSound() {
		return this.hurtSound;
	}

	public boolean canHaveBreasts() {
		return this.canHaveBreasts;
	}
}

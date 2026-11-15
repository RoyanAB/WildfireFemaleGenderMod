package com.wildfire.main;

import net.minecraft.text.Formatting;

/**
 * The three genders supported by the mod.
 *
 * <p>Note that this lives in the common source set; localized display names are resolved on the client by
 * {@code com.wildfire.gui.GuiHelper#getGenderDisplayName}, as the server has no business rendering gender labels.</p>
 */
public enum Gender {
	FEMALE("wildfire_gender.label.female", Formatting.LIGHT_PURPLE, true),
	MALE("wildfire_gender.label.male", Formatting.BLUE, false),
	OTHER("wildfire_gender.label.other", Formatting.GREEN, true);

	private final String translationKey;
	private final Formatting color;
	private final boolean canHaveBreasts;

	Gender(String translationKey, Formatting color, boolean canHaveBreasts) {
		this.translationKey = translationKey;
		this.color = color;
		this.canHaveBreasts = canHaveBreasts;
	}

	public String getTranslationKey() {
		return this.translationKey;
	}

	public Formatting getColor() {
		return this.color;
	}

	public boolean canHaveBreasts() {
		return this.canHaveBreasts;
	}
}

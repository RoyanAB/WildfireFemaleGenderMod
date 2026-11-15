package com.wildfire.render.armor;

import com.wildfire.api.IGenderArmor;

/**
 * Default implementations of {@link IGenderArmor} for vanilla armor types.
 */
public final class SimpleGenderArmor implements IGenderArmor {

	public static final SimpleGenderArmor FALLBACK = new SimpleGenderArmor(0.5F);
	public static final SimpleGenderArmor LEATHER = new SimpleGenderArmor(0.3F, 0.5F);
	public static final SimpleGenderArmor CHAIN_MAIL = new SimpleGenderArmor(0.5F, 0.2F);
	public static final SimpleGenderArmor GOLD = new SimpleGenderArmor(0.85F, true);
	public static final SimpleGenderArmor IRON = new SimpleGenderArmor(1F, true);
	public static final SimpleGenderArmor DIAMOND = new SimpleGenderArmor(1F, true);

	private final float physicsResistance;
	private final float tightness;
	private final boolean armorStandsCopySettings;

	public SimpleGenderArmor(float physicsResistance) {
		this(physicsResistance, 0F, false);
	}

	public SimpleGenderArmor(float physicsResistance, boolean armorStandsCopySettings) {
		this(physicsResistance, 0F, armorStandsCopySettings);
	}

	public SimpleGenderArmor(float physicsResistance, float tightness) {
		this(physicsResistance, tightness, false);
	}

	public SimpleGenderArmor(float physicsResistance, float tightness, boolean armorStandsCopySettings) {
		this.physicsResistance = physicsResistance;
		this.tightness = tightness;
		this.armorStandsCopySettings = armorStandsCopySettings;
	}

	@Override
	public float physicsResistance() {
		return physicsResistance;
	}

	@Override
	public float tightness() {
		return tightness;
	}

	@Override
	public boolean armorStandsCopySettings() {
		return armorStandsCopySettings;
	}
}

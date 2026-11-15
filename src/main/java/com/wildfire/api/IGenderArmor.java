package com.wildfire.api;

/**
 * Implement this on a custom class for your chestplates or items that go in the chest slot to configure how it
 * interacts with breast rendering.
 */
public interface IGenderArmor {

	/**
	 * Determines whether this {@link IGenderArmor} "covers" the breasts or if it has an open front ({@code false}) like
	 * the elytra.
	 */
	default boolean coversBreasts() {
		return true;
	}

	/**
	 * Determines if this {@link IGenderArmor} should always hide the wearer's breasts when worn even if they have
	 * {@code showBreastsInArmor} set to {@code true}.
	 */
	default boolean alwaysHidesBreasts() {
		return false;
	}

	/**
	 * The percent of physical resistance this {@link IGenderArmor} provides to the wearer's breasts when calculating
	 * the corresponding physics.
	 *
	 * @return Value between {@code 0} (no resistance, full physics) and {@code 1} (total resistance, no physics).
	 */
	default float physicsResistance() {
		return 0;
	}

	/**
	 * Value representing how "tight" this {@link IGenderArmor} is. Tightness "compresses" the breasts against the
	 * wearer causing the breasts to appear up to {@code 15%} smaller.
	 *
	 * @return Value between {@code 0} (no tightness, no size reduction) and {@code 1} (full tightness, {@code 15%}
	 * size reduction).
	 */
	default float tightness() {
		return 0;
	}

	/**
	 * Determines whether armor stands should copy the breast settings of the player equipping this chestplate onto it.
	 */
	default boolean armorStandsCopySettings() {
		return !alwaysHidesBreasts() && coversBreasts() && physicsResistance() == 1f;
	}
}

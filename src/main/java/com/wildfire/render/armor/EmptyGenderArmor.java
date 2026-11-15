package com.wildfire.render.armor;

import com.wildfire.api.IGenderArmor;

/**
 * Implementation of {@link IGenderArmor} for when there is nothing being worn or the item being worn does not cover
 * the breast area.
 */
public class EmptyGenderArmor implements IGenderArmor {

	public static final EmptyGenderArmor INSTANCE = new EmptyGenderArmor();

	private EmptyGenderArmor() {
	}

	@Override
	public boolean coversBreasts() {
		return false;
	}

	@Override
	public boolean armorStandsCopySettings() {
		return false;
	}
}

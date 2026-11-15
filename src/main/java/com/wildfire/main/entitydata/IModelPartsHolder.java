package com.wildfire.main.entitydata;

/**
 * Implemented by a mixin on {@code PlayerEntity} so that common code can read the player's model parts flag without
 * referring to the client-only {@code PlayerModelPart} enum or {@code PlayerEntity#isModelPartVisible}, both of which
 * do not exist on dedicated servers in 1.12.2.
 */
public interface IModelPartsHolder {

	/**
	 * @return the raw model parts bit flag, where bit 1 (value {@code 2}) is the jacket layer.
	 */
	int wildfiregender$getModelParts();
}

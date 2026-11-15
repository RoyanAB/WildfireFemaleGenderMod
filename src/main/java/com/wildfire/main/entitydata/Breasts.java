package com.wildfire.main.entitydata;

import java.util.function.Consumer;

import com.wildfire.main.config.ConfigKey;
import com.wildfire.main.config.Configuration;

/**
 * Data class representing an entity's breast appearance settings.
 */
public class Breasts {

	private float xOffset = Configuration.BREASTS_OFFSET_X.getDefault();
	private float yOffset = Configuration.BREASTS_OFFSET_Y.getDefault();
	private float zOffset = Configuration.BREASTS_OFFSET_Z.getDefault();
	private float cleavage = Configuration.BREASTS_CLEAVAGE.getDefault();
	private boolean uniboob = Configuration.BREASTS_UNIBOOB.getDefault();

	private <VALUE> boolean updateValue(ConfigKey<VALUE> key, VALUE value, Consumer<VALUE> setter) {
		if (key.validate(value)) {
			setter.accept(value);
			return true;
		}
		return false;
	}

	/**
	 * How far apart the player's breasts should be rendered from each other, also referred to as Separation in the UI.
	 */
	public float getXOffset() {
		return xOffset;
	}

	public boolean updateXOffset(float value) {
		return updateValue(Configuration.BREASTS_OFFSET_X, value, new Consumer<Float>() {
			@Override
			public void accept(Float v) {
				xOffset = v;
			}
		});
	}

	/**
	 * How far up or down the player's breasts should be rendered, also referred to as Height in the UI.
	 */
	public float getYOffset() {
		return yOffset;
	}

	public boolean updateYOffset(float value) {
		return updateValue(Configuration.BREASTS_OFFSET_Y, value, new Consumer<Float>() {
			@Override
			public void accept(Float v) {
				yOffset = v;
			}
		});
	}

	/**
	 * How far back the player's breasts should be rendered, also referred to as Depth in the UI.
	 */
	public float getZOffset() {
		return zOffset;
	}

	public boolean updateZOffset(float value) {
		return updateValue(Configuration.BREASTS_OFFSET_Z, value, new Consumer<Float>() {
			@Override
			public void accept(Float v) {
				zOffset = v;
			}
		});
	}

	/**
	 * How much rotation outward there should be on each of the player's breasts.
	 */
	public float getCleavage() {
		return cleavage;
	}

	public boolean updateCleavage(float value) {
		return updateValue(Configuration.BREASTS_CLEAVAGE, value, new Consumer<Float>() {
			@Override
			public void accept(Float v) {
				cleavage = v;
			}
		});
	}

	/**
	 * Determines if breast physics should be independent of each other; also referred to as Dual-Physics in the UI.
	 */
	public boolean isUniboob() {
		return uniboob;
	}

	public boolean updateUniboob(boolean value) {
		return updateValue(Configuration.BREASTS_UNIBOOB, value, new Consumer<Boolean>() {
			@Override
			public void accept(Boolean v) {
				uniboob = v;
			}
		});
	}
}

package com.wildfire.main.entitydata;

import com.wildfire.main.Gender;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.config.ConfigKey;
import com.wildfire.main.config.Configuration;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * A version of {@link EntityConfig} backed by a {@link Configuration} for use with players.
 */
public class PlayerConfig extends EntityConfig {

	public boolean needsSync;
	public SyncStatus syncStatus = SyncStatus.UNKNOWN;

	private final Configuration cfg;
	private boolean armorPhysOverride = Configuration.ARMOR_PHYSICS_OVERRIDE.getDefault();

	public PlayerConfig(UUID uuid) {
		this(uuid, Configuration.GENDER.getDefault());
	}

	public PlayerConfig(UUID uuid, Gender gender) {
		super(uuid);
		this.gender = gender;
		this.cfg = new Configuration("WildfireGender", this.uuid.toString());
		this.cfg.set(Configuration.USERNAME, this.uuid);
		this.cfg.setDefault(Configuration.GENDER);
		this.cfg.setDefault(Configuration.BUST_SIZE);

		this.cfg.setDefault(Configuration.BREASTS_OFFSET_X);
		this.cfg.setDefault(Configuration.BREASTS_OFFSET_Y);
		this.cfg.setDefault(Configuration.BREASTS_OFFSET_Z);
		this.cfg.setDefault(Configuration.BREASTS_UNIBOOB);
		this.cfg.setDefault(Configuration.BREASTS_CLEAVAGE);

		this.cfg.setDefault(Configuration.BREAST_PHYSICS);
		this.cfg.setDefault(Configuration.ARMOR_PHYSICS_OVERRIDE);
		this.cfg.setDefault(Configuration.SHOW_IN_ARMOR);
		this.cfg.setDefault(Configuration.BOUNCE_MULTIPLIER);
		this.cfg.setDefault(Configuration.FLOPPY_MULTIPLIER);
		this.cfg.finish();
	}

	@Override
	public void readFromStack(@NotNull ItemStack chestplate) {
	}

	public Configuration getConfig() {
		return cfg;
	}

	private <VALUE> boolean updateValue(ConfigKey<VALUE> key, VALUE value, Consumer<VALUE> setter) {
		if (key.validate(value)) {
			setter.accept(value);
			return true;
		}
		return false;
	}

	public boolean updateGender(final Gender value) {
		return updateValue(Configuration.GENDER, value, v -> gender = v);
	}

	public boolean updateBustSize(final float value) {
		return updateValue(Configuration.BUST_SIZE, value, v -> pBustSize = v);
	}

	public boolean updateBreastPhysics(final boolean value) {
		return updateValue(Configuration.BREAST_PHYSICS, value, v -> breastPhysics = v);
	}

	@Override
	public boolean getArmorPhysicsOverride() {
		return armorPhysOverride;
	}

	public boolean updateArmorPhysicsOverride(final boolean value) {
		return updateValue(Configuration.ARMOR_PHYSICS_OVERRIDE, value, v -> armorPhysOverride = v);
	}

	@Override
	public boolean showBreastsInArmor() {
		return showBreastsInArmor;
	}

	public boolean updateShowBreastsInArmor(final boolean value) {
		return updateValue(Configuration.SHOW_IN_ARMOR, value, v -> showBreastsInArmor = v);
	}

	public boolean updateBounceMultiplier(final float value) {
		return updateValue(Configuration.BOUNCE_MULTIPLIER, value, v -> bounceMultiplier = v);
	}

	public boolean updateFloppiness(final float value) {
		return updateValue(Configuration.FLOPPY_MULTIPLIER, value, v -> floppyMultiplier = v);
	}

	public static PlayerConfig loadCachedPlayer(UUID uuid, boolean markForSync) {
		PlayerConfig plr = WildfireGender.getPlayerById(uuid);
		if (plr != null) {
			// A sync packet may have arrived while the cached file was being read; server data is authoritative,
			// so don't overwrite it with the (possibly stale) local file.
			if (plr.syncStatus == SyncStatus.SYNCED && !markForSync) {
				return plr;
			}
			plr.syncStatus = SyncStatus.CACHED;
			Configuration config = plr.getConfig();
			plr.updateGender(config.get(Configuration.GENDER));
			plr.updateBustSize(config.get(Configuration.BUST_SIZE));

			plr.updateBreastPhysics(config.get(Configuration.BREAST_PHYSICS));
			plr.updateShowBreastsInArmor(config.get(Configuration.SHOW_IN_ARMOR));
			plr.updateArmorPhysicsOverride(config.get(Configuration.ARMOR_PHYSICS_OVERRIDE));
			plr.updateBounceMultiplier(config.get(Configuration.BOUNCE_MULTIPLIER));
			plr.updateFloppiness(config.get(Configuration.FLOPPY_MULTIPLIER));

			Breasts breasts = plr.getBreasts();
			breasts.updateXOffset(config.get(Configuration.BREASTS_OFFSET_X));
			breasts.updateYOffset(config.get(Configuration.BREASTS_OFFSET_Y));
			breasts.updateZOffset(config.get(Configuration.BREASTS_OFFSET_Z));
			breasts.updateUniboob(config.get(Configuration.BREASTS_UNIBOOB));
			breasts.updateCleavage(config.get(Configuration.BREASTS_CLEAVAGE));
			if (markForSync) {
				plr.needsSync = true;
			}
			return plr;
		}
		return null;
	}

	public static void saveGenderInfo(PlayerConfig plr) {
		Configuration config = plr.getConfig();
		config.set(Configuration.USERNAME, plr.uuid);
		config.set(Configuration.GENDER, plr.getGender());
		config.set(Configuration.BUST_SIZE, plr.getBustSize());

		config.set(Configuration.BREAST_PHYSICS, plr.hasBreastPhysics());
		config.set(Configuration.SHOW_IN_ARMOR, plr.showBreastsInArmor());
		config.set(Configuration.ARMOR_PHYSICS_OVERRIDE, plr.getArmorPhysicsOverride());
		config.set(Configuration.BOUNCE_MULTIPLIER, plr.getBounceMultiplier());
		config.set(Configuration.FLOPPY_MULTIPLIER, plr.getFloppiness());

		config.set(Configuration.BREASTS_OFFSET_X, plr.getBreasts().getXOffset());
		config.set(Configuration.BREASTS_OFFSET_Y, plr.getBreasts().getYOffset());
		config.set(Configuration.BREASTS_OFFSET_Z, plr.getBreasts().getZOffset());
		config.set(Configuration.BREASTS_UNIBOOB, plr.getBreasts().isUniboob());
		config.set(Configuration.BREASTS_CLEAVAGE, plr.getBreasts().getCleavage());

		config.save();
		plr.needsSync = true;
	}

	@Override
	public boolean hasJacketLayer() {
		throw new UnsupportedOperationException("PlayerConfig does not support #hasJacketLayer(); use PlayerEntity#isModelPartVisible instead");
	}

	public enum SyncStatus {
		CACHED, SYNCED, UNKNOWN
	}
}

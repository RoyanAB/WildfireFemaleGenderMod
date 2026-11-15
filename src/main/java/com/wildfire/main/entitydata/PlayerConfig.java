package com.wildfire.main.entitydata;

import java.util.UUID;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;
import com.wildfire.main.Gender;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.config.ConfigKey;
import com.wildfire.main.config.Configuration;

import net.minecraft.item.ItemStack;

/**
 * A version of {@link EntityConfig} backed by a {@link Configuration} for use with players.
 */
public class PlayerConfig extends EntityConfig {

	public boolean needsSync;
	public SyncStatus syncStatus = SyncStatus.UNKNOWN;

	private final Configuration cfg;
	private boolean hurtSounds = Configuration.HURT_SOUNDS.getDefault();
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
		this.cfg.setDefault(Configuration.HURT_SOUNDS);

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
		return updateValue(Configuration.GENDER, value, new Consumer<Gender>() {
			@Override
			public void accept(Gender v) {
				gender = v;
			}
		});
	}

	public boolean updateBustSize(final float value) {
		return updateValue(Configuration.BUST_SIZE, value, new Consumer<Float>() {
			@Override
			public void accept(Float v) {
				pBustSize = v;
			}
		});
	}

	public boolean hasHurtSounds() {
		return hurtSounds;
	}

	public boolean updateHurtSounds(final boolean value) {
		return updateValue(Configuration.HURT_SOUNDS, value, new Consumer<Boolean>() {
			@Override
			public void accept(Boolean v) {
				hurtSounds = v;
			}
		});
	}

	public boolean updateBreastPhysics(final boolean value) {
		return updateValue(Configuration.BREAST_PHYSICS, value, new Consumer<Boolean>() {
			@Override
			public void accept(Boolean v) {
				breastPhysics = v;
			}
		});
	}

	@Override
	public boolean getArmorPhysicsOverride() {
		return armorPhysOverride;
	}

	public boolean updateArmorPhysicsOverride(final boolean value) {
		return updateValue(Configuration.ARMOR_PHYSICS_OVERRIDE, value, new Consumer<Boolean>() {
			@Override
			public void accept(Boolean v) {
				armorPhysOverride = v;
			}
		});
	}

	@Override
	public boolean showBreastsInArmor() {
		return showBreastsInArmor;
	}

	public boolean updateShowBreastsInArmor(final boolean value) {
		return updateValue(Configuration.SHOW_IN_ARMOR, value, new Consumer<Boolean>() {
			@Override
			public void accept(Boolean v) {
				showBreastsInArmor = v;
			}
		});
	}

	public boolean updateBounceMultiplier(final float value) {
		return updateValue(Configuration.BOUNCE_MULTIPLIER, value, new Consumer<Float>() {
			@Override
			public void accept(Float v) {
				bounceMultiplier = v;
			}
		});
	}

	public boolean updateFloppiness(final float value) {
		return updateValue(Configuration.FLOPPY_MULTIPLIER, value, new Consumer<Float>() {
			@Override
			public void accept(Float v) {
				floppyMultiplier = v;
			}
		});
	}

	public SyncStatus getSyncStatus() {
		return this.syncStatus;
	}

	public static JsonObject toJsonObject(PlayerConfig plr) {
		JsonObject obj = new JsonObject();
		Configuration.USERNAME.save(obj, plr.uuid);
		Configuration.GENDER.save(obj, plr.getGender());
		Configuration.BUST_SIZE.save(obj, plr.getBustSize());
		Configuration.HURT_SOUNDS.save(obj, plr.hasHurtSounds());

		Configuration.BREAST_PHYSICS.save(obj, plr.hasBreastPhysics());
		Configuration.SHOW_IN_ARMOR.save(obj, plr.showBreastsInArmor());
		Configuration.ARMOR_PHYSICS_OVERRIDE.save(obj, plr.getArmorPhysicsOverride());
		Configuration.BOUNCE_MULTIPLIER.save(obj, plr.getBounceMultiplier());
		Configuration.FLOPPY_MULTIPLIER.save(obj, plr.getFloppiness());

		Breasts breasts = plr.getBreasts();
		Configuration.BREASTS_OFFSET_X.save(obj, breasts.getXOffset());
		Configuration.BREASTS_OFFSET_Y.save(obj, breasts.getYOffset());
		Configuration.BREASTS_OFFSET_Z.save(obj, breasts.getZOffset());
		Configuration.BREASTS_UNIBOOB.save(obj, breasts.isUniboob());
		Configuration.BREASTS_CLEAVAGE.save(obj, breasts.getCleavage());
		return obj;
	}

	public static PlayerConfig loadCachedPlayer(UUID uuid, boolean markForSync) {
		PlayerConfig plr = WildfireGender.getPlayerById(uuid);
		if (plr != null) {
			plr.syncStatus = SyncStatus.CACHED;
			Configuration config = plr.getConfig();
			plr.updateGender(config.get(Configuration.GENDER));
			plr.updateBustSize(config.get(Configuration.BUST_SIZE));
			plr.updateHurtSounds(config.get(Configuration.HURT_SOUNDS));

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
		config.set(Configuration.HURT_SOUNDS, plr.hasHurtSounds());

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

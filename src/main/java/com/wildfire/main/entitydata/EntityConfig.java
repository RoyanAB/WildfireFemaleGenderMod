package com.wildfire.main.entitydata;

import com.wildfire.api.IGenderArmor;
import com.wildfire.main.Gender;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.WildfireHelper;
import com.wildfire.main.config.Configuration;
import com.wildfire.physics.BreastPhysics;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.living.ArmorStandEntity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

/**
 * <p>A stripped down version of a {@link PlayerConfig player's config}, intended for use with non-player entities.</p>
 *
 * <p>Currently only used for {@link ArmorStandEntity armor stands}, and as a superclass for {@link PlayerConfig player configs}.</p>
 */
public class EntityConfig {

	public static final HashMap<UUID, EntityConfig> ENTITY_CACHE = new HashMap<>();

	public final UUID uuid;
	protected Gender gender = Configuration.GENDER.getDefault();
	protected float pBustSize = Configuration.BUST_SIZE.getDefault();
	protected boolean breastPhysics = Configuration.BREAST_PHYSICS.getDefault();
	protected float bounceMultiplier = Configuration.BOUNCE_MULTIPLIER.getDefault();
	protected float floppyMultiplier = Configuration.FLOPPY_MULTIPLIER.getDefault();
	protected boolean showBreastsInArmor = Configuration.SHOW_IN_ARMOR.getDefault();
	protected final BreastPhysics lBreastPhysics;
	protected final BreastPhysics rBreastPhysics;
	protected final Breasts breasts;
	protected boolean jacketLayer = true;

	EntityConfig(UUID uuid) {
		this.uuid = uuid;
		this.breasts = new Breasts();
		lBreastPhysics = new BreastPhysics(this);
		rBreastPhysics = new BreastPhysics(this);
	}

	public void readFromStack(@NotNull ItemStack chestplate) {
		NbtCompound nbt = !chestplate.isEmpty() ? chestplate.getNbt("WildfireGender") : null;
		if (nbt == null) {
			this.gender = Gender.MALE;
			return;
		}
		this.pBustSize = nbt.contains("BreastSize") ? nbt.getFloat("BreastSize") : 0f;
		this.gender = this.pBustSize > 0.02f ? Gender.FEMALE : Gender.MALE;
		if (nbt.contains("Cleavage")) breasts.updateCleavage(nbt.getFloat("Cleavage"));
		if (nbt.contains("Uniboob")) breasts.updateUniboob(nbt.getBoolean("Uniboob"));
		if (nbt.contains("XOffset")) breasts.updateXOffset(nbt.getFloat("XOffset"));
		if (nbt.contains("YOffset")) breasts.updateYOffset(nbt.getFloat("YOffset"));
		if (nbt.contains("ZOffset")) breasts.updateZOffset(nbt.getFloat("ZOffset"));
		if (nbt.contains("Jacket")) jacketLayer = nbt.getBoolean("Jacket");
	}

	/**
	 * Get the configuration for a given entity.
	 */
	public static @Nullable EntityConfig getEntity(@NotNull LivingEntity entity) {
		if (entity instanceof PlayerEntity) {
			return WildfireGender.getPlayerById(entity.getUuid());
		}
		if (entity.isBaby()) {
			return null;
		}
		EntityConfig config = ENTITY_CACHE.get(entity.getUuid());
		if (config == null) {
			config = new EntityConfig(entity.getUuid());
			ENTITY_CACHE.put(entity.getUuid(), config);
		}
		return config;
	}

	public @NotNull Gender getGender() {
		return gender;
	}

	public @NotNull Breasts getBreasts() {
		return breasts;
	}

	public float getBustSize() {
		return pBustSize;
	}

	public boolean hasBreastPhysics() {
		return breastPhysics;
	}

	public boolean getArmorPhysicsOverride() {
		return false;
	}

	public boolean showBreastsInArmor() {
		return true;
	}

	public float getBounceMultiplier() {
		return bounceMultiplier;
	}

	public float getFloppiness() {
		return this.floppyMultiplier;
	}

	public @NotNull BreastPhysics getLeftBreastPhysics() {
		return lBreastPhysics;
	}

	public @NotNull BreastPhysics getRightBreastPhysics() {
		return rBreastPhysics;
	}

	/**
	 * Only used in the case of armor stands.
	 */
	public boolean hasJacketLayer() {
		return jacketLayer;
	}

	@Environment(EnvType.CLIENT)
	public void tickBreastPhysics(@NotNull LivingEntity entity) {
		IGenderArmor armor = WildfireHelper.getArmorConfig(entity.getEquipment(EquipmentSlot.CHEST));

		getLeftBreastPhysics().update(entity, armor);
		getRightBreastPhysics().update(entity, armor);
	}

	@Override
	public String toString() {
		return String.format("%s(uuid=%s, gender=%s)", getClass().getCanonicalName(), uuid, gender);
	}
}

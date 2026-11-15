package com.wildfire.api;

import com.wildfire.main.Gender;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.config.Configuration;
import com.wildfire.main.entitydata.PlayerConfig;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Future;

@SuppressWarnings("unused")
public class WildfireAPI {

	private static final Map<Item, IGenderArmor> GENDER_ARMORS = new HashMap<>();

	/**
	 * Add custom physics resistance attributes to a chestplate.
	 */
	public static void addGenderArmor(Item item, IGenderArmor genderArmor) {
		GENDER_ARMORS.put(item, genderArmor);
	}

	/**
	 * Get the config for a {@link PlayerEntity}.
	 */
	public static @Nullable PlayerConfig getPlayerById(UUID uuid) {
		return WildfireGender.getPlayerById(uuid);
	}

	/**
	 * Get the player's {@link Gender}.
	 */
	public static @NotNull Gender getPlayerGender(UUID uuid) {
		PlayerConfig cfg = WildfireGender.getPlayerById(uuid);
		if (cfg == null) return Configuration.GENDER.getDefault();
		return cfg.getGender();
	}

	/**
	 * Load the cached gender settings file for the specified {@link UUID}.
	 */
	public static Future<Optional<PlayerConfig>> loadGenderInfo(UUID uuid, boolean markForSync) {
		return WildfireGender.loadGenderInfo(uuid, markForSync);
	}

	/**
	 * Get every registered {@link IGenderArmor custom armor configuration}.
	 */
	public static Map<Item, IGenderArmor> getGenderArmors() {
		return GENDER_ARMORS;
	}
}

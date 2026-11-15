package com.wildfire.main;

import java.util.concurrent.ThreadLocalRandom;

import org.jetbrains.annotations.NotNull;

import com.wildfire.api.IGenderArmor;
import com.wildfire.api.WildfireAPI;
import com.wildfire.main.entitydata.IModelPartsHolder;
import com.wildfire.main.entitydata.PlayerConfig;
import com.wildfire.render.armor.EmptyGenderArmor;
import com.wildfire.render.armor.SimpleGenderArmor;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class WildfireHelper {

	public static int randInt(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}

	public static float randFloat(float min, float max) {
		return (float) ThreadLocalRandom.current().nextDouble(min, (double) max + 1);
	}

	public static IGenderArmor getArmorConfig(ItemStack stack) {
		if (stack.isEmpty()) {
			return EmptyGenderArmor.INSTANCE;
		}

		IGenderArmor registered = WildfireAPI.getGenderArmors().get(stack.getItem());
		if (registered != null) {
			return registered;
		}

		if (stack.getItem() instanceof ArmorItem) {
			ArmorItem armorItem = (ArmorItem) stack.getItem();
			if (armorItem.getSlot() == EquipmentSlot.CHEST) {
				// Check vanilla chestplates against the armor material instead of the item instance, so that mods
				// defining custom armor items using vanilla materials still get a sensible default implementation.
				ArmorItem.Tier material = armorItem.getTier();
				if (material == ArmorItem.Tier.CLOTH) {
					return SimpleGenderArmor.LEATHER;
				} else if (material == ArmorItem.Tier.CHAIN) {
					return SimpleGenderArmor.CHAIN_MAIL;
				} else if (material == ArmorItem.Tier.GOLD) {
					return SimpleGenderArmor.GOLD;
				} else if (material == ArmorItem.Tier.IRON) {
					return SimpleGenderArmor.IRON;
				} else if (material == ArmorItem.Tier.DIAMOND) {
					return SimpleGenderArmor.DIAMOND;
				}
				return SimpleGenderArmor.FALLBACK;
			}
		}

		// Not an armor item; default to "nothing is being worn that covers the breast area". This also covers
		// wearables such as the elytra.
		return EmptyGenderArmor.INSTANCE;
	}

	/**
	 * <p>Write a player's gender config to NBT on the given item stack.</p>
	 *
	 * <p>This only copies enough data to render breasts similarly to how they'd appear on the given player, which
	 * includes the breast size, cleavage, uniboob, the X/Y/Z offsets, and whether the player's jacket layer is
	 * visible.</p>
	 */
	public static void writeToNbt(@NotNull PlayerEntity player, @NotNull PlayerConfig config, @NotNull ItemStack armor) {
		NbtCompound nbt = armor.getOrCreateNbt("WildfireGender");
		nbt.putFloat("BreastSize", config.getGender().canHaveBreasts() && config.showBreastsInArmor() ? config.getBustSize() : 0f);
		nbt.putFloat("Cleavage", config.getBreasts().getCleavage());
		nbt.putBoolean("Uniboob", config.getBreasts().isUniboob());
		nbt.putFloat("XOffset", config.getBreasts().getXOffset());
		nbt.putFloat("YOffset", config.getBreasts().getYOffset());
		nbt.putFloat("ZOffset", config.getBreasts().getZOffset());
		nbt.putBoolean("Jacket", (((IModelPartsHolder) player).wildfiregender$getModelParts() & 2) != 0);
	}
}

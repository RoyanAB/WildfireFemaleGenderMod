package com.wildfire.main;

import java.util.UUID;
import java.util.function.Consumer;

import com.wildfire.gui.screen.WardrobeBrowserScreen;
import com.wildfire.main.entitydata.EntityConfig;
import com.wildfire.main.entitydata.PlayerConfig;
import com.wildfire.main.networking.WildfireSyncClient;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.entity.living.player.PlayerEntity;
import net.ornithemc.osl.keybinds.api.KeybindEvents;
import net.ornithemc.osl.keybinds.api.KeybindRegistry;
import net.ornithemc.osl.lifecycle.api.client.MinecraftClientEvents;
import org.lwjgl.input.Keyboard;

@Environment(EnvType.CLIENT)
public class WildfireEventHandler {

	public static KeyBinding toggleEditGUI;
	private static long timer = 0;

	public static void registerClientEvents() {
		KeybindEvents.REGISTER_KEYBINDS.register(new Runnable() {
			@Override
			public void run() {
				toggleEditGUI = KeybindRegistry.register("key.wildfire_gender.gender_menu", Keyboard.KEY_G, "category.wildfire_gender.generic");
			}
		});

		MinecraftClientEvents.TICK_END.register(new Consumer<Minecraft>() {
			@Override
			public void accept(Minecraft minecraft) {
				onClientTick(minecraft);
			}
		});

		WildfireSyncClient.registerClient();
	}

	private static void onClientTick(Minecraft client) {
		if (client.world == null || client.player == null) return;

		loadPlayerConfigs(client);

		// Only attempt to sync if the server will accept the packet, and only once every 5 ticks, or around 4 times
		// a second.
		if (timer++ % 5 == 0) {
			PlayerConfig aPlr = WildfireGender.getPlayerById(client.player.getUuid());
			if (aPlr != null) WildfireSyncClient.sendToServer(aPlr);
		}

		if (toggleEditGUI != null && toggleEditGUI.consumeClick() && client.screen == null) {
			client.openScreen(new WardrobeBrowserScreen(null, client.player.getUuid()));
		}
	}

	/**
	 * Equivalent to the client entity load event from newer versions: whenever an unknown player shows up in the
	 * client world, create a config for them and load their cached settings from disk.
	 */
	private static void loadPlayerConfigs(Minecraft client) {
		for (PlayerEntity player : client.world.players) {
			UUID uuid = player.getUuid();
			if (WildfireGender.getPlayerById(uuid) == null) {
				WildfireGender.PLAYER_CACHE.put(uuid, new PlayerConfig(uuid));
				WildfireGender.loadGenderInfo(uuid, uuid.equals(client.player.getUuid()));
			}
		}
	}

	/**
	 * Clears the various player caches; called when the client disconnects from a world.
	 */
	public static void disconnect() {
		WildfireGender.PLAYER_CACHE.clear();
		EntityConfig.ENTITY_CACHE.clear();
	}
}

package com.wildfire.main;

import java.util.ArrayList;
import java.util.function.Consumer;

import com.wildfire.main.entitydata.PlayerConfig;
import com.wildfire.main.networking.WildfireSync;

import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.ornithemc.osl.entrypoints.api.ModInitializer;
import net.ornithemc.osl.networking.api.server.ServerConnectionEvents;

public class WildfireGenderServer implements ModInitializer {

	@Override
	public void init() {
		// While this class is named 'Server', this is actually a common code path, so we can safely register here
		// for both sides.
		WildfireSounds.register();
		WildfireSync.registerServer();

		ServerConnectionEvents.PLAY_READY.register(new Consumer<ServerConnectionEvents.PlayReadyContext>() {
			@Override
			public void accept(ServerConnectionEvents.PlayReadyContext ctx) {
				onPlayerJoin(ctx.player());
			}
		});
	}

	/**
	 * Replacement for the client-bound entity tracking event used by newer versions: whenever a player connects, send
	 * every known configuration to them, and re-broadcast their own configuration (if the server already knows it).
	 */
	private static void onPlayerJoin(ServerPlayerEntity player) {
		PlayerConfig joining = WildfireGender.getPlayerById(player.getUuid());
		if (joining != null) {
			WildfireSync.sendToAllClients(player, joining);
		}

		for (PlayerConfig config : new ArrayList<PlayerConfig>(WildfireGender.PLAYER_CACHE.values())) {
			if (!config.uuid.equals(player.getUuid())) {
				WildfireSync.sendToClient(player, config);
			}
		}
	}
}

package com.wildfire.main;

import com.wildfire.main.entitydata.PlayerConfig;
import com.wildfire.main.networking.WildfireSync;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.ornithemc.osl.entrypoints.api.ModInitializer;
import net.ornithemc.osl.networking.api.server.ServerConnectionEvents;

import java.util.ArrayList;

public class WildfireGenderServer implements ModInitializer {

	@Override
	public void init() {
		// While this class is named 'Server', this is actually a common code path, so we can safely register here
		// for both sides.
		WildfireSync.registerServer();

		ServerConnectionEvents.PLAY_READY.register(ctx -> {
			final ServerPlayerEntity player = ctx.player();

			// PLAY_READY is fired from OSL's handshake listener, which runs on the network thread. Move the
			// synchronisation work onto the server thread before touching player/config state or sending packets.
			ctx.server().execute(() -> {
				if (player.removed || player.networkHandler == null) return;

				try {
					onPlayerJoin(player);
				} catch (Exception e) {
					WildfireGender.LOGGER.warn("Failed to sync gender data to joining player {}", player.getName(), e);
				}
			});
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

		for (PlayerConfig config : new ArrayList<>(WildfireGender.PLAYER_CACHE.values())) {
			if (!config.uuid.equals(player.getUuid())) {
				WildfireSync.sendToClient(player, config);
			}
		}
	}
}

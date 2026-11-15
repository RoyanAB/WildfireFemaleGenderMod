package com.wildfire.main.networking;

import com.wildfire.main.WildfireGender;
import com.wildfire.main.entitydata.PlayerConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.living.player.PlayerEntity;
import net.ornithemc.osl.networking.api.client.ClientPlayNetworking;

/**
 * Client-only side of the mod's custom payload networking.
 */
@Environment(EnvType.CLIENT)
public final class WildfireSyncClient {

	private WildfireSyncClient() {
	}

	public static void registerClient() {
		ClientPlayNetworking.registerListener(WildfireSync.CHANNEL_SYNC, (ctx, data) -> {
			// OSL invokes packet listeners on the network thread; queue the packet onto the client thread before
			// touching configs or the player cache.
			ctx.ensureOnMainThread();

			try {
				PlayerEntity player = ctx.minecraft().player;
				if (player == null) return;

				new SyncToClientPacket(data).handle(player);
			} catch (Exception e) {
				WildfireGender.LOGGER.warn("Failed to handle gender sync packet", e);
			}
		});
	}

	/**
	 * Send the client player's configuration to the server for syncing to other players.
	 */
	public static void sendToServer(PlayerConfig plr) {
		if (plr == null || !plr.needsSync) return;
		if (!ClientPlayNetworking.isPlayReady(WildfireSync.CHANNEL_C2S)) return;

		final SyncToServerPacket packet;
		synchronized (plr) {
			// Re-check and clear under the same lock that PlayerConfig#loadCachedPlayer uses; otherwise a cache load
			// finishing between packet construction and the clearing below could be lost.
			if (!plr.needsSync) return;
			packet = new SyncToServerPacket(plr);
			plr.needsSync = false;
		}

		ClientPlayNetworking.send(WildfireSync.CHANNEL_C2S, buffer -> packet.write(buffer));
	}
}

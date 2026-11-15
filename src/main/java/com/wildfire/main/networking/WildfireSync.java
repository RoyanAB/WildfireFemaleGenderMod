package com.wildfire.main.networking;

import com.wildfire.main.WildfireGender;
import com.wildfire.main.entitydata.PlayerConfig;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.ornithemc.osl.core.api.util.NamespacedIdentifier;
import net.ornithemc.osl.networking.api.ChannelIdentifiers;
import net.ornithemc.osl.networking.api.server.ServerPlayNetworking;

/**
 * Common (server-safe) side of the mod's custom payload networking. The client-only half lives in
 * {@link WildfireSyncClient} so that this class can be loaded on dedicated servers, where client classes do not exist.
 */
public class WildfireSync {

	/**
	 * Channel used for server-to-client gender synchronisation.
	 */
	public static final NamespacedIdentifier CHANNEL_SYNC = ChannelIdentifiers.register(WildfireGender.MODID, "sync", true, false);
	/**
	 * Channel used for client-to-server gender synchronisation.
	 */
	public static final NamespacedIdentifier CHANNEL_C2S = ChannelIdentifiers.register(WildfireGender.MODID, "c2s", false, true);

	public static void registerServer() {
		ServerPlayNetworking.registerListener(CHANNEL_C2S, (ctx, data) -> {
			// OSL invokes packet listeners on the network thread. Re-scheduling is done by throwing
			// NotOnMainThreadException from ensureOnMainThread(); OSL then queues the packet onto the server thread.
			ctx.ensureOnMainThread();

			try {
				new SyncToServerPacket(data).handle(ctx.player());
			} catch (Exception e) {
				WildfireGender.LOGGER.warn("Failed to handle gender sync packet from {}", ctx.player().getName(), e);
			}
		});
	}

	/**
	 * Send a player's configuration to every other player on the server.
	 *
	 * <p>All worlds are visited instead of just the source player's world: clients keep configurations for players in
	 * other dimensions too, which means a player changing dimensions does not need a separate re-sync event. Each
	 * recipient gets its own packet buffer, because a 1.12.2 custom payload packet writes from (and therefore advances)
	 * the shared buffer when it is encoded.</p>
	 */
	public static void sendToAllClients(ServerPlayerEntity toSync, PlayerConfig playerConfig) {
		if (playerConfig == null || toSync.world == null) return;

		MinecraftServer server = toSync.world.getServer();
		if (server == null || server.worlds == null) return;

		final SyncToClientPacket syncPacket;
		synchronized (playerConfig) {
			syncPacket = new SyncToClientPacket(playerConfig);
		}
		for (ServerWorld world : server.worlds) {
			if (world == null) continue;

			for (PlayerEntity player : world.players) {
				if (player instanceof ServerPlayerEntity && !player.getUuid().equals(toSync.getUuid())) {
					sendPacket((ServerPlayerEntity) player, syncPacket);
				}
			}
		}
	}

	/**
	 * Send a player's configuration to one specific player.
	 */
	public static void sendToClient(ServerPlayerEntity sendTo, PlayerConfig toSync) {
		if (toSync == null) return;

		final SyncToClientPacket syncPacket;
		synchronized (toSync) {
			syncPacket = new SyncToClientPacket(toSync);
		}
		sendPacket(sendTo, syncPacket);
	}

	private static void sendPacket(ServerPlayerEntity sendTo, final SyncToClientPacket syncPacket) {
		ServerPlayNetworking.send(sendTo, CHANNEL_SYNC, buffer -> syncPacket.write(buffer));
	}
}

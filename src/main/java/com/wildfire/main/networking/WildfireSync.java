package com.wildfire.main.networking;

import java.util.ArrayList;
import java.util.List;

import com.wildfire.main.WildfireGender;
import com.wildfire.main.entitydata.PlayerConfig;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.ornithemc.osl.core.api.util.NamespacedIdentifier;
import net.ornithemc.osl.core.api.util.function.IOConsumer;
import net.ornithemc.osl.networking.api.ChannelIdentifiers;
import net.ornithemc.osl.networking.api.PacketBuffer;
import net.ornithemc.osl.networking.api.server.ServerPacketListener;
import net.ornithemc.osl.networking.api.server.ServerPlayNetworking;

/**
 * Common (server-safe) side of the mod's custom payload networking. The client-only half lives in
 * {@link WildfireSyncClient} so that this class can be loaded on dedicated servers, where client classes do not exist.
 */
public class WildfireSync {

	/** Channel used for server-to-client gender synchronisation. */
	public static final NamespacedIdentifier CHANNEL_SYNC = ChannelIdentifiers.register(WildfireGender.MODID, "sync", true, false);
	/** Channel used for client-to-server gender synchronisation. */
	public static final NamespacedIdentifier CHANNEL_C2S = ChannelIdentifiers.register(WildfireGender.MODID, "c2s", false, true);

	public static void registerServer() {
		ServerPlayNetworking.registerListener(CHANNEL_C2S, new ServerPacketListener.Buffer() {
			@Override
			public void handle(Context ctx, PacketBuffer data) {
				new SyncToServerPacket(data).handle(ctx.player());
			}
		});
	}

	/**
	 * Sync a player's configuration to all nearby connected players.
	 */
	public static void sendToAllClients(ServerPlayerEntity toSync, PlayerConfig playerConfig) {
		if (playerConfig == null || toSync.world == null || toSync.world.getServer() == null) return;

		final SyncToClientPacket syncPacket = new SyncToClientPacket(playerConfig);
		List<ServerPlayerEntity> targets = new ArrayList<ServerPlayerEntity>();
		for (PlayerEntity player : toSync.world.players) {
			if (player instanceof ServerPlayerEntity && !player.getUuid().equals(toSync.getUuid())) {
				targets.add((ServerPlayerEntity) player);
			}
		}
		for (ServerPlayerEntity sendTo : targets) {
			ServerPlayNetworking.send(sendTo, CHANNEL_SYNC, new IOConsumer<PacketBuffer>() {
				@Override
				public void accept(PacketBuffer buffer) {
					syncPacket.write(buffer);
				}
			});
		}
	}

	/**
	 * Sync a player's configuration to another connected player.
	 */
	public static void sendToClient(ServerPlayerEntity sendTo, PlayerConfig toSync) {
		if (toSync == null) return;
		final SyncToClientPacket syncPacket = new SyncToClientPacket(toSync);
		ServerPlayNetworking.send(sendTo, CHANNEL_SYNC, new IOConsumer<PacketBuffer>() {
			@Override
			public void accept(PacketBuffer buffer) {
				syncPacket.write(buffer);
			}
		});
	}
}

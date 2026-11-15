package com.wildfire.main.networking;

import com.wildfire.main.entitydata.PlayerConfig;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.living.player.PlayerEntity;
import net.ornithemc.osl.core.api.util.function.IOConsumer;
import net.ornithemc.osl.networking.api.PacketBuffer;
import net.ornithemc.osl.networking.api.client.ClientPacketListener;
import net.ornithemc.osl.networking.api.client.ClientPlayNetworking;

/**
 * Client-only side of the mod's custom payload networking.
 */
@Environment(EnvType.CLIENT)
public final class WildfireSyncClient {

	private WildfireSyncClient() {
	}

	public static void registerClient() {
		ClientPlayNetworking.registerListener(WildfireSync.CHANNEL_SYNC, new ClientPacketListener.Buffer() {
			@Override
			public void handle(Context ctx, PacketBuffer data) {
				PlayerEntity player = ctx.minecraft().player;
				if (player == null) return;
				new SyncToClientPacket(data).handle(player);
			}
		});
	}

	/**
	 * Send the client player's configuration to the server for syncing to other players.
	 */
	public static void sendToServer(PlayerConfig plr) {
		if (plr == null || !plr.needsSync) return;
		if (!ClientPlayNetworking.isPlayReady(WildfireSync.CHANNEL_C2S)) return;
		final SyncToServerPacket packet = new SyncToServerPacket(plr);
		ClientPlayNetworking.send(WildfireSync.CHANNEL_C2S, new IOConsumer<PacketBuffer>() {
			@Override
			public void accept(PacketBuffer buffer) {
				packet.write(buffer);
			}
		});
		plr.needsSync = false;
	}
}

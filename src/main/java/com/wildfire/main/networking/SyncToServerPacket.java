package com.wildfire.main.networking;

import com.wildfire.main.WildfireGender;
import com.wildfire.main.entitydata.PlayerConfig;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.ornithemc.osl.networking.api.PacketBuffer;

public class SyncToServerPacket extends SyncPacket {

	protected SyncToServerPacket(PlayerConfig plr) {
		super(plr);
	}

	protected SyncToServerPacket(PacketBuffer buffer) {
		super(buffer);
	}

	public void handle(ServerPlayerEntity player) {
		// Only accept data for the sending player; ignore spoofed UUIDs.
		if (player.removed || !player.getUuid().equals(uuid)) return;

		PlayerConfig plr = WildfireGender.getOrAddPlayerById(uuid);
		synchronized (plr) {
			updatePlayerFromPacket(plr);
		}
		WildfireSync.sendToAllClients(player, plr);
	}
}

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
		if (player.getUuid().equals(uuid)) {
			PlayerConfig plr = WildfireGender.getOrAddPlayerById(uuid);
			updatePlayerFromPacket(plr);
			WildfireSync.sendToAllClients(player, plr);
		}
	}
}

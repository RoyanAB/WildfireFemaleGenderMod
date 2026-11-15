package com.wildfire.main.networking;

import com.wildfire.main.WildfireGender;
import com.wildfire.main.entitydata.PlayerConfig;
import net.minecraft.entity.living.player.PlayerEntity;
import net.ornithemc.osl.networking.api.PacketBuffer;

public class SyncToClientPacket extends SyncPacket {

	protected SyncToClientPacket(PlayerConfig plr) {
		super(plr);
	}

	protected SyncToClientPacket(PacketBuffer buffer) {
		super(buffer);
	}

	public void handle(PlayerEntity player) {
		if (player.getUuid().equals(uuid)) return;

		PlayerConfig plr = WildfireGender.getOrAddPlayerById(uuid);
		// Keep the update and the status change atomic with respect to PlayerConfig#loadCachedPlayer, which may still
		// be reading the local cache on the IO thread.
		synchronized (plr) {
			updatePlayerFromPacket(plr);
			plr.syncStatus = PlayerConfig.SyncStatus.SYNCED;
		}
	}
}

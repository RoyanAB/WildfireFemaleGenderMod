package com.wildfire.main.networking;

import java.util.UUID;

import com.wildfire.main.Gender;
import com.wildfire.main.entitydata.Breasts;
import com.wildfire.main.entitydata.PlayerConfig;

import net.ornithemc.osl.networking.api.PacketBuffer;

abstract class SyncPacket {
	protected final UUID uuid;
	private final Gender gender;
	private final float bustSize;

	// physics variables
	private final boolean breastPhysics;
	private final boolean showInArmor;
	private final float bounceMultiplier;
	private final float floppyMultiplier;

	private final float xOffset;
	private final float yOffset;
	private final float zOffset;
	private final boolean uniboob;
	private final float cleavage;

	private final boolean hurtSounds;

	protected SyncPacket(PlayerConfig plr) {
		this.uuid = plr.uuid;
		this.gender = plr.getGender();
		this.bustSize = plr.getBustSize();
		this.hurtSounds = plr.hasHurtSounds();

		this.breastPhysics = plr.hasBreastPhysics();
		this.showInArmor = plr.showBreastsInArmor();
		this.bounceMultiplier = plr.getBounceMultiplier();
		this.floppyMultiplier = plr.getFloppiness();

		Breasts breasts = plr.getBreasts();
		this.xOffset = breasts.getXOffset();
		this.yOffset = breasts.getYOffset();
		this.zOffset = breasts.getZOffset();

		this.uniboob = breasts.isUniboob();
		this.cleavage = breasts.getCleavage();
	}

	protected SyncPacket(PacketBuffer buffer) {
		this.uuid = buffer.readUuid();
		this.gender = buffer.readEnum(Gender.class);
		this.bustSize = buffer.readFloat();
		this.hurtSounds = buffer.readBoolean();

		this.breastPhysics = buffer.readBoolean();
		this.showInArmor = buffer.readBoolean();
		this.bounceMultiplier = buffer.readFloat();
		this.floppyMultiplier = buffer.readFloat();

		this.xOffset = buffer.readFloat();
		this.yOffset = buffer.readFloat();
		this.zOffset = buffer.readFloat();
		this.uniboob = buffer.readBoolean();
		this.cleavage = buffer.readFloat();
	}

	public void write(PacketBuffer buffer) {
		buffer.writeUuid(this.uuid);
		buffer.writeEnum(this.gender);
		buffer.writeFloat(this.bustSize);
		buffer.writeBoolean(this.hurtSounds);
		buffer.writeBoolean(this.breastPhysics);
		buffer.writeBoolean(this.showInArmor);
		buffer.writeFloat(this.bounceMultiplier);
		buffer.writeFloat(this.floppyMultiplier);

		buffer.writeFloat(this.xOffset);
		buffer.writeFloat(this.yOffset);
		buffer.writeFloat(this.zOffset);
		buffer.writeBoolean(this.uniboob);
		buffer.writeFloat(this.cleavage);
	}

	protected void updatePlayerFromPacket(PlayerConfig plr) {
		plr.updateGender(gender);
		plr.updateBustSize(bustSize);
		plr.updateHurtSounds(hurtSounds);

		plr.updateBreastPhysics(breastPhysics);
		plr.updateShowBreastsInArmor(showInArmor);
		plr.updateBounceMultiplier(bounceMultiplier);
		plr.updateFloppiness(floppyMultiplier);

		Breasts breasts = plr.getBreasts();
		breasts.updateXOffset(xOffset);
		breasts.updateYOffset(yOffset);
		breasts.updateZOffset(zOffset);
		breasts.updateUniboob(uniboob);
		breasts.updateCleavage(cleavage);
	}
}

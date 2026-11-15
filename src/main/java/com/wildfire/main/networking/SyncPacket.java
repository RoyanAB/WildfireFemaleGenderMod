package com.wildfire.main.networking;

import com.wildfire.main.Gender;
import com.wildfire.main.entitydata.Breasts;
import com.wildfire.main.entitydata.PlayerConfig;
import net.ornithemc.osl.networking.api.PacketBuffer;

import java.util.UUID;

abstract class SyncPacket {
	protected final UUID uuid;
	private final Gender gender;
	private final float bustSize;

	// physics variables
	private final boolean breastPhysics;
	private final boolean showInArmor;
	private final boolean armorPhysicsOverride;
	private final float bounceMultiplier;
	private final float floppyMultiplier;

	private final float xOffset;
	private final float yOffset;
	private final float zOffset;
	private final boolean uniboob;
	private final float cleavage;

	protected SyncPacket(PlayerConfig plr) {
		this.uuid = plr.uuid;
		this.gender = plr.getGender();
		this.bustSize = plr.getBustSize();

		this.breastPhysics = plr.hasBreastPhysics();
		this.showInArmor = plr.showBreastsInArmor();
		this.armorPhysicsOverride = plr.getArmorPhysicsOverride();
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
		// Read the enum ordinal manually so a malformed packet cannot throw an ArrayIndexOutOfBoundsException.
		int genderOrdinal = buffer.readVarInt();
		Gender[] genders = Gender.values();
		this.gender = genderOrdinal >= 0 && genderOrdinal < genders.length ? genders[genderOrdinal] : Gender.MALE;
		this.bustSize = buffer.readFloat();

		this.breastPhysics = buffer.readBoolean();
		this.showInArmor = buffer.readBoolean();
		this.armorPhysicsOverride = buffer.readBoolean();
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
		buffer.writeVarInt(this.gender.ordinal());
		buffer.writeFloat(this.bustSize);
		buffer.writeBoolean(this.breastPhysics);
		buffer.writeBoolean(this.showInArmor);
		buffer.writeBoolean(this.armorPhysicsOverride);
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

		plr.updateBreastPhysics(breastPhysics);
		plr.updateShowBreastsInArmor(showInArmor);
		plr.updateArmorPhysicsOverride(armorPhysicsOverride);
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

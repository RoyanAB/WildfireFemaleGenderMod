package com.wildfire.main;

import net.minecraft.resource.Identifier;
import net.minecraft.sound.SoundEvent;

/**
 * Sound events added by the mod.
 *
 * <p>Minecraft 1.12.2 does not have a sound event registration API, so the event is inserted into the vanilla
 * {@link SoundEvent#REGISTRY} with the next free numeric id. Both sides of a connection run the same code in the same
 * order, so the chosen id is identical on the client and the server, which is required for {@code SoundEventS2CPacket}
 * to resolve the correct event.</p>
 *
 * <p>Note that {@link SoundEvent#getLocation()} is client-only in this version, so the identifier is kept around
 * separately instead of being read back from the sound event.</p>
 */
public class WildfireSounds {

	public static final Identifier FEMALE_HURT_ID = new Identifier(WildfireGender.MODID, "female_hurt");
	public static final SoundEvent FEMALE_HURT = new SoundEvent(FEMALE_HURT_ID);

	private static boolean registered = false;

	protected static synchronized void register() {
		if (registered) return;
		registered = true;

		if (SoundEvent.REGISTRY.get(FEMALE_HURT_ID) != null) {
			return;
		}

		int nextId = 0;
		for (SoundEvent event : SoundEvent.REGISTRY) {
			nextId = Math.max(nextId, SoundEvent.REGISTRY.getId(event) + 1);
		}
		SoundEvent.REGISTRY.register(nextId, FEMALE_HURT_ID, FEMALE_HURT);
	}
}

package com.wildfire.main;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer;

@Environment(EnvType.CLIENT)
public class WildfireGenderClient implements ClientModInitializer {

	@Override
	public void initClient() {
		WildfireEventHandler.registerClientEvents();
	}
}

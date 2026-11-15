package com.wildfire.gui.screen;

import java.util.UUID;

import com.wildfire.gui.WildfireButton;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.entitydata.PlayerConfig;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

@Environment(EnvType.CLIENT)
public abstract class BaseWildfireScreen extends Screen {

	protected final UUID playerUUID;
	protected final Screen parent;
	protected final String title;

	protected BaseWildfireScreen(String title, Screen parent, UUID uuid) {
		this.title = title;
		this.parent = parent;
		this.playerUUID = uuid;
	}

	public PlayerConfig getPlayer() {
		return WildfireGender.getPlayerById(this.playerUUID);
	}

	public String getTitle() {
		return this.title;
	}

	@Override
	public boolean shouldPauseGame() {
		return false;
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button instanceof WildfireButton) {
			((WildfireButton) button).runAction();
		}
	}

	protected void renderButtonTooltips(int mouseX, int mouseY) {
		for (ButtonWidget button : this.buttons) {
			if (!(button instanceof WildfireButton) || !button.visible) continue;
			WildfireButton wildfireButton = (WildfireButton) button;
			if (wildfireButton.getTooltip() == null) continue;
			if (mouseX >= button.x && mouseY >= button.y && mouseX < button.x + button.getWidth() && mouseY < button.y + wildfireButton.getHeight()) {
				String[] lines = wildfireButton.getTooltip().split("\n");
				java.util.List<String> tooltip = new java.util.ArrayList<String>();
				for (String line : lines) {
					tooltip.add(line);
				}
				this.renderTooltip(tooltip, mouseX, mouseY);
				break;
			}
		}
	}
}

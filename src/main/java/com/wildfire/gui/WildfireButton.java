package com.wildfire.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.platform.GlStateManager;

@Environment(EnvType.CLIENT)
public class WildfireButton extends ButtonWidget {

	private final Runnable onPress;
	private String tooltip;
	public boolean transparent = false;

	public WildfireButton(int x, int y, int w, int h, String text, Runnable onPress) {
		super(0, x, y, w, h, text);
		this.onPress = onPress;
	}

	public WildfireButton withTooltip(String tooltip) {
		this.tooltip = tooltip;
		return this;
	}

	public String getTooltip() {
		return tooltip;
	}

	public int getHeight() {
		return this.height;
	}

	public void runAction() {
		if (this.onPress != null) {
			this.onPress.run();
		}
	}

	@Override
	public void render(Minecraft minecraft, int mouseX, int mouseY, float tickDelta) {
		if (!this.visible) return;
		TextRenderer font = minecraft.textRenderer;
		this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
		GlStateManager.disableTexture();
		int clr = 0x444444 + (84 << 24);
		if (this.hovered) clr = 0x666666 + (84 << 24);
		if (!this.active) clr = 0x222222 + (84 << 24);
		if (!transparent) {
			fill(this.x, this.y, this.x + this.width, this.y + this.height, clr);
		}
		GlStateManager.enableTexture();

		int textColor = active ? 0xFFFFFF : 0x666666;
		int i = this.x + 2;
		int j = this.x + this.width - 2;
		GuiHelper.drawScrollableText(font, this.message, i, this.y, j, this.y + this.height, textColor);
		GlStateManager.color4f(1f, 1f, 1f, 1f);
	}

	public WildfireButton setTransparent(boolean b) {
		this.transparent = b;
		return this;
	}

	@Override
	public boolean mouseClicked(Minecraft minecraft, int mouseX, int mouseY) {
		return super.mouseClicked(minecraft, mouseX, mouseY);
	}
}

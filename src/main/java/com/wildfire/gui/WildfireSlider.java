package com.wildfire.gui;

import java.util.function.Consumer;
import java.util.function.Function;

import com.wildfire.main.config.FloatConfigKey;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class WildfireSlider extends ButtonWidget {

	private double value;
	private final double minValue;
	private final double maxValue;
	private final Consumer<Float> valueUpdate;
	private final Function<Float, String> messageUpdate;
	private final Consumer<Float> onSave;

	private float lastValue;
	private boolean changed;
	private boolean dragging;

	public WildfireSlider(int xPos, int yPos, int width, int height, FloatConfigKey config, double currentVal, Consumer<Float> valueUpdate,
			Function<Float, String> messageUpdate, Consumer<Float> onSave) {
		this(xPos, yPos, width, height, config.getMinInclusive(), config.getMaxInclusive(), currentVal, valueUpdate, messageUpdate, onSave);
	}

	public WildfireSlider(int xPos, int yPos, int width, int height, double minVal, double maxVal, double currentVal, Consumer<Float> valueUpdate,
			Function<Float, String> messageUpdate, Consumer<Float> onSave) {
		super(0, xPos, yPos, width, height, "");
		this.minValue = minVal;
		this.maxValue = maxVal;
		this.valueUpdate = valueUpdate;
		this.messageUpdate = messageUpdate;
		this.onSave = onSave;
		setValueInternal(currentVal);
	}

	protected void applyValue() {
		float newValue = getFloatValue();
		if (lastValue != newValue) {
			valueUpdate.accept(newValue);
			lastValue = newValue;
			changed = true;
		}
	}

	public void save() {
		if (changed) {
			onSave.accept(lastValue);
			changed = false;
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY) {
		this.dragging = false;
		save();
	}

	@Override
	public boolean mouseClicked(Minecraft minecraft, int mouseX, int mouseY) {
		if (super.mouseClicked(minecraft, mouseX, mouseY)) {
			this.dragging = true;
			this.setValueFromMouse(mouseX);
			return true;
		}
		return false;
	}

	@Override
	public void render(Minecraft minecraft, int mouseX, int mouseY, float delta) {
		if (!this.visible) return;

		this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
		if (this.dragging) {
			this.setValueFromMouse(mouseX);
		}

		GlStateManager.disableDepthTest();
		GlStateManager.disableTexture();
		int xP = this.x + 2;
		fill(xP - 2, this.y, this.x + this.width - 1, this.y + this.height, 0x222222 + (128 << 24));
		int xPos = this.x + 2 + (int) (this.value * (float) (this.width - 3));
		fill(this.x + 1, this.y + 1, xPos - 1, this.y + this.height - 1, 0x222266 + (180 << 24));

		int xPos2 = this.x + 3 + (int) (this.value * (float) (this.width - 5));
		fill(xPos2 - 2, this.y + 1, xPos2, this.y + this.height - 1, 0xFFFFFF + (120 << 24));
		GlStateManager.enableTexture();
		GlStateManager.enableDepthTest();

		TextRenderer font = minecraft.textRenderer;
		String message = this.messageUpdate.apply((float) getValue());
		int i = this.x + 2;
		int j = this.x + this.width - 2;
		GuiHelper.drawScrollableText(font, message, i, this.y, j, this.y + this.height, this.hovered || changed ? 0xFFFF55 : 0xFFFFFF);
	}

	public float getFloatValue() {
		return (float) getValue();
	}

	public double getValue() {
		return this.value * (maxValue - minValue) + minValue;
	}

	public void setValue(double value) {
		setValueInternal(value);
		applyValue();
	}

	private void setValueInternal(double value) {
		this.value = MathHelper.clamp((value - this.minValue) / (this.maxValue - this.minValue), 0, 1);
		this.lastValue = (float) value;
	}

	private void setValueFromMouse(double mouseX) {
		this.value = (mouseX - (double) (this.x + 4)) / (double) (this.width - 8);
		if (this.value < 0.0F) {
			this.value = 0.0F;
		}
		if (this.value > 1.0F) {
			this.value = 1.0F;
		}
		applyValue();
	}
}

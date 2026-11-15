package com.wildfire.gui;

import com.wildfire.main.Gender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Formatting;
import org.lwjgl.opengl.GL11;

/**
 * Client-only drawing helpers. Kept separate from {@link com.wildfire.main.WildfireHelper} so that the common code
 * never touches client-only classes.
 */
@Environment(EnvType.CLIENT)
public class GuiHelper {

	/**
	 * @return the localized, color formatted display name of the given gender.
	 */
	public static String getGenderDisplayName(Gender gender) {
		return gender.getColor() + I18n.translate(gender.getTranslationKey()) + Formatting.RESET;
	}

	private static void enableTextBlend() {
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
	}

	public static void drawCenteredText(TextRenderer textRenderer, String text, int x, int y, int color) {
		int centeredX = x - textRenderer.getWidth(text) / 2;
		enableTextBlend();
		textRenderer.draw(text, centeredX, y, color, false);
		GlStateManager.disableBlend();
	}

	public static void drawText(TextRenderer textRenderer, String text, int x, int y, int color) {
		enableTextBlend();
		textRenderer.draw(text, x, y, color, false);
		GlStateManager.disableBlend();
	}

	public static void drawTextWithShadow(TextRenderer textRenderer, String text, int x, int y, int color) {
		enableTextBlend();
		textRenderer.draw(text, x, y, color, true);
		GlStateManager.disableBlend();
	}

	public static void drawScrollableText(TextRenderer textRenderer, String text, int left, int top, int right, int bottom, int color) {
		int i = textRenderer.getWidth(text);
		int j = (top + bottom - 9) / 2 + 1;
		int k = right - left;
		if (i > k) {
			int l = i - k;
			double d = (double) Minecraft.getTime() / 1000.0;
			double e = Math.max((double) l * 0.5, 3.0);
			double f = Math.sin(1.5707963267948966 * Math.cos(6.283185307179586 * d / e)) / 2.0 + 0.5;
			double g = lerp(f, 0.0, l);
			enableScissor(left, top, right, bottom);
			enableTextBlend();
			textRenderer.draw(text, (float) (left - (int) g), (float) j, color, false);
			GlStateManager.disableBlend();
			disableScissor();
		} else {
			drawCenteredText(textRenderer, text, (left + right) / 2, j, color);
		}
	}

	private static double lerp(double delta, double start, double end) {
		return start + delta * (end - start);
	}

	private static void enableScissor(int left, int top, int right, int bottom) {
		Minecraft minecraft = Minecraft.getInstance();
		int guiWidth = minecraft.screen != null ? minecraft.screen.width : minecraft.width;
		int guiHeight = minecraft.screen != null ? minecraft.screen.height : minecraft.height;
		double factorX = (double) minecraft.width / (double) Math.max(1, guiWidth);
		double factorY = (double) minecraft.height / (double) Math.max(1, guiHeight);
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor((int) (left * factorX), (int) ((guiHeight - bottom) * factorY),
			(int) ((right - left) * factorX), (int) ((bottom - top) * factorY));
	}

	private static void disableScissor() {
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}
}

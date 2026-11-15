package com.wildfire.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.platform.GLX;
import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.entity.living.LivingEntity;

/**
 * Helper for drawing the 3D player preview used by the wardrobe screens.
 *
 * <p>In 1.20.2 the preview passes a hardcoded full-bright light value to the entity renderer.
 * In 1.12.2 the GUI entity renderer overload does not set the lightmap texture coordinates at all,
 * so the preview would reuse whatever lightmap coordinate happened to be left over from the world
 * (usually the player's world light level, which makes the preview dark at night or in caves).
 * We bind the lightmap and set the full-bright coordinate for the duration of the preview, then
 * restore the GUI state; this matches the way vanilla renders items in the inventory.</p>
 */
@Environment(EnvType.CLIENT)
public final class EntityPreview {

	private EntityPreview() {
	}

	public static void render(int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.gameRenderer.enableLightMap();
		GLX.multiTexCoord2f(GLX.GL_TEXTURE1, 240.0F, 240.0F);
		try {
			net.minecraft.client.gui.screen.game.inventory.SurvivalInventoryScreen.renderEntity(x, y, size, mouseX, mouseY, entity);
		} finally {
			minecraft.gameRenderer.disableLightMap();
			GlStateManager.color4f(1f, 1f, 1f, 1f);
		}
	}
}

package com.wildfire.render;

import com.wildfire.main.WildfireGender;
import com.wildfire.main.entitydata.EntityConfig;
import com.wildfire.render.WildfireModelRenderer.BreastModelBox;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.living.player.ClientPlayerEntity;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.client.render.model.PlayerModelPart;
import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.living.ArmorStandEntity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.resource.Identifier;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class GenderArmorLayer<T extends LivingEntity> extends GenderLayer<T> {

	private static final Identifier ENCHANTMENT_GLINT_TEXTURE = new Identifier("textures/misc/enchanted_item_glint.png");

	protected final BreastModelBox lBoobArmor, rBoobArmor;
	private EntityConfig entityConfig;

	public GenderArmorLayer(LivingEntityRenderer<?> render) {
		super(render);
		lBoobArmor = new BreastModelBox(64, 32, 16, 17, -4F, 0.0F, 0F, 4, 5, 3, 0.0F, false);
		rBoobArmor = new BreastModelBox(64, 32, 20, 17, 0, 0.0F, 0F, 4, 5, 3, 0.0F, false);
	}

	@Override
	public void render(T ent, float walkAnimationProgress, float walkAnimationSpeed, float tickDelta, float bob, float yaw, float pitch, float scale) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null) {
			return;
		}

		if (ent.getEquipment(EquipmentSlot.CHEST).isEmpty()) return;

		try {
			entityConfig = getConfig(ent);
			if (entityConfig == null) return;

			if (!setupRender(ent, entityConfig, tickDelta)) return;
			if (ent instanceof ArmorStandEntity && !genderArmor.armorStandsCopySettings()) return;
			ModelPart body = getBody();

			GlStateManager.pushMatrix();
			try {
				setupTransformations(ent, body, BreastSide.LEFT, scale);
				renderBreastArmor(ent, BreastSide.LEFT, tickDelta);
			} finally {
				GlStateManager.popMatrix();
			}

			GlStateManager.pushMatrix();
			try {
				setupTransformations(ent, body, BreastSide.RIGHT, scale);
				renderBreastArmor(ent, BreastSide.RIGHT, tickDelta);
			} finally {
				GlStateManager.popMatrix();
			}
		} catch (Exception e) {
			WildfireGender.LOGGER.error("Failed to render breast armor", e);
		}
	}

	@Override
	protected void setupTransformations(T entity, ModelPart body, BreastSide side, float scale) {
		super.setupTransformations(entity, body, side, scale);
		if ((entity instanceof ClientPlayerEntity && ((ClientPlayerEntity) entity).isModelPartVisible(PlayerModelPart.JACKET))
			|| (entity instanceof ArmorStandEntity && entityConfig != null && entityConfig.hasJacketLayer())) {
			GlStateManager.translatef(0, 0, -0.015f);
			GlStateManager.scalef(1.05f, 1.05f, 1.05f);
		}
	}

	protected void renderBreastArmor(T entity, BreastSide side, float partialTicks) {
		if (armorStack.isEmpty() || !(armorStack.getItem() instanceof ArmorItem)) return;

		ArmorItem armorItem = (ArmorItem) armorStack.getItem();
		Identifier armorTexture = getArmorResource(armorItem);
		float armorR = 1f, armorG = 1f, armorB = 1f;
		if (armorItem.getTier() == ArmorItem.Tier.CLOTH) {
			int color = armorItem.getColor(armorStack);
			armorR = (float) (color >> 16 & 255) / 255.0F;
			armorG = (float) (color >> 8 & 255) / 255.0F;
			armorB = (float) (color & 255) / 255.0F;
		}

		GlStateManager.pushMatrix();
		try {
			GlStateManager.translatef(side == BreastSide.LEFT ? 0.001f : -0.001f, 0.015f, -0.015f);
			GlStateManager.scalef(1.05f, 1, 1);
			BreastModelBox armor = side == BreastSide.LEFT ? lBoobArmor : rBoobArmor;
			Minecraft.getInstance().getTextureManager().bind(armorTexture);
			renderBox(armor, armorR, armorG, armorB, 1f);

			if (armorStack.hasEnchantments()) {
				renderArmorGlint(entity, partialTicks, side);
			}
		} finally {
			GlStateManager.color4f(1f, 1f, 1f, 1f);
			GlStateManager.popMatrix();
		}
	}

	private Identifier getArmorResource(@NotNull ArmorItem item) {
		return new Identifier("textures/models/armor/" + item.getTier().getKey() + "_layer_1.png");
	}

	/**
	 * Renders the enchantment glint over the breast armor. The logic mirrors vanilla's
	 * {@code AbstractArmorLayer#renderEnchantmentGlint}, but draws our custom boxes instead of a model.
	 */
	private void renderArmorGlint(T entity, float tickDelta, BreastSide side) {
		float f = entity.ticks + tickDelta;
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.getTextureManager().bind(ENCHANTMENT_GLINT_TEXTURE);
		minecraft.gameRenderer.resetFogColor(true);
		GlStateManager.enableBlend();
		GlStateManager.depthFunc(GL11.GL_EQUAL);
		GlStateManager.depthMask(false);

		BreastModelBox armor = side == BreastSide.LEFT ? lBoobArmor : rBoobArmor;
		for (int i = 0; i < 2; i++) {
			GlStateManager.disableLighting();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			GlStateManager.scalef(0.33333334F, 0.33333334F, 0.33333334F);
			GlStateManager.rotatef(30.0F - i * 60.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translatef(0.0F, f * (0.001F + i * 0.003F) * 20.0F, 0.0F);
			GlStateManager.matrixMode(5888);
			renderBox(armor, 0.38F, 0.19F, 0.608F, 1f);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		}

		GlStateManager.matrixMode(5890);
		GlStateManager.loadIdentity();
		GlStateManager.matrixMode(5888);
		GlStateManager.enableLighting();
		GlStateManager.depthMask(true);
		GlStateManager.depthFunc(GL11.GL_LEQUAL);
		GlStateManager.disableBlend();
		GlStateManager.color4f(1f, 1f, 1f, 1f);
		minecraft.gameRenderer.resetFogColor(false);
	}
}

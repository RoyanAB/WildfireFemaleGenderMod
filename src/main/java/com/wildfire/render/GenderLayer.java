package com.wildfire.render;

import com.wildfire.api.IGenderArmor;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.WildfireHelper;
import com.wildfire.main.entitydata.Breasts;
import com.wildfire.main.entitydata.EntityConfig;
import com.wildfire.physics.BreastPhysics;
import com.wildfire.render.WildfireModelRenderer.BreastModelBox;
import com.wildfire.render.WildfireModelRenderer.OverlayModelBox;
import com.wildfire.render.WildfireModelRenderer.PositionTextureVertex;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.living.player.ClientPlayerEntity;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.layer.EntityRenderLayer;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.client.render.model.PlayerModelPart;
import net.minecraft.client.render.model.entity.HumanoidModel;
import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.client.render.texture.TextureManager;
import net.minecraft.client.render.vertex.BufferBuilder;
import net.minecraft.client.render.vertex.DefaultVertexFormat;
import net.minecraft.client.render.vertex.Tesselator;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ConcurrentModificationException;

@Environment(EnvType.CLIENT)
public class GenderLayer<T extends LivingEntity> implements EntityRenderLayer<T> {

	protected BreastModelBox lBreast, rBreast;
	protected final OverlayModelBox lBreastWear, rBreastWear;

	protected float preBreastSize = 0f;
	protected Breasts breasts;
	protected ItemStack armorStack;
	protected IGenderArmor genderArmor;
	protected boolean isChestplateOccupied, bounceEnabled, breathingAnimation;
	protected float breastOffsetX, breastOffsetY, breastOffsetZ, lPhysPositionY, lPhysPositionX, rPhysPositionY, rTotalX,
		lPhysBounceRotation, rPhysBounceRotation, breastSize, zOffset, outwardAngle;

	protected final LivingEntityRenderer<?> parent;

	public GenderLayer(LivingEntityRenderer<?> parent) {
		this.parent = parent;
		lBreast = new BreastModelBox(64, 64, 16, 17, -4F, 0.0F, 0F, 4, 5, 4, 0.0F, false);
		rBreast = new BreastModelBox(64, 64, 20, 17, 0, 0.0F, 0F, 4, 5, 4, 0.0F, false);
		lBreastWear = new OverlayModelBox(true, 64, 64, 17, 34, -4F, 0.0F, 0F, 4, 5, 3, 0.0F, false);
		rBreastWear = new OverlayModelBox(false, 64, 64, 21, 34, 0, 0.0F, 0F, 4, 5, 3, 0.0F, false);
	}

	protected @Nullable Identifier getTexture(T entity) {
		if (entity instanceof ClientPlayerEntity) {
			return ((ClientPlayerEntity) entity).getSkinTextureLocation();
		}
		return null;
	}

	/**
	 * @return whether the entity itself (not its armor) is visible to the local player, and should therefore have its
	 * breast layer rendered.
	 */
	protected boolean isEntityVisible(T entity) {
		Minecraft mc = Minecraft.getInstance();
		if (!entity.isInvisible()) return true;
		if (mc.player == null) return false;
		if (!entity.isInvisibleTo(mc.player)) return true;
		return entity.isGlowing();
	}

	protected @Nullable EntityConfig getConfig(T entity) {
		try {
			return EntityConfig.getEntity(entity);
		} catch (ConcurrentModificationException e) {
			return null;
		}
	}

	@Override
	public boolean colorsWhenDamaged() {
		return false;
	}

	@Override
	public void render(T ent, float walkAnimationProgress, float walkAnimationSpeed, float tickDelta, float bob, float yaw, float pitch, float scale) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null) {
			return;
		}

		EntityConfig entityConfig = getConfig(ent);
		if (entityConfig == null) return;

		try {
			if (!setupRender(ent, entityConfig, tickDelta)) return;
			ModelPart body = getBody();

			GlStateManager.pushMatrix();
			try {
				setupTransformations(ent, body, BreastSide.LEFT, scale);
				renderBreast(ent, BreastSide.LEFT, scale);
			} finally {
				GlStateManager.popMatrix();
			}

			GlStateManager.pushMatrix();
			try {
				setupTransformations(ent, body, BreastSide.RIGHT, scale);
				renderBreast(ent, BreastSide.RIGHT, scale);
			} finally {
				GlStateManager.popMatrix();
			}
		} catch (Exception e) {
			WildfireGender.LOGGER.error("Failed to render breast layer", e);
		}
	}

	/**
	 * Common logic for setting up breast rendering.
	 *
	 * @return {@code true} if rendering should continue
	 */
	protected boolean setupRender(T entity, EntityConfig entityConfig, float partialTicks) {
		if (entity.isBaby()) return false;

		armorStack = entity.getEquipment(EquipmentSlot.CHEST);
		genderArmor = WildfireHelper.getArmorConfig(armorStack);
		isChestplateOccupied = genderArmor.coversBreasts() && !entityConfig.getArmorPhysicsOverride();
		if (genderArmor.alwaysHidesBreasts() || (!entityConfig.showBreastsInArmor() && isChestplateOccupied)) {
			return false;
		}

		if (!isEntityVisible(entity) && !isChestplateOccupied) {
			return false;
		}

		breasts = entityConfig.getBreasts();
		breastOffsetX = Math.round((Math.round(breasts.getXOffset() * 100f) / 100f) * 10) / 10f;
		breastOffsetY = -Math.round((Math.round(breasts.getYOffset() * 100f) / 100f) * 10) / 10f;
		breastOffsetZ = -Math.round((Math.round(breasts.getZOffset() * 100f) / 100f) * 10) / 10f;

		BreastPhysics leftBreastPhysics = entityConfig.getLeftBreastPhysics();
		final float bSize = leftBreastPhysics.getBreastSize(partialTicks);
		outwardAngle = (Math.round(breasts.getCleavage() * 100f) / 100f) * 100f;
		outwardAngle = Math.min(outwardAngle, 10);

		float reducer = -1;
		if (bSize < 0.84f) reducer++;
		if (bSize < 0.72f) reducer++;

		if (preBreastSize != bSize) {
			lBreast = new BreastModelBox(64, 64, 16, 17, -4F, 0.0F, 0F, 4, 5, (int) (4 - breastOffsetZ - reducer), 0.0F, false);
			rBreast = new BreastModelBox(64, 64, 20, 17, 0, 0.0F, 0F, 4, 5, (int) (4 - breastOffsetZ - reducer), 0.0F, false);
			preBreastSize = bSize;
		}

		lPhysPositionY = (float) MathHelper.clampedLerp(leftBreastPhysics.getPrePositionY(), leftBreastPhysics.getPositionY(), partialTicks);
		lPhysPositionX = (float) MathHelper.clampedLerp(leftBreastPhysics.getPrePositionX(), leftBreastPhysics.getPositionX(), partialTicks);
		lPhysBounceRotation = (float) MathHelper.clampedLerp(leftBreastPhysics.getPreBounceRotation(), leftBreastPhysics.getBounceRotation(), partialTicks);
		if (breasts.isUniboob()) {
			rPhysPositionY = lPhysPositionY;
			rTotalX = lPhysPositionX;
			rPhysBounceRotation = lPhysBounceRotation;
		} else {
			BreastPhysics rightBreastPhysics = entityConfig.getRightBreastPhysics();
			rPhysPositionY = (float) MathHelper.clampedLerp(rightBreastPhysics.getPrePositionY(), rightBreastPhysics.getPositionY(), partialTicks);
			rTotalX = (float) MathHelper.clampedLerp(rightBreastPhysics.getPrePositionX(), rightBreastPhysics.getPositionX(), partialTicks);
			rPhysBounceRotation = (float) MathHelper.clampedLerp(rightBreastPhysics.getPreBounceRotation(), rightBreastPhysics.getBounceRotation(), partialTicks);
		}
		breastSize = bSize * 1.5f;
		if (breastSize > 0.7f) breastSize = 0.7f;
		if (bSize > 0.7f) breastSize = bSize;
		if (breastSize < 0.02f) return false;

		zOffset = 0.0625f - (bSize * 0.0625f);
		breastSize = bSize + 0.5f * Math.abs(bSize - 0.7f) * 2f;

		float resistance = MathHelper.clamp(genderArmor.physicsResistance(), 0, 1);
		breathingAnimation = (entityConfig.getArmorPhysicsOverride() || resistance <= 0.5F)
			&& (!entity.isInWater() || entity.hasStatusEffect(StatusEffects.WATER_BREATHING));
		bounceEnabled = entityConfig.hasBreastPhysics() && (!isChestplateOccupied || resistance < 1);
		return true;
	}

	protected ModelPart getBody() {
		return ((HumanoidModel) parent.getModel()).body;
	}

	protected void setupTransformations(T entity, ModelPart body, BreastSide side, float scale) {
		boolean left = side == BreastSide.LEFT;
		// In 1.12.2 HumanoidModel#render applies the crouching offset to the model itself (and pops the matrix
		// before feature layers run), unlike 1.20 where the offset is part of the entity matrix. Replicate it here
		// so the breast layer stays attached to the body while sneaking.
		if (entity.isSneaking()) {
			GlStateManager.translatef(0.0F, 0.2F, 0.0F);
		}
		GlStateManager.translatef(body.x * 0.0625f, body.y * 0.0625f, body.z * 0.0625f);
		if (body.rotationZ != 0.0F) {
			GlStateManager.rotatef((float) Math.toDegrees(body.rotationZ), 0.0F, 0.0F, 1.0F);
		}
		if (body.rotationY != 0.0F) {
			GlStateManager.rotatef((float) Math.toDegrees(body.rotationY), 0.0F, 1.0F, 0.0F);
		}
		if (body.rotationX != 0.0F) {
			GlStateManager.rotatef((float) Math.toDegrees(body.rotationX), 1.0F, 0.0F, 0.0F);
		}

		if (bounceEnabled) {
			GlStateManager.translatef((left ? lPhysPositionX : rTotalX) / 32f, 0, 0);
			GlStateManager.translatef(0, (left ? lPhysPositionY : rPhysPositionY) / 32f, 0);
		}

		GlStateManager.translatef((left ? breastOffsetX : -breastOffsetX) * 0.0625f, 0.05625f + (breastOffsetY * 0.0625f),
			zOffset - 0.0625f * 2f + (breastOffsetZ * 0.0625f));

		if (!breasts.isUniboob()) {
			GlStateManager.translatef(-0.0625f * 2 * (left ? 1 : -1), 0, 0);
		}
		if (bounceEnabled) {
			GlStateManager.rotatef((left ? lPhysBounceRotation : rPhysBounceRotation), 0.0F, 1.0F, 0.0F);
		}
		if (!breasts.isUniboob()) {
			GlStateManager.translatef(0.0625f * 2 * (left ? 1 : -1), 0, 0);
		}

		float rotationMultiplier = 0;
		if (bounceEnabled) {
			GlStateManager.translatef(0, -0.035f * breastSize, 0);
			rotationMultiplier = -(left ? lPhysPositionY : rPhysPositionY) / 12f;
		}
		float totalRotation = breastSize + rotationMultiplier;
		if (!bounceEnabled) {
			totalRotation = breastSize;
		}
		if (totalRotation > breastSize + 0.2F) {
			totalRotation = breastSize + 0.2F;
		}
		totalRotation = Math.min(totalRotation, 1);

		if (isChestplateOccupied) {
			GlStateManager.translatef(0, 0, 0.01f);
		}

		GlStateManager.rotatef((left ? outwardAngle : -outwardAngle), 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(-35f * totalRotation, 1.0F, 0.0F, 0.0F);

		if (breathingAnimation) {
			float f5 = -MathHelper.cos(entity.ticks * 0.09F) * 0.45F + 0.45F;
			GlStateManager.rotatef(f5, 1.0F, 0.0F, 0.0F);
		}

		GlStateManager.scalef(0.9995f, 1f, 1f);
	}

	protected void renderBreast(T entity, BreastSide side, float scale) {
		if (!isEntityVisible(entity)) return;

		Identifier texture = getTexture(entity);
		if (texture == null) return;

		TextureManager textureManager = Minecraft.getInstance().getTextureManager();
		textureManager.bind(texture);
		float alpha = entity.isInvisible() ? 0.15F : 1;
		renderBox(side == BreastSide.LEFT ? lBreast : rBreast, 1f, 1f, 1f, alpha);
		if (entity instanceof ClientPlayerEntity && ((ClientPlayerEntity) entity).isModelPartVisible(PlayerModelPart.JACKET)) {
			GlStateManager.translatef(0, 0, -0.015f);
			GlStateManager.scalef(1.05f, 1.05f, 1.05f);
			renderBox(side == BreastSide.LEFT ? lBreastWear : rBreastWear, 1f, 1f, 1f, alpha);
		}
		GlStateManager.color4f(1f, 1f, 1f, 1f);
	}

	protected static void renderBox(WildfireModelRenderer.ModelBox model, float red, float green, float blue, float alpha) {
		GlStateManager.color4f(red, green, blue, alpha);
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder buffer = tesselator.getBuffer();
		buffer.begin(7, DefaultVertexFormat.POSITION_TEX_NORMAL);
		for (WildfireModelRenderer.TexturedQuad quad : model.quads) {
			if (quad == null) continue;
			for (PositionTextureVertex vertex : quad.vertexPositions) {
				buffer.vertex(vertex.x * 0.0625F, vertex.y * 0.0625F, vertex.z * 0.0625F)
					.texture(vertex.texturePositionX, vertex.texturePositionY)
					.normal(quad.normalX, quad.normalY, quad.normalZ)
					.nextVertex();
			}
		}
		tesselator.end();
	}
}

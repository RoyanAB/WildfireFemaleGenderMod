package com.wildfire.physics;

import com.wildfire.api.IGenderArmor;
import com.wildfire.main.WildfireHelper;
import com.wildfire.main.entitydata.EntityConfig;
import net.minecraft.entity.living.ArmorStandEntity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.passive.animal.HorseEntity;
import net.minecraft.entity.living.mob.passive.animal.PigEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class BreastPhysics {

	//X-Axis
	private float bounceVelX = 0;
	private final float targetBounceX = 0;
	private float velocityX = 0;
	private float positionX;
	private float prePositionX;
	//Y-Axis
	private float bounceVel = 0, targetBounceY = 0, velocity = 0, positionY, prePositionY;
	//Rotation
	private float bounceRotVel = 0, targetRotVel = 0, rotVelocity = 0, wfg_bounceRotation, wfg_preBounceRotation;

	private boolean justSneaking = false, alreadySleeping = false;

	private float breastSize = 0, preBreastSize = 0;

	private Vec3d prePos;
	private final EntityConfig entityConfig;

	public BreastPhysics(EntityConfig entityConfig) {
		this.entityConfig = entityConfig;
	}

	private int randomB = 1;
	private boolean alreadyFalling = false;

	public void update(LivingEntity entity, IGenderArmor armor) {
		if (entity instanceof ArmorStandEntity && !armor.armorStandsCopySettings()) {
			return;
		}

		this.prePositionY = this.positionY;
		this.prePositionX = this.positionX;
		this.wfg_preBounceRotation = this.wfg_bounceRotation;
		this.preBreastSize = this.breastSize;

		Vec3d entityPos = new Vec3d(entity.x, entity.y, entity.z);
		if (this.prePos == null) {
			this.prePos = entityPos;
			return;
		}

		float breastWeight = entityConfig.getBustSize() * 1.25f;
		float targetBreastSize = entityConfig.getBustSize();

		if (!entityConfig.getGender().canHaveBreasts()) {
			targetBreastSize = 0;
		} else {
			float tightness = MathHelper.clamp(armor.tightness(), 0, 1);
			if (entityConfig.getArmorPhysicsOverride()) tightness = 0;
			targetBreastSize *= 1 - 0.15F * tightness;
		}

		breastSize += (breastSize < targetBreastSize) ? Math.abs(breastSize - targetBreastSize) / 2f
			: -Math.abs(breastSize - targetBreastSize) / 2f;

		Vec3d motion = entityPos.subtract(this.prePos);
		this.prePos = entityPos;

		float bounceIntensity = (targetBreastSize * 3f) * Math.round((entityConfig.getBounceMultiplier() * 3) * 100) / 100f;
		float resistance = MathHelper.clamp(armor.physicsResistance(), 0, 1);
		if (entityConfig.getArmorPhysicsOverride()) resistance = 0;

		bounceIntensity *= 1 - resistance;

		if (!entityConfig.getBreasts().isUniboob()) {
			bounceIntensity = bounceIntensity * WildfireHelper.randFloat(0.5f, 1.5f);
		}
		if (entity.fallDistance > 0 && !alreadyFalling) {
			randomB = entity.world.random.nextBoolean() ? -1 : 1;
			alreadyFalling = true;
		}
		if (entity.fallDistance == 0) alreadyFalling = false;

		this.targetBounceY = (float) motion.y * bounceIntensity;
		this.targetBounceY += breastWeight;
		this.targetRotVel = -((entity.bodyYaw - entity.lastBodyYaw) / 15f) * bounceIntensity;

		float f2 = (float) (entity.velocityX * entity.velocityX + entity.velocityY * entity.velocityY + entity.velocityZ * entity.velocityZ) / 0.2F;
		f2 = f2 * f2 * f2;
		if (f2 < 1.0F) f2 = 1.0F;

		this.targetBounceY += MathHelper.cos(entity.walkAnimationProgress * 0.6662F + (float) Math.PI) * 0.5F
			* entity.walkAnimationSpeed * 0.5F / f2;

		this.targetRotVel += (float) motion.y * bounceIntensity * randomB;

		if (entity.isSneaking() && !this.justSneaking) {
			this.justSneaking = true;
			this.targetBounceY += bounceIntensity;
		}
		if (!entity.isSneaking() && this.justSneaking) {
			this.justSneaking = false;
			this.targetBounceY += bounceIntensity;
		}

		if (entity.getMount() != null) {
			if (entity.getMount() instanceof BoatEntity) {
				BoatEntity boat = (BoatEntity) entity.getMount();
				int rowTime = (int) boat.interpolatePaddlePhase(0, entity.walkAnimationProgress);
				int rowTime2 = (int) boat.interpolatePaddlePhase(1, entity.walkAnimationProgress);

				float rotationL = (float) MathHelper.clampedLerp(-(float) Math.PI / 3F, -0.2617994F, (MathHelper.sin(-rowTime2) + 1.0F) / 2.0F);
				float rotationR = (float) MathHelper.clampedLerp(-(float) Math.PI / 4F, (float) Math.PI / 4F, (MathHelper.sin(-rowTime + 1.0F) + 1.0F) / 2.0F);
				if (rotationL < -1 || rotationR < -0.6f) {
					this.targetBounceY = bounceIntensity / 3.25f;
				}
			}

			if (entity.getMount() instanceof MinecartEntity) {
				MinecartEntity cart = (MinecartEntity) entity.getMount();
				float speed = (float) (cart.velocityX * cart.velocityX + cart.velocityY * cart.velocityY + cart.velocityZ * cart.velocityZ);
				if (Math.random() * speed < 0.5f && speed > 0.2f) {
					this.targetBounceY = (Math.random() > 0.5 ? -bounceIntensity : bounceIntensity) / 6f;
				}
			}
			if (entity.getMount() instanceof HorseEntity) {
				HorseEntity horse = (HorseEntity) entity.getMount();
				float movement = (float) (horse.velocityX * horse.velocityX + horse.velocityY * horse.velocityY + horse.velocityZ * horse.velocityZ);
				if (horse.ticks % clampMovement(movement) == 5 && movement > 0.1f) {
					this.targetBounceY = bounceIntensity / 4f;
				}
			}
			if (entity.getMount() instanceof PigEntity) {
				PigEntity pig = (PigEntity) entity.getMount();
				float movement = (float) (pig.velocityX * pig.velocityX + pig.velocityY * pig.velocityY + pig.velocityZ * pig.velocityZ);
				if (pig.ticks % clampMovement(movement) == 5 && movement > 0.08f) {
					this.targetBounceY = bounceIntensity / 4f;
				}
			}
		}
		if (entity.armSwinging && entity.ticks % 5 == 0 && !entity.isSleeping()) {
			this.targetBounceY += (Math.random() > 0.5 ? -0.25f : 0.25f) * bounceIntensity;
		}
		if (entity.isSleeping() && !this.alreadySleeping) {
			this.targetBounceY = bounceIntensity;
			this.alreadySleeping = true;
		}
		if (!entity.isSleeping() && this.alreadySleeping) {
			this.targetBounceY = bounceIntensity;
			this.alreadySleeping = false;
		}

		float percent = entityConfig.getFloppiness();
		float bounceAmount = 0.45f * (1f - percent) + 0.15f;
		bounceAmount = MathHelper.clamp(bounceAmount, 0.15f, 0.6f);
		float delta = 2.25f - bounceAmount;

		float distanceFromMin = Math.abs(bounceVel + 1.5f) * 0.5f;
		float distanceFromMax = Math.abs(bounceVel - 2.65f) * 0.5f;

		if (bounceVel < -0.5f) {
			targetBounceY += distanceFromMin;
		}
		if (bounceVel > 2.5f) {
			targetBounceY -= distanceFromMax;
		}
		if (targetBounceY < -1.5f) targetBounceY = -1.5f;
		if (targetBounceY > 2.5f) targetBounceY = 2.5f;
		if (targetRotVel < -25f) targetRotVel = -25f;
		if (targetRotVel > 25f) targetRotVel = 25f;

		this.velocity = lerp(bounceAmount, this.velocity, (this.targetBounceY - this.bounceVel) * delta);
		this.bounceVel += this.velocity * percent * 1.1625f;

		this.velocityX = lerp(bounceAmount, this.velocityX, (this.targetBounceX - this.bounceVelX) * delta);
		this.bounceVelX += this.velocityX * percent;

		this.rotVelocity = lerp(bounceAmount, this.rotVelocity, (this.targetRotVel - this.bounceRotVel) * delta);
		this.bounceRotVel += this.rotVelocity * percent;

		this.wfg_bounceRotation = this.bounceRotVel;
		this.positionX = this.bounceVelX;
		this.positionY = this.bounceVel;

		if (this.positionY < -0.5f) this.positionY = -0.5f;
		if (this.positionY > 1.5f) {
			this.positionY = 1.5f;
			this.velocity = 0;
		}
	}

	/**
	 * Equivalent to modern {@code MathHelper#lerp(float, float, float)}: interpolates from {@code start} to {@code end}
	 * by {@code delta}.
	 */
	private static float lerp(float delta, float start, float end) {
		return start + delta * (end - start);
	}

	public float getBreastSize(float partialTicks) {
		return lerp(partialTicks, preBreastSize, breastSize);
	}

	public float getPrePositionY() {
		return this.prePositionY;
	}

	public float getPositionY() {
		return this.positionY;
	}

	public float getPrePositionX() {
		return this.prePositionX;
	}

	public float getPositionX() {
		return this.positionX;
	}

	public float getBounceRotation() {
		return this.wfg_bounceRotation;
	}

	public float getPreBounceRotation() {
		return this.wfg_preBounceRotation;
	}

	private int clampMovement(float movement) {
		int val = (int) (10 - movement * 2f);
		if (val < 1) val = 1;
		return val;
	}
}

package com.wildfire.mixins;

import com.wildfire.main.entitydata.IModelPartsHolder;

import net.minecraft.entity.data.DataAttribute;
import net.minecraft.entity.living.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityAccessorMixin implements IModelPartsHolder {

	@Shadow
	protected static DataAttribute<Byte> MODEL_PARTS;

	@Unique
	@Override
	public int wildfiregender$getModelParts() {
		return ((PlayerEntity) (Object) this).getSyncedData().get(MODEL_PARTS);
	}
}

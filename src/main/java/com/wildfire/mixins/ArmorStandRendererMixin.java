package com.wildfire.mixins;

import com.wildfire.render.GenderArmorLayer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.ArmorStandRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.model.Model;
import net.minecraft.entity.living.ArmorStandEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ArmorStandRenderer.class)
public abstract class ArmorStandRendererMixin extends LivingEntityRenderer<ArmorStandEntity> {

	public ArmorStandRendererMixin(EntityRenderDispatcher dispatcher, Model model, float shadow) {
		super(dispatcher, model, shadow);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void wildfiregender$armorStandBreastArmor(EntityRenderDispatcher dispatcher, CallbackInfo ci) {
		this.addLayer(new GenderArmorLayer<ArmorStandEntity>(this));
	}
}

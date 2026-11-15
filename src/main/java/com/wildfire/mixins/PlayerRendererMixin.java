package com.wildfire.mixins;

import com.wildfire.render.GenderArmorLayer;
import com.wildfire.render.GenderLayer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.entity.living.player.ClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerRenderer;
import net.minecraft.client.render.model.Model;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<ClientPlayerEntity> {

	public PlayerRendererMixin(EntityRenderDispatcher dispatcher, Model model, float shadow) {
		super(dispatcher, model, shadow);
	}

	@Inject(method = "<init>(Lnet/minecraft/client/render/entity/EntityRenderDispatcher;Z)V", at = @At("TAIL"))
	private void wildfiregender$addBreastLayers(EntityRenderDispatcher dispatcher, boolean thinArms, CallbackInfo ci) {
		this.addLayer(new GenderLayer<ClientPlayerEntity>(this));
		this.addLayer(new GenderArmorLayer<ClientPlayerEntity>(this));
	}
}

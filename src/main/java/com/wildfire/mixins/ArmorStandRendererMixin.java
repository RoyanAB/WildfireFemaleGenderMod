package com.wildfire.mixins;

import com.wildfire.render.GenderArmorLayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.ArmorStandRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.layer.ItemInHandLayer;
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
		// Same ordering as players: after the vanilla armor layer, before the held-item layer.
		int index = 1;
		for (int i = 0; i < this.layers.size(); i++) {
			if (this.layers.get(i).getClass() == ItemInHandLayer.class) {
				index = i;
				break;
			}
		}
		this.layers.add(index, new GenderArmorLayer<ArmorStandEntity>(this));
	}
}

package com.wildfire.mixins;

import com.wildfire.render.GenderArmorLayer;
import com.wildfire.render.GenderLayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.entity.living.player.ClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerRenderer;
import net.minecraft.client.render.entity.layer.ItemInHandLayer;
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
		// Insert between the vanilla armor layer and the held-item layer, exactly like a vanilla layer.
		// Item renderers (vanilla or other mods) call Lighting.turnOn()/turnOff() with the entity matrix
		// still active and leave the global lighting/lightmap state changed; rendering after them made the
		// breasts use the item's lighting instead of the entity's. Rendering before them keeps the same
		// clean state the vanilla armor layer uses, without any preview-specific handling.
		int index = 1;
		for (int i = 0; i < this.layers.size(); i++) {
			if (this.layers.get(i).getClass() == ItemInHandLayer.class) {
				index = i;
				break;
			}
		}
		this.layers.add(index, new GenderLayer<ClientPlayerEntity>(this));
		this.layers.add(index + 1, new GenderArmorLayer<ClientPlayerEntity>(this));
	}

}

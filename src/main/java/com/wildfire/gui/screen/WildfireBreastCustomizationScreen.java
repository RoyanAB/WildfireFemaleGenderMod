package com.wildfire.gui.screen;

import com.wildfire.gui.EntityPreview;
import com.wildfire.gui.GuiHelper;
import com.wildfire.gui.WildfireButton;
import com.wildfire.gui.WildfireSlider;
import com.wildfire.main.config.Configuration;
import com.wildfire.main.entitydata.Breasts;
import com.wildfire.main.entitydata.PlayerConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.living.player.PlayerEntity;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class WildfireBreastCustomizationScreen extends BaseWildfireScreen {

	private WildfireSlider breastSlider, xOffsetBoobSlider, yOffsetBoobSlider, zOffsetBoobSlider, cleavageSlider;
	private WildfireButton btnDualPhysics;

	public WildfireBreastCustomizationScreen(Screen parent, UUID uuid) {
		super(I18n.translate("wildfire_gender.appearance_settings.title"), parent, uuid);
	}

	@Override
	public void init() {
		PlayerConfig plr = getPlayer();
		if (plr == null) return;
		int j = this.height / 2 - 11;

		final Breasts breasts = plr.getBreasts();
		final Runnable onSave = new Runnable() {
			@Override
			public void run() {
				PlayerConfig player = getPlayer();
				if (player != null) PlayerConfig.saveGenderInfo(player);
			}
		};

		this.addButton(new WildfireButton(this.width / 2 + 178, j - 72, 9, 9, "X", new Runnable() {
			@Override
			public void run() {
				Minecraft.getInstance().openScreen(parent);
			}
		}));

		this.addButton(this.breastSlider = new WildfireSlider(this.width / 2 + 30, j - 48, 158, 20, Configuration.BUST_SIZE, plr.getBustSize(),
			plr::updateBustSize, value -> I18n.translate("wildfire_gender.wardrobe.slider.breast_size", Integer.valueOf(Math.round(value * 1.25f * 100))),
			value -> onSave.run()));

		this.addButton(this.xOffsetBoobSlider = new WildfireSlider(this.width / 2 + 30, j - 27, 158, 20, Configuration.BREASTS_OFFSET_X, breasts.getXOffset(),
			breasts::updateXOffset,
			value -> I18n.translate("wildfire_gender.wardrobe.slider.separation", Integer.valueOf(Math.round((Math.round(value * 100f) / 100f) * 10))),
			value -> onSave.run()));
		this.addButton(this.yOffsetBoobSlider = new WildfireSlider(this.width / 2 + 30, j - 6, 158, 20, Configuration.BREASTS_OFFSET_Y, breasts.getYOffset(),
			breasts::updateYOffset,
			value -> I18n.translate("wildfire_gender.wardrobe.slider.height", Integer.valueOf(Math.round((Math.round(value * 100f) / 100f) * 10))),
			value -> onSave.run()));
		this.addButton(this.zOffsetBoobSlider = new WildfireSlider(this.width / 2 + 30, j + 15, 158, 20, Configuration.BREASTS_OFFSET_Z, breasts.getZOffset(),
			breasts::updateZOffset,
			value -> I18n.translate("wildfire_gender.wardrobe.slider.depth", Integer.valueOf(Math.round((Math.round(value * 100f) / 100f) * 10))),
			value -> onSave.run()));

		this.addButton(this.cleavageSlider = new WildfireSlider(this.width / 2 + 30, j + 36, 158, 20, Configuration.BREASTS_CLEAVAGE, breasts.getCleavage(),
			breasts::updateCleavage,
			value -> I18n.translate("wildfire_gender.wardrobe.slider.rotation", Integer.valueOf(Math.round((Math.round(value * 100f) / 100f) * 100))),
			value -> onSave.run()));

		this.addButton(this.btnDualPhysics = new WildfireButton(this.width / 2 + 30, j + 57, 158, 20,
			I18n.translate("wildfire_gender.breast_customization.dual_physics",
				I18n.translate(breasts.isUniboob() ? "wildfire_gender.label.no" : "wildfire_gender.label.yes")), new Runnable() {
			@Override
			public void run() {
				boolean isUniboob = !breasts.isUniboob();
				if (breasts.updateUniboob(isUniboob)) {
					btnDualPhysics.message = I18n.translate("wildfire_gender.breast_customization.dual_physics",
						I18n.translate(isUniboob ? "wildfire_gender.label.no" : "wildfire_gender.label.yes"));
					PlayerConfig player = getPlayer();
					if (player != null) PlayerConfig.saveGenderInfo(player);
				}
			}
		}));

		this.updateContent();
		super.init();
	}

	private void updateContent() {
		if (breastSlider == null) return;
		boolean canHaveBreasts = getPlayer() != null && getPlayer().getGender().canHaveBreasts();
		breastSlider.visible = canHaveBreasts;
		xOffsetBoobSlider.visible = canHaveBreasts;
		yOffsetBoobSlider.visible = canHaveBreasts;
		zOffsetBoobSlider.visible = canHaveBreasts;
		cleavageSlider.visible = canHaveBreasts;
		btnDualPhysics.visible = canHaveBreasts;
	}

	@Override
	public void renderBackground() {
		super.renderBackground();
		int x = this.width / 2;
		int y = this.height / 2;
		fill(x + 28, y - 64 - 21, x + 190, y + 68, 0x55000000);
		fill(x + 29, y - 63 - 21, x + 189, y - 60, 0x55000000);
		GuiHelper.drawText(this.textRenderer, this.title, x + 32, y - 60 - 21, 0xFFFFFF);
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		if (this.minecraft == null || this.minecraft.player == null || this.minecraft.world == null) return;

		this.updateContent();
		this.renderBackground();
		super.render(mouseX, mouseY, tickDelta);

		int xP = this.width / 2 - 102;
		int yP = this.height / 2 + 275;
		PlayerEntity ent = findPlayerEntity();
		if (ent != null) {
			EntityPreview.render(xP, yP, 200, -20, -20, ent);
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int button) {
		if (breastSlider != null) breastSlider.save();
		if (xOffsetBoobSlider != null) xOffsetBoobSlider.save();
		if (yOffsetBoobSlider != null) yOffsetBoobSlider.save();
		if (zOffsetBoobSlider != null) zOffsetBoobSlider.save();
		if (cleavageSlider != null) cleavageSlider.save();
		super.mouseReleased(mouseX, mouseY, button);
	}

	private PlayerEntity findPlayerEntity() {
		if (this.minecraft.world == null) return null;
		for (PlayerEntity player : this.minecraft.world.players) {
			if (player.getUuid().equals(this.playerUUID)) {
				return player;
			}
		}
		return null;
	}
}

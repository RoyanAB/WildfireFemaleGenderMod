package com.wildfire.gui.screen;

import java.util.UUID;

import com.wildfire.gui.GuiHelper;
import com.wildfire.gui.GuiHelper;
import com.wildfire.gui.WildfireBreastPresetList;
import com.wildfire.gui.WildfireButton;
import com.wildfire.gui.WildfireSlider;
import com.wildfire.main.config.BreastPresetConfiguration;
import com.wildfire.main.config.Configuration;
import com.wildfire.main.entitydata.Breasts;
import com.wildfire.main.entitydata.PlayerConfig;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.game.inventory.SurvivalInventoryScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.living.player.PlayerEntity;

@Environment(EnvType.CLIENT)
public class WildfireBreastCustomizationScreen extends BaseWildfireScreen {

	private WildfireSlider breastSlider, xOffsetBoobSlider, yOffsetBoobSlider, zOffsetBoobSlider, cleavageSlider;
	private WildfireButton btnDualPhysics, btnPresets, btnCustomization;
	private WildfireButton btnAddPreset, btnDeletePreset;

	private WildfireBreastPresetList PRESET_LIST;
	private int currentTab = 0;

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

		this.addButton(btnCustomization = new WildfireButton(this.width / 2 + 30, j - 60, 158 / 2 - 1, 10,
				I18n.translate("wildfire_gender.breast_customization.tab_customization"), new Runnable() {
					@Override
					public void run() {
						currentTab = 0;
						btnCustomization.active = false;
						btnPresets.active = true;
						btnAddPreset.visible = false;
						btnDeletePreset.visible = false;
					}
				}));

		this.addButton(btnPresets = new WildfireButton(this.width / 2 + 31 + 158 / 2, j - 60, 158 / 2 - 1, 10,
				I18n.translate("wildfire_gender.breast_customization.tab_presets"), new Runnable() {
					@Override
					public void run() {
						if (!FabricLoader.getInstance().isDevelopmentEnvironment()) return;
						currentTab = 1;
						btnCustomization.active = true;
						btnPresets.active = false;
						btnAddPreset.visible = true;
						btnDeletePreset.visible = true;
						PRESET_LIST.refreshList();
					}
				}));

		this.addButton(btnAddPreset = new WildfireButton(this.width / 2 + 31 + 158 / 2, j + 80, 158 / 2 - 1, 12,
				I18n.translate("wildfire_gender.breast_customization.presets.add_new"), new Runnable() {
					@Override
					public void run() {
						createNewPreset("Test Preset");
					}
				}));
		btnAddPreset.visible = false;

		this.addButton(btnDeletePreset = new WildfireButton(this.width / 2 + 30, j + 80, 158 / 2 - 1, 12,
				I18n.translate("wildfire_gender.breast_customization.presets.delete"), new Runnable() {
					@Override
					public void run() {
					}
				}));
		btnDeletePreset.active = false;
		btnDeletePreset.visible = false;

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

		PRESET_LIST = new WildfireBreastPresetList(this, 156, (j - 48), (j + 77));
		PRESET_LIST.setX(this.width / 2 + 30);

		this.currentTab = 0;
		btnCustomization.active = false;

		if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
			btnPresets.withTooltip(I18n.translate("wildfire_gender.coming_soon"));
		}

		super.init();
	}

	private void createNewPreset(String presetName) {
		BreastPresetConfiguration cfg = new BreastPresetConfiguration(presetName);
		cfg.set(BreastPresetConfiguration.PRESET_NAME, presetName);
		cfg.set(BreastPresetConfiguration.BUST_SIZE, this.getPlayer().getBustSize());
		cfg.set(BreastPresetConfiguration.BREASTS_UNIBOOB, this.getPlayer().getBreasts().isUniboob());
		cfg.set(BreastPresetConfiguration.BREASTS_CLEAVAGE, this.getPlayer().getBreasts().getCleavage());
		cfg.set(BreastPresetConfiguration.BREASTS_OFFSET_X, this.getPlayer().getBreasts().getXOffset());
		cfg.set(BreastPresetConfiguration.BREASTS_OFFSET_Y, this.getPlayer().getBreasts().getYOffset());
		cfg.set(BreastPresetConfiguration.BREASTS_OFFSET_Z, this.getPlayer().getBreasts().getZOffset());
		cfg.save();

		PRESET_LIST.refreshList();
	}

	private void updatePresetTab() {
		boolean canHaveBreasts = getPlayer() != null && getPlayer().getGender().canHaveBreasts();
		breastSlider.visible = canHaveBreasts && currentTab == 0;
		xOffsetBoobSlider.visible = canHaveBreasts && currentTab == 0;
		yOffsetBoobSlider.visible = canHaveBreasts && currentTab == 0;
		zOffsetBoobSlider.visible = canHaveBreasts && currentTab == 0;
		cleavageSlider.visible = canHaveBreasts && currentTab == 0;
		btnDualPhysics.visible = canHaveBreasts && currentTab == 0;
		PRESET_LIST.setListVisible(currentTab == 1);
	}

	@Override
	public void renderBackground() {
		super.renderBackground();
		int x = this.width / 2;
		int y = this.height / 2;
		fill(x + 28, y - 64 - 21, x + 190, y + 68, 0x55000000);
		fill(x + 29, y - 63 - 21, x + 189, y - 60, 0x55000000);
		GuiHelper.drawText(this.textRenderer, this.title, x + 32, y - 60 - 21, 0xFFFFFF);
		if (currentTab == 1) {
			fill(PRESET_LIST.getLeft(), PRESET_LIST.getTop(), PRESET_LIST.getRight(), PRESET_LIST.getBottom(), 0x55000000);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		if (this.minecraft == null || this.minecraft.player == null || this.minecraft.world == null) return;

		updatePresetTab();
		this.renderBackground();
		super.render(mouseX, mouseY, tickDelta);

		int xP = this.width / 2 - 102;
		int yP = this.height / 2 + 275;
		PlayerEntity ent = findPlayerEntity();
		if (ent != null) {
			SurvivalInventoryScreen.renderEntity(xP, yP, 200, -20, -20, ent);
		}

		int x = this.width / 2;
		int y = this.height / 2;
		if (currentTab == 1) {
			PRESET_LIST.render(mouseX, mouseY, tickDelta);
			if (PRESET_LIST.getPresetList().length == 0) {
				String noPresets = "No Presets Found";
				GuiHelper.drawText(this.textRenderer, noPresets, x + ((190 + 28) / 2) - this.textRenderer.getWidth(noPresets) / 2, y - 4, 0xFFFFFF);
			}
		}

		this.renderButtonTooltips(mouseX, mouseY);
	}

	@Override
	public void handleMouse() {
		super.handleMouse();
		if (PRESET_LIST != null) {
			PRESET_LIST.handleMouse();
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		if (currentTab == 1 && PRESET_LIST != null && PRESET_LIST.mouseClicked(mouseX, mouseY, button)) {
			return;
		}
		super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int button) {
		if (currentTab == 1 && PRESET_LIST != null) {
			PRESET_LIST.mouseReleased(mouseX, mouseY, button);
		}
		breastSlider.save();
		xOffsetBoobSlider.save();
		yOffsetBoobSlider.save();
		zOffsetBoobSlider.save();
		cleavageSlider.save();
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

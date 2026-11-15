package com.wildfire.gui.screen;

import com.wildfire.gui.GuiHelper;
import com.wildfire.gui.WildfireButton;
import com.wildfire.gui.WildfireSlider;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.config.Configuration;
import com.wildfire.main.entitydata.PlayerConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.resource.Identifier;
import net.minecraft.text.Formatting;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class WildfireCharacterSettingsScreen extends BaseWildfireScreen {

	private static final String ENABLED = Formatting.GREEN + I18n.translate("wildfire_gender.label.enabled") + Formatting.RESET;
	private static final String DISABLED = Formatting.RED + I18n.translate("wildfire_gender.label.disabled") + Formatting.RESET;
	private static final Identifier BACKGROUND = new Identifier(WildfireGender.MODID, "textures/gui/settings_bg.png");

	private WildfireSlider bounceSlider, floppySlider;
	private WildfireButton btnPhysics, btnHideInArmor, btnArmorPhysicsOverride;
	private boolean bounceWarning;

	public WildfireCharacterSettingsScreen(Screen parent, UUID uuid) {
		super(I18n.translate("wildfire_gender.char_settings.title"), parent, uuid);
	}

	@Override
	public void init() {
		PlayerConfig aPlr = getPlayer();
		if (aPlr == null) return;
		int x = this.width / 2;
		int y = this.height / 2;
		int yPos = y - 47;
		int xPos = x - 156 / 2 - 1;

		this.addButton(new WildfireButton(this.width / 2 + 73, yPos - 11, 9, 9, "X", () -> Minecraft.getInstance().openScreen(parent)));

		this.addButton(btnPhysics = new WildfireButton(xPos, yPos, 157, 20,
			I18n.translate("wildfire_gender.char_settings.physics", aPlr.hasBreastPhysics() ? ENABLED : DISABLED), () -> {
			PlayerConfig player = getPlayer();
			if (player == null) return;
			boolean enablePhysics = !player.hasBreastPhysics();
			if (player.updateBreastPhysics(enablePhysics)) {
				btnPhysics.message = I18n.translate("wildfire_gender.char_settings.physics", enablePhysics ? ENABLED : DISABLED);
				PlayerConfig.saveGenderInfo(player);
			}
		}));

		this.addButton(btnHideInArmor = new WildfireButton(xPos, yPos + 20, 157, 20,
			I18n.translate("wildfire_gender.char_settings.hide_in_armor", aPlr.showBreastsInArmor() ? DISABLED : ENABLED), () -> {
			PlayerConfig player = getPlayer();
			if (player == null) return;
			boolean enableShowInArmor = !player.showBreastsInArmor();
			if (player.updateShowBreastsInArmor(enableShowInArmor)) {
				btnHideInArmor.message = I18n.translate("wildfire_gender.char_settings.hide_in_armor", enableShowInArmor ? DISABLED : ENABLED);
				PlayerConfig.saveGenderInfo(player);
			}
		}));

		this.addButton(btnArmorPhysicsOverride = new WildfireButton(xPos, yPos + 40, 157, 20,
			I18n.translate("wildfire_gender.char_settings.override_armor_physics", aPlr.getArmorPhysicsOverride() ? ENABLED : DISABLED), () -> {
			PlayerConfig player = getPlayer();
			if (player == null) return;
			boolean enableArmorPhysicsOverride = !player.getArmorPhysicsOverride();
			if (player.updateArmorPhysicsOverride(enableArmorPhysicsOverride)) {
				btnArmorPhysicsOverride.message = I18n.translate("wildfire_gender.char_settings.override_armor_physics",
					player.getArmorPhysicsOverride() ? ENABLED : DISABLED);
				PlayerConfig.saveGenderInfo(player);
			}
		}).withTooltip(I18n.translate("wildfire_gender.tooltip.override_armor_physics.line1") + "\n\n"
			+ I18n.translate("wildfire_gender.tooltip.override_armor_physics.line2")));

		this.addButton(this.bounceSlider = new WildfireSlider(xPos, yPos + 60, 158, 20, Configuration.BOUNCE_MULTIPLIER, aPlr.getBounceMultiplier(), value -> {
		}, value -> {
			float bounceText = 3 * value;
			int v = Math.round(bounceText * 100);
			bounceWarning = v > 100;
			return I18n.translate("wildfire_gender.slider.bounce", v);
		}, value -> {
			PlayerConfig player = getPlayer();
			if (player != null && player.updateBounceMultiplier(value)) {
				PlayerConfig.saveGenderInfo(player);
			}
		}));

		this.addButton(this.floppySlider = new WildfireSlider(xPos, yPos + 80, 158, 20, Configuration.FLOPPY_MULTIPLIER, aPlr.getFloppiness(), value -> {
		}, value -> I18n.translate("wildfire_gender.slider.floppy", Math.round(value * 100)), value -> {
			PlayerConfig player = getPlayer();
			if (player != null && player.updateFloppiness(value)) {
				PlayerConfig.saveGenderInfo(player);
			}
		}));

		super.init();
	}

	@Override
	public void renderBackground() {
		super.renderBackground();
		this.minecraft.getTextureManager().bind(BACKGROUND);
		GlStateManager.color4f(1f, 1f, 1f, 1f);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		this.drawTexture((this.width - 172) / 2, (this.height - 124) / 2, 0, 0, 172, 144);
		GlStateManager.disableBlend();
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		if (this.minecraft.world == null) return;
		this.renderBackground();
		super.render(mouseX, mouseY, tickDelta);
		int x = this.width / 2;
		int y = this.height / 2;
		int yPos = y - 47;
		GuiHelper.drawText(this.textRenderer, this.title, x - 79, yPos - 10, 4473924);
		PlayerEntity plrEntity = findPlayerEntity();
		if (plrEntity != null) {
			GuiHelper.drawCenteredText(this.textRenderer, plrEntity.getDisplayName().getFormattedString(), x, yPos - 30, 0xFFFFFF);
		}

		if (bounceWarning) {
			GuiHelper.drawCenteredText(this.textRenderer, Formatting.ITALIC + I18n.translate("wildfire_gender.tooltip.bounce_warning"),
				x, y + 90, 0xFF6666);
		}

		this.renderButtonTooltips(mouseX, mouseY);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int button) {
		if (bounceSlider != null) bounceSlider.save();
		if (floppySlider != null) floppySlider.save();
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

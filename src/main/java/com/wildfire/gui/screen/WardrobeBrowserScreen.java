package com.wildfire.gui.screen;

import java.util.Calendar;
import java.util.UUID;

import com.wildfire.gui.GuiHelper;
import com.wildfire.gui.WildfireButton;
import com.wildfire.main.Gender;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.entitydata.PlayerConfig;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.game.inventory.SurvivalInventoryScreen;
import net.minecraft.client.network.PlayerInfo;
import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.resource.Identifier;
import net.minecraft.text.Formatting;

@Environment(EnvType.CLIENT)
public class WardrobeBrowserScreen extends BaseWildfireScreen {

	private static final Identifier BACKGROUND_FEMALE = new Identifier(WildfireGender.MODID, "textures/gui/wardrobe_bg2.png");
	private static final Identifier BACKGROUND = new Identifier(WildfireGender.MODID, "textures/gui/wardrobe_bg3.png");
	private static final Identifier TXTR_RIBBON = new Identifier(WildfireGender.MODID, "textures/bc_ribbon.png");
	private static final UUID CREATOR_UUID = UUID.fromString("23b6feed-2dfe-4f2e-9429-863fd4adb946");
	private static final boolean IS_BREAST_CANCER_AWARENESS_MONTH = Calendar.getInstance().get(Calendar.MONTH) == Calendar.OCTOBER;

	public WardrobeBrowserScreen(Screen parent, UUID uuid) {
		super(I18n.translate("wildfire_gender.wardrobe.title"), parent, uuid);
	}

	@Override
	public void init() {
		int y = this.height / 2;
		PlayerConfig plr = getPlayer();
		if (plr == null) return;

		this.addButton(new WildfireButton(this.width / 2 - 42, y - 52, 158, 20, getGenderLabel(plr.getGender()), new Runnable() {
			@Override
			public void run() {
				PlayerConfig player = getPlayer();
				if (player == null) return;
				Gender gender;
				switch (player.getGender()) {
					case MALE:
						gender = Gender.FEMALE;
						break;
					case FEMALE:
						gender = Gender.OTHER;
						break;
					default:
						gender = Gender.MALE;
						break;
				}
				if (player.updateGender(gender)) {
					PlayerConfig.saveGenderInfo(player);
					Minecraft.getInstance().openScreen(new WardrobeBrowserScreen(parent, playerUUID));
				}
			}
		}));

		if (plr.getGender().canHaveBreasts()) {
			this.addButton(new WildfireButton(this.width / 2 - 42, y - 32, 158, 20, I18n.translate("wildfire_gender.appearance_settings.title") + "...", new Runnable() {
				@Override
				public void run() {
					Minecraft.getInstance().openScreen(new WildfireBreastCustomizationScreen(WardrobeBrowserScreen.this, playerUUID));
				}
			}));
		}
		this.addButton(new WildfireButton(this.width / 2 - 42, y - (plr.getGender().canHaveBreasts() ? 12 : 32), 158, 20,
				I18n.translate("wildfire_gender.char_settings.title") + "...", new Runnable() {
					@Override
					public void run() {
						Minecraft.getInstance().openScreen(new WildfireCharacterSettingsScreen(WardrobeBrowserScreen.this, playerUUID));
					}
				}));

		this.addButton(new WildfireButton(this.width / 2 + 111, y - 63, 9, 9, "X", new Runnable() {
			@Override
			public void run() {
				Minecraft.getInstance().openScreen(parent);
			}
		}));

		super.init();
	}

	private String getGenderLabel(Gender gender) {
		return I18n.translate("wildfire_gender.label.gender") + " - " + GuiHelper.getGenderDisplayName(gender);
	}

	@Override
	public void renderBackground() {
		super.renderBackground();
		PlayerConfig plr = getPlayer();
		Identifier backgroundTexture = plr != null && plr.getGender().canHaveBreasts() ? BACKGROUND_FEMALE : BACKGROUND;
		this.minecraft.getTextureManager().bind(backgroundTexture);
		GlStateManager.color4f(1f, 1f, 1f, 1f);
		this.drawTexture((this.width - 248) / 2, (this.height - 134) / 2, 0, 0, 248, 156);
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		super.render(mouseX, mouseY, tickDelta);

		int x = this.width / 2;
		int y = this.height / 2;
		GuiHelper.drawText(this.textRenderer, this.title, x - 118, y - 62, 4473924);

		if (this.minecraft.world != null) {
			int xP = this.width / 2 - 82;
			int yP = this.height / 2 + 40;
			PlayerEntity ent = findPlayerEntity();
			if (ent != null) {
				SurvivalInventoryScreen.renderEntity(xP, yP, 45, (float) (xP - mouseX), (float) (yP - 76 - mouseY), ent);
			}
		}

		if (this.minecraft.getNetworkHandler() != null && this.minecraft.player != null) {
			boolean withCreator = false;
			for (PlayerInfo info : this.minecraft.getNetworkHandler().getOnlinePlayers()) {
				if (info.getProfile().getId().equals(CREATOR_UUID)) {
					withCreator = true;
					break;
				}
			}
			if (withCreator) {
				int creatorY = y + 65;
				if (IS_BREAST_CANCER_AWARENESS_MONTH) creatorY += 30;
				GuiHelper.drawCenteredText(this.textRenderer, I18n.translate("wildfire_gender.label.with_creator"), this.width / 2, creatorY, 0xFF00FF);
			}
		}

		if (IS_BREAST_CANCER_AWARENESS_MONTH) {
			int bcaY = y - 45;
			fill(x - 159, bcaY + 106, x + 159, bcaY + 136, 0x55000000);
			GuiHelper.drawTextWithShadow(this.textRenderer, Formatting.BOLD.toString() + Formatting.ITALIC + I18n.translate("wildfire_gender.cancer_awareness.title"),
					this.width / 2 - 148, bcaY + 117, 0xFFFFFF);
			this.minecraft.getTextureManager().bind(TXTR_RIBBON);
			GlStateManager.color4f(1f, 1f, 1f, 1f);
			drawTexture(x + 130, bcaY + 109, 0, 0, 20, 20, 26, 26, 20, 20);
		}
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

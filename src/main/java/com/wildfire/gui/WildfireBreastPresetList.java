package com.wildfire.gui;

import java.util.ArrayList;
import java.util.List;

import com.wildfire.gui.screen.WildfireBreastCustomizationScreen;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.config.BreastPresetConfiguration;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.resource.Identifier;

@Environment(EnvType.CLIENT)
public class WildfireBreastPresetList extends EntryListWidget {

	public class BreastPresetListEntry {

		public Identifier ident;
		public String name;
		private BreastPresetConfiguration data;

		public BreastPresetListEntry(String name, BreastPresetConfiguration data) {
			this.name = name;
			this.data = data;
			this.ident = new Identifier(WildfireGender.MODID, "textures/presets/iknowthisisnull.png");
		}
	}

	private BreastPresetListEntry[] BREAST_PRESETS = new BreastPresetListEntry[0];
	public boolean active = true;
	private final List<Entry> entries = new ArrayList<Entry>();
	private final int listWidth;
	private final WildfireBreastCustomizationScreen parent;

	public WildfireBreastPresetList(WildfireBreastCustomizationScreen parent, int listWidth, int top, int bottom) {
		super(Minecraft.getInstance(), 156, parent.height, top, bottom, 32);
		this.setHeader(false, 0);
		this.setRenderSelectionHighlight(false);
		this.parent = parent;
		this.listWidth = listWidth;
		this.refreshList();
	}

	public BreastPresetListEntry[] getPresetList() {
		return BREAST_PRESETS;
	}

	@Override
	protected int size() {
		return entries.size();
	}

	@Override
	public EntryListWidget.Entry getEntry(int index) {
		return entries.get(index);
	}

	@Override
	public int getRowWidth() {
		return this.listWidth;
	}

	@Override
	protected int getScrollbarPosition() {
		return this.parent.width / 2 + 181;
	}

	public void refreshList() {
		this.entries.clear();

		BreastPresetConfiguration[] CONFIGS = BreastPresetConfiguration.getBreastPresetConfigurationFiles();
		ArrayList<BreastPresetListEntry> tmpPresets = new ArrayList<BreastPresetListEntry>();
		for (BreastPresetConfiguration presetCfg : CONFIGS) {
			tmpPresets.add(new BreastPresetListEntry(presetCfg.get(BreastPresetConfiguration.PRESET_NAME), presetCfg));
		}
		BREAST_PRESETS = tmpPresets.toArray(new BreastPresetListEntry[tmpPresets.size()]);

		if (this.minecraft.world == null || this.minecraft.player == null) return;

		for (int i = 0; i < BREAST_PRESETS.length; i++) {
			entries.add(new Entry(BREAST_PRESETS[i]));
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		if (!this.visible) return;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.capScrolling();
		int x = this.minX + this.width / 2 - this.getRowWidth() / 2 + 2;
		int y = this.minY + 4 - this.getScrollAmount();
		this.renderList(x, y, mouseX, mouseY, tickDelta);
	}

	public void setListVisible(boolean visible) {
		this.visible = visible;
	}

	public int getLeft() {
		return minX;
	}

	public int getRight() {
		return minX + width;
	}

	public int getTop() {
		return minY;
	}

	public int getBottom() {
		return maxY;
	}

	@Environment(EnvType.CLIENT)
	public class Entry implements EntryListWidget.Entry {
		private final Identifier thumbnail;
		public final BreastPresetListEntry nInfo;
		private final WildfireButton btnOpenGUI;

		private Entry(final BreastPresetListEntry nInfo) {
			this.nInfo = nInfo;
			this.thumbnail = nInfo.ident;
			btnOpenGUI = new WildfireButton(0, 0, getRowWidth() - 6, entryHeight, "", new Runnable() {
				@Override
				public void run() {
					parent.getPlayer().updateBustSize(nInfo.data.get(BreastPresetConfiguration.BUST_SIZE));
					parent.getPlayer().getBreasts().updateXOffset(nInfo.data.get(BreastPresetConfiguration.BREASTS_OFFSET_X));
					parent.getPlayer().getBreasts().updateYOffset(nInfo.data.get(BreastPresetConfiguration.BREASTS_OFFSET_Y));
					parent.getPlayer().getBreasts().updateZOffset(nInfo.data.get(BreastPresetConfiguration.BREASTS_OFFSET_Z));
					parent.getPlayer().getBreasts().updateCleavage(nInfo.data.get(BreastPresetConfiguration.BREASTS_CLEAVAGE));
					parent.getPlayer().getBreasts().updateUniboob(nInfo.data.get(BreastPresetConfiguration.BREASTS_UNIBOOB));
				}
			});
		}

		@Override
		public void renderOutOfBounds(int index, int x, int y, float tickDelta) {
		}

		@Override
		public void render(int index, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float partialTicks) {
			if (!visible) return;

			TextRenderer font = Minecraft.getInstance().textRenderer;
			Minecraft.getInstance().getTextureManager().bind(thumbnail);
			GlStateManager.color4f(1f, 1f, 1f, 1f);
			GuiElement.drawTexture(x + 2, y + 2, 0, 0, 28, 28, 28, 28);

			font.draw(nInfo.name, x + 34, y + 4, 0xFFFFFF, false);
			this.btnOpenGUI.x = x;
			this.btnOpenGUI.y = y;
			this.btnOpenGUI.render(Minecraft.getInstance(), mouseX, mouseY, partialTicks);
		}

		@Override
		public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY) {
			if (active && visible) {
				if (this.btnOpenGUI.mouseClicked(Minecraft.getInstance(), mouseX, mouseY)) {
					this.btnOpenGUI.runAction();
					return true;
				}
				return true;
			}
			return false;
		}

		@Override
		public void mouseReleased(int index, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY) {
		}
	}
}

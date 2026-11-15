package com.wildfire.main.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonWriter;

import net.fabricmc.loader.api.FabricLoader;

public class Configuration {

	public static final UUIDConfigKey USERNAME = new UUIDConfigKey("username", UUID.nameUUIDFromBytes("UNKNOWN".getBytes(StandardCharsets.UTF_8)));
	public static final GenderConfigKey GENDER = new GenderConfigKey("gender");
	public static final FloatConfigKey BUST_SIZE = new FloatConfigKey("bust_size", 0.6F, 0, 0.8f);
	public static final BooleanConfigKey HURT_SOUNDS = new BooleanConfigKey("hurt_sounds", true);

	public static final FloatConfigKey BREASTS_OFFSET_X = new FloatConfigKey("breasts_xOffset", 0.0F, -1, 1);
	public static final FloatConfigKey BREASTS_OFFSET_Y = new FloatConfigKey("breasts_yOffset", 0.0F, -1, 1);
	public static final FloatConfigKey BREASTS_OFFSET_Z = new FloatConfigKey("breasts_zOffset", 0.0F, -1, 0);
	public static final BooleanConfigKey BREASTS_UNIBOOB = new BooleanConfigKey("breasts_uniboob", true);
	public static final FloatConfigKey BREASTS_CLEAVAGE = new FloatConfigKey("breasts_cleavage", 0, 0, 0.1F);

	public static final BooleanConfigKey BREAST_PHYSICS = new BooleanConfigKey("breast_physics", true);
	public static final BooleanConfigKey ARMOR_PHYSICS_OVERRIDE = new BooleanConfigKey("armor_physics_override", false);
	public static final BooleanConfigKey SHOW_IN_ARMOR = new BooleanConfigKey("show_in_armor", true);
	public static final FloatConfigKey BOUNCE_MULTIPLIER = new FloatConfigKey("bounce_multiplier", 0.34F, 0, 0.5f);
	public static final FloatConfigKey FLOPPY_MULTIPLIER = new FloatConfigKey("floppy_multiplier", 0.75F, 0.25f, 1);

	private static final TypeAdapter<JsonObject> ADAPTER = new Gson().getAdapter(JsonObject.class);

	private final File CFG_FILE;
	public JsonObject SAVE_VALUES = new JsonObject();

	public Configuration(String saveLoc, String cfgName) {
		Path saveDir = FabricLoader.getInstance().getConfigDir();
		if (!Files.isDirectory(saveDir.resolve(saveLoc))) {
			try {
				Files.createDirectories(saveDir.resolve(saveLoc));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		CFG_FILE = saveDir.resolve(saveLoc).resolve(cfgName + ".json").toFile();
	}

	public void finish() {
		if (CFG_FILE.exists()) {
			load();
			updateConfig();
		}
	}

	public <TYPE> void set(ConfigKey<TYPE> key, TYPE value) {
		key.save(SAVE_VALUES, value);
	}

	public <TYPE> void setDefault(ConfigKey<TYPE> key) {
		if (!SAVE_VALUES.has(key.key)) {
			set(key, key.defaultValue);
		}
	}

	public <TYPE> TYPE get(ConfigKey<TYPE> key) {
		return key.read(SAVE_VALUES);
	}

	public void removeParameter(ConfigKey<?> key) {
		removeParameter(key.key);
	}

	public void removeParameter(String key) {
		SAVE_VALUES.remove(key);
	}

	public void updateConfig() {
		JsonObject obj;
		try (FileReader configurationFile = new FileReader(CFG_FILE)) {
			obj = new Gson().fromJson(configurationFile, JsonObject.class);
			if (obj == null) return;
			for (Map.Entry<String, JsonElement> entry : SAVE_VALUES.entrySet()) {
				obj.add(entry.getKey(), entry.getValue());
			}
		} catch (Exception ignored) {
			return;
		}
		try (FileWriter writer = new FileWriter(CFG_FILE); JsonWriter jsonWriter = new JsonWriter(writer)) {
			ADAPTER.write(jsonWriter, obj);
		} catch (Exception ignored) {
		}
	}

	public void save() {
		try (FileWriter writer = new FileWriter(CFG_FILE); JsonWriter jsonWriter = new JsonWriter(writer)) {
			jsonWriter.setIndent("		");
			ADAPTER.write(jsonWriter, SAVE_VALUES);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void load() {
		try (FileReader configurationFile = new FileReader(CFG_FILE)) {
			JsonObject obj = new Gson().fromJson(configurationFile, JsonObject.class);
			if (obj == null) return;
			for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
				SAVE_VALUES.add(entry.getKey(), entry.getValue());
			}
		} catch (Exception e) {
			// no config file yet, or it's corrupt; defaults will be used
		}
	}
}

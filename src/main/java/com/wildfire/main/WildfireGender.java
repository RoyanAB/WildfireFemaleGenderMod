package com.wildfire.main;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.wildfire.main.entitydata.PlayerConfig;


public class WildfireGender {

	public static final String VERSION = "3.1";
	public static final String MODID = "wildfire_gender";
	public static final Logger LOGGER = LogManager.getLogger("WildfireGender");
	public static final Map<UUID, PlayerConfig> PLAYER_CACHE = new ConcurrentHashMap<UUID, PlayerConfig>();

	private static final ExecutorService IO_WORKER = Executors.newSingleThreadExecutor(new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r, "WildfireGender IO");
			thread.setDaemon(true);
			return thread;
		}
	});

	public static @Nullable PlayerConfig getPlayerById(UUID id) {
		return PLAYER_CACHE.get(id);
	}

	public static @NotNull PlayerConfig getOrAddPlayerById(UUID id) {
		// The integrated server and the client share this cache, so guard against concurrent creation.
		synchronized (PLAYER_CACHE) {
			PlayerConfig config = PLAYER_CACHE.get(id);
			if (config == null) {
				config = new PlayerConfig(id);
				PLAYER_CACHE.put(id, config);
			}
			return config;
		}
	}

	public static Future<Optional<PlayerConfig>> loadGenderInfo(final UUID uuid, final boolean markForSync) {
		return IO_WORKER.submit(new java.util.concurrent.Callable<Optional<PlayerConfig>>() {
			@Override
			public Optional<PlayerConfig> call() {
				return Optional.ofNullable(PlayerConfig.loadCachedPlayer(uuid, markForSync));
			}
		});
	}
}

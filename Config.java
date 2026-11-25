package dev.hitfx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Saved to config/hitfx.json. */
public final class Config {
	public HitParticle hitParticle = HitParticle.MAGIC;
	public HitSound hitSound = HitSound.DING;
	public KillEffect killEffect = KillEffect.TOTEM;
	public Trail trail = Trail.OFF;
	public int particleAmount = 15;

	public boolean hitMarker = true;
	public boolean comboCounter = true;
	public boolean hurtFlash = true;
	public boolean infoHud = true;
	public boolean keystrokes = true;
	public boolean armorHud = true;

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private static Path path() {
		return FabricLoader.getInstance().getConfigDir().resolve("hitfx.json");
	}

	public static Config load() {
		Config config = null;

		try {
			if (Files.exists(path())) {
				config = GSON.fromJson(Files.readString(path()), Config.class);
			}
		} catch (IOException | RuntimeException e) {
			System.err.println("[HitFX] Could not read config, using defaults: " + e);
		}

		if (config == null) config = new Config();
		config.repair();
		return config;
	}

	/** Gson leaves unknown enum names as null, so put defaults back. */
	private void repair() {
		if (hitParticle == null) hitParticle = HitParticle.MAGIC;
		if (hitSound == null) hitSound = HitSound.DING;
		if (killEffect == null) killEffect = KillEffect.TOTEM;
		if (trail == null) trail = Trail.OFF;
		particleAmount = Math.max(5, Math.min(40, particleAmount));
	}

	public void save() {
		try {
			Files.writeString(path(), GSON.toJson(this));
		} catch (IOException e) {
			System.err.println("[HitFX] Could not save config: " + e);
		}
	}
}

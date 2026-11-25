package dev.hitfx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;

import java.util.concurrent.ThreadLocalRandom;

/** Everything here is local: particles and sounds only exist on your screen. */
public final class Effects {
	private Effects() {
	}

	public static void hit(Minecraft client, Entity target, int combo) {
		Config config = HitFxClient.config;

		if (config.hitParticle != HitParticle.OFF) {
			burst(client, target, config.hitParticle.type, config.particleAmount, 0.25);
		}

		if (config.hitSound != HitSound.OFF) {
			// Pitch climbs as your combo grows.
			float pitch = 1.0F + Math.min(combo - 1, 10) * 0.06F;
			play(client, config.hitSound.sound.get(), pitch, 0.5F);
		}
	}

	public static void kill(Minecraft client, Entity target) {
		int amount = HitFxClient.config.particleAmount * 3;

		switch (HitFxClient.config.killEffect) {
			case TOTEM -> {
				burst(client, target, ParticleTypes.TOTEM_OF_UNDYING, amount, 0.5);
				play(client, SoundEvents.PLAYER_LEVELUP, 1.4F, 0.5F);
			}
			case FIREWORK -> {
				burst(client, target, ParticleTypes.FIREWORK, amount, 0.35);
				play(client, SoundEvents.FIREWORK_ROCKET_BLAST, 1.0F, 0.8F);
			}
			case HEARTS -> {
				burst(client, target, ParticleTypes.HEART, amount / 2, 0.2);
				play(client, SoundEvents.AMETHYST_BLOCK_CHIME, 1.2F, 1.0F);
			}
			case SOUL -> {
				burst(client, target, ParticleTypes.SOUL, amount, 0.15);
				burst(client, target, ParticleTypes.SOUL_FIRE_FLAME, amount, 0.2);
			}
			case OFF -> {
			}
		}
	}

	public static void trail(Minecraft client) {
		Trail trail = HitFxClient.config.trail;
		if (trail == Trail.OFF || client.player == null || client.level == null) return;
		if (client.player.isSpectator() || client.player.isInvisible()) return;
		if (client.player.getDeltaMovement().horizontalDistanceSqr() < 0.002) return;
		if (client.player.tickCount % 2 != 0) return;

		ThreadLocalRandom random = ThreadLocalRandom.current();
		client.level.addParticle(trail.type,
				client.player.getX() + random.nextDouble(-0.25, 0.25),
				client.player.getY() + 0.1,
				client.player.getZ() + random.nextDouble(-0.25, 0.25),
				0.0, 0.01, 0.0);
	}

	private static void burst(Minecraft client, Entity target, SimpleParticleType type, int count, double speed) {
		if (client.level == null) return;
		ThreadLocalRandom random = ThreadLocalRandom.current();
		double width = target.getBbWidth();
		double height = target.getBbHeight();

		for (int i = 0; i < count; i++) {
			client.level.addParticle(type,
					target.getX() + random.nextDouble(-0.5, 0.5) * width,
					target.getY() + random.nextDouble(0.2, 1.0) * height,
					target.getZ() + random.nextDouble(-0.5, 0.5) * width,
					random.nextDouble(-speed, speed),
					random.nextDouble(-speed * 0.5, speed),
					random.nextDouble(-speed, speed));
		}
	}

	private static void play(Minecraft client, SoundEvent sound, float pitch, float volume) {
		client.getSoundManager().play(SimpleSoundInstance.forUI(sound, pitch, volume));
	}
}

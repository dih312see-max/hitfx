package dev.hitfx;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Watches entities you attack. Effects fire only once the target actually shows
 * it was hurt, so hits the server rejects stay quiet.
 */
public final class HitTracker {
	private static final int CONFIRM_TICKS = 20;
	private static final int KILL_TICKS = 60;
	private static final int COMBO_TICKS = 60;

	private static final class Watched {
		final LivingEntity entity;
		int lastHurtTime;
		int confirmTicks;
		int killTicks;

		Watched(LivingEntity entity) {
			this.entity = entity;
			this.lastHurtTime = entity.hurtTime;
		}
	}

	private static final Map<Integer, Watched> watched = new HashMap<>();
	private static int comboTimer;
	private static int lastOwnHurtTime;

	public static int combo;
	public static int bestCombo;
	public static int hits;
	public static int kills;
	public static int markerTicks;
	public static int flashTicks;

	private HitTracker() {
	}

	public static void onAttack(LivingEntity target) {
		if (target.isDeadOrDying()) return;
		Watched entry = watched.computeIfAbsent(target.getId(), id -> new Watched(target));
		entry.confirmTicks = CONFIRM_TICKS;
	}

	public static void tick(Minecraft client) {
		if (client.player == null || client.level == null) return;

		if (markerTicks > 0) markerTicks--;
		if (flashTicks > 0) flashTicks--;
		if (comboTimer > 0 && --comboTimer == 0) combo = 0;

		int ownHurtTime = client.player.hurtTime;

		if (ownHurtTime > lastOwnHurtTime) {
			flashTicks = 10;
			combo = 0;
			comboTimer = 0;
		}

		lastOwnHurtTime = ownHurtTime;

		Iterator<Watched> iterator = watched.values().iterator();

		while (iterator.hasNext()) {
			Watched entry = iterator.next();
			LivingEntity target = entry.entity;

			if (entry.confirmTicks > 0) {
				entry.confirmTicks--;

				if (target.hurtTime > entry.lastHurtTime) {
					entry.confirmTicks = 0;
					entry.killTicks = KILL_TICKS;
					hits++;
					combo++;
					bestCombo = Math.max(bestCombo, combo);
					comboTimer = COMBO_TICKS;
					markerTicks = 6;
					Effects.hit(client, target, combo);
				}
			}

			entry.lastHurtTime = target.hurtTime;

			if (entry.killTicks > 0) {
				entry.killTicks--;

				if (target.isDeadOrDying()) {
					kills++;
					Effects.kill(client, target);
					iterator.remove();
					continue;
				}
			}

			if (target.isRemoved() || (entry.confirmTicks == 0 && entry.killTicks == 0)) {
				iterator.remove();
			}
		}
	}

	public static void reset() {
		watched.clear();
		combo = bestCombo = hits = kills = 0;
		comboTimer = markerTicks = flashTicks = lastOwnHurtTime = 0;
	}
}

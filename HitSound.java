package dev.hitfx;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.util.function.Supplier;

public enum HitSound {
	OFF(null),
	DING(() -> SoundEvents.EXPERIENCE_ORB_PICKUP),
	PLING(() -> SoundEvents.NOTE_BLOCK_PLING.value()),
	BELL(() -> SoundEvents.NOTE_BLOCK_BELL.value()),
	CRYSTAL(() -> SoundEvents.AMETHYST_BLOCK_CHIME),
	BONK(() -> SoundEvents.ANVIL_LAND);

	public final Supplier<SoundEvent> sound;

	HitSound(Supplier<SoundEvent> sound) {
		this.sound = sound;
	}
}
